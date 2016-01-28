package me.stammberger.starcitizeninformer.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.SciApplication;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.models.CommLinkModelContentPart;
import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkContentPartTable;
import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkTable;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.AsyncSubject;
import timber.log.Timber;

/**
 * Helper class to get all the comm links using PkRSS
 * This basically serves as a proxy class to turn PkRSS's output to an observable
 */
public class CommLinkFetcher implements Callback {
    final AsyncSubject<ArrayList<CommLinkModel>> mCommLinkSubject;
    private final Context mContext;
    private long mLatestCommLinkDateInDb = 0;

    public CommLinkFetcher() {
        mContext = SciApplication.getContext();

        // get the latest from Database to check whether we have new comm links
        getLatestCommLinkFromDb()
                .subscribe(new Action1<CommLinkModel>() {
                    @Override
                    public void call(CommLinkModel commLinkModel) {
                        if (commLinkModel != null) {
                            mLatestCommLinkDateInDb = commLinkModel.date;
                        }
                        fetchRSSFeed();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.d("Error retrieving latest comm link from database. Error: %s", throwable.toString());
                        fetchRSSFeed();
                    }
                });

        mCommLinkSubject = AsyncSubject.create();
    }

    /**
     * Start loading of the rss feed
     */
    private void fetchRSSFeed() {
        Timber.d("fetchRSSFeed");
        PkRSS.with(mContext)
                .load("https://robertsspaceindustries.com/comm-link/rss")
                        //.load("http://192.168.178.95:8000/sci_rss.xml")
                        //.load("http://192.168.178.95:8000/sci_rss_two_items.xml") // LAN
                        //.load("http://192.168.178.52:8000/sci_rss_two_items.xml") // wireless
                .callback(this)
                .async();
    }

    /**
     * Fetches latest comm link from local database
     *
     * @return Observable with the comm link
     */
    private Observable<CommLinkModel> getLatestCommLinkFromDb() {
        Query q = Query.builder()
                .table(CommLinkTable.TABLE)
                .orderBy(CommLinkTable.COLUMN_DATE + " DESC")
                .limit(1)
                .build();

        return SciApplication.getInstance().getStorIOSQLite()
                .get()
                .object(CommLinkModel.class)
                .withQuery(q)
                .prepare()
                .asRxObservable();
    }

    /**
     * Fetches all comm links from local database and calls the subject once completed.
     * This is a blocking call.
     */
    private void getAllCommLinksFromDb() {
        Query q = Query.builder()
                .table(CommLinkTable.TABLE)
                .orderBy(CommLinkTable.COLUMN_DATE + " DESC")
                .build();

        List<CommLinkModel> commLinkModel = SciApplication.getInstance().getStorIOSQLite()
                .get()
                .listOfObjects(CommLinkModel.class)
                .withQuery(q)
                .prepare()
                .executeAsBlocking();

        mCommLinkSubject.onNext(new ArrayList<>(commLinkModel));
        mCommLinkSubject.onCompleted();
    }

    /**
     * Called before PkRSS starts loading
     */
    @Override
    public void onPreload() {
        Timber.d("Started loading comm links");
    }


    /**
     * Called when PkRSS has finished loading
     *
     * @param newArticles The loaded articles contained in the feed
     */
    @Override
    public void onLoaded(List<Article> newArticles) {
        Timber.d("PkRSS articles loaded");
        if (newArticles.get(0).getDate() <= mLatestCommLinkDateInDb) {
            Timber.d("No new articles. Skip update and fetch from DB");
            getAllCommLinksFromDb();
            return;
        }

        Observable.from(newArticles)
                .takeWhile(new Func1<Article, Boolean>() {
                    @Override
                    public Boolean call(Article article) {
                        // emits the observable received if its newer than the latest one received from database
                        return article.getDate() > mLatestCommLinkDateInDb;
                    }
                })
                .map(new Func1<Article, CommLinkModel>() {
                    @Override
                    public CommLinkModel call(Article a) {
                        // translate Article to CommLinkModel
                        return getCommLinkModel(a);
                    }
                })
                .toList() // this will replace also the onCompleted call
                .subscribe(new Action1<List<CommLinkModel>>() {
                    @Override
                    public void call(List<CommLinkModel> commLinkModels) {
                        SciApplication.getInstance().getStorIOSQLite()
                                .put()
                                .objects(commLinkModels)
                                .prepare()
                                .asRxObservable()
                                .subscribe(new Action1<PutResults<CommLinkModel>>() {
                                    @Override
                                    public void call(PutResults<CommLinkModel> commLinkModelPutResults) {
                                        Timber.d("Saved %s new comm links to DB.", commLinkModelPutResults.numberOfInserts());
                                        getAllCommLinksFromDb();
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Timber.d("Error putting CommLinkModels into database: \n %s", throwable.getCause().toString());
                                    }
                                });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.d("Error1: %s", throwable.getCause());
                        Timber.d("Stacktrace %s", throwable.getStackTrace());
                        Timber.d("onError: %s", throwable.toString());
                    }
                });
    }

    /**
     * Creates a {@link CommLinkModel} from an PkRSS Article
     *
     * @param a Article to create the CommLinkModel from
     * @return Article's CommlinkModel
     */
    @NonNull
    private CommLinkModel getCommLinkModel(Article a) {
        CommLinkModel m = new CommLinkModel();
        m.sourceUri = a.getSource().toString();
        m.title = a.getTitle();
        m.setContentIds(processContent(a.getSource().toString(), a.getContent()));
        m.date = a.getDate();
        m.description = a.getDescription();

        List<String> tags = a.getTags();
        for (int i1 = 0; i1 < tags.size(); i1++) {
            String s = tags.get(i1);
            if (i1 + 1 < tags.size()) {
                if (m.tags == null) {
                    m.tags = s + CommLinkModel.DATA_SEPARATOR;
                } else {
                    m.tags += s + CommLinkModel.DATA_SEPARATOR;
                }
            } else {
                if (m.tags == null) {
                    m.tags = s;
                } else {
                    m.tags += s;
                }
            }
        }

        if (a.getMediaContent().size() > 0) {
            m.backdropUrl = a.getMediaContent().get(0).getUrl();
        }
        return m;
    }

    /**
     * Splits the RSS content into several parts to make it easier to work with and
     * display in a visually pleasing way within a RecyclerView.
     *
     * @param sourceUrl The comm link url these parts belong to
     * @param content   Content string directly from the RSS feed
     * @return {@link CommLinkModelContentPart} {@link ArrayList} Holding all parsed data
     */
    private ArrayList<CommLinkModelContentPart> processContent(String sourceUrl, String content) {
        ArrayList<CommLinkModelContentPart> parts = new ArrayList<>();
        if (content == null) {
            Timber.d("Content null at: %s", sourceUrl);
            return parts;
        }


        ArrayList<String> currentSlideShowLinks = new ArrayList<>();
        String splitDelimiter = "<a class=\"image  js-open-in-slideshow\" data-source_url=\"";

        String[] split = content.split(splitDelimiter);
        for (String s : split) {
            if (s.startsWith("https://")) {
                String[] linkSplit = s.split("\" rel=\"post\"><img src=\"");

                if (linkSplit.length > 2) {
                    // In theory this should never happen. But as I don't have control over the source
                    // it's better to check.
                    Timber.d("Split size > 2: %s", sourceUrl);
                }


                String img = linkSplit[0];
                currentSlideShowLinks.add(img);

                String[] a = null;

                if (linkSplit.length > 1) {
                    String remainder = linkSplit[1];
                    a = remainder.trim().split("\" alt=\"\" /></a>");
                }

                if (a != null && a.length > 1) {
                    if (currentSlideShowLinks.size() > 0) {
                        CommLinkModelContentPart clcp = new CommLinkModelContentPart(sourceUrl);
                        clcp.setSlideShowLinks(currentSlideShowLinks);
                        parts.add(clcp);
                        currentSlideShowLinks = new ArrayList<>();
                    }
                    CommLinkModelContentPart clcp = new CommLinkModelContentPart(sourceUrl);
                    clcp.setTextContent(a[1]);
                    parts.add(clcp);
                }
            } else {
                if (currentSlideShowLinks.size() > 0) {
                    CommLinkModelContentPart clcp = new CommLinkModelContentPart(sourceUrl);
                    clcp.setSlideShowLinks(currentSlideShowLinks);
                    parts.add(clcp);
                    currentSlideShowLinks = new ArrayList<>();
                }
                CommLinkModelContentPart clcp = new CommLinkModelContentPart(sourceUrl);
                clcp.setTextContent(s);
                parts.add(clcp);
            }
        }


        PutResults<CommLinkModelContentPart> commLinkModelContentPartPutResults = SciApplication.getInstance().getStorIOSQLite()
                .put()
                .objects(parts)
                .prepare()
                .executeAsBlocking();

        Timber.d("Added ContentParts. Inserts: %s Updates: %s link: %s",
                commLinkModelContentPartPutResults.numberOfInserts(),
                commLinkModelContentPartPutResults.numberOfUpdates(),
                sourceUrl);

        for (CommLinkModelContentPart p : commLinkModelContentPartPutResults.results().keySet()) {
            p.id = commLinkModelContentPartPutResults.results().get(p).insertedId();
            if (p.id == null) {
                Timber.d("break here");
            }
        }

        return parts;
    }

    /**
     * Called when an error occurred during loading
     */
    @Override
    public void onLoadFailed() {
        mCommLinkSubject.onError(new Throwable(mContext.getString(R.string.error_failed_to_load_comm_link_rss)));
    }


    /**
     * @return An RxJava observable for which will be called once all articles are loaded
     */
    public Observable<ArrayList<CommLinkModel>> getCommLinks() {
        Timber.d("Observer subscribed for comm links");

        return mCommLinkSubject.asObservable();
    }

    public Observable<List<CommLinkModelContentPart>> getCommLinkContentParts(String sourceUrl) {
        Query q = Query.builder()
                .table(CommLinkContentPartTable.TABLE)
                .where(CommLinkContentPartTable.COLUMN_SOURCE_URI + " = ?")
                .whereArgs(sourceUrl)
                .build();

        return SciApplication.getInstance().getStorIOSQLite()
                .get()
                .listOfObjects(CommLinkModelContentPart.class)
                .withQuery(q)
                .prepare()
                .asRxObservable();
    }
}
