package me.stammberger.galactictavern.core;

import android.content.Context;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.core.retrofit.CommLinkApiService;
import me.stammberger.galactictavern.models.commlink.CommLinkModel;
import me.stammberger.galactictavern.models.commlink.Wrapper;
import me.stammberger.galactictavern.stores.db.resolvers.ContentWrapperGetResolver;
import me.stammberger.galactictavern.stores.db.tables.commlink.CommLinkModelTable;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentWrapperTable;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import timber.log.Timber;

/**
 * Helper class to get all the comm links using PkRSS
 * This basically serves as a proxy class to turn PkRSS's output to an observable
 */
public class CommLinkFetcher implements Callback {
    private final AsyncSubject<ArrayList<CommLinkModel>> mCommLinkSubject;
    private final Context mContext;
    private long mLatestCommLinkDateInDb = 0;
    private UpdateProgressCallback mCallback = null;
    private boolean mBroadcastAfterRSSProcessing = true;

    public CommLinkFetcher() {
        Timber.d("New CommLinkFetcher No Callback");
        mContext = GtApplication.getContext();
        mCommLinkSubject = AsyncSubject.create();
        startUpdate();
    }

    public CommLinkFetcher(UpdateProgressCallback callback, boolean broadcastAfterRSSProcessing) {
        Timber.d("New CommLinkFetcher With Callback");
        mContext = GtApplication.getContext();
        mCallback = callback;
        mCommLinkSubject = AsyncSubject.create();
        mBroadcastAfterRSSProcessing = broadcastAfterRSSProcessing;
        startUpdate();
    }

    private void startUpdate() {
        // get the latest from Database to check whether we have new comm links
        getLatestCommLinkFromDb()
                .take(1)
                .subscribe(commLinkModel -> {
                    Timber.d("calling fetch");
                    if (commLinkModel != null) {
                        mLatestCommLinkDateInDb = commLinkModel.getPublished();
                    }
                    fetchRSSFeed();
                }, throwable -> {
                    Timber.d("Error retrieving latest comm link from database. Error: %s", throwable.toString());
                    fetchRSSFeed();
                });
    }

    /**
     * Start loading of the rss feed
     */
    private void fetchRSSFeed() {
        if (mCallback != null) {
            mCallback.onUpdateStarted();
        }

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
                .table(CommLinkModelTable.TABLE)
                .orderBy(CommLinkModelTable.COLUMN_PUBLISHED_DATE + " DESC")
                .limit(1)
                .build();

        return GtApplication.getInstance().getStorIOSQLite()
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
        if (!mBroadcastAfterRSSProcessing) {
            Timber.d("Broadcast off");
            return;
        }

        Query q = Query.builder()
                .table(CommLinkModelTable.TABLE)
                .orderBy(CommLinkModelTable.COLUMN_PUBLISHED_DATE + " DESC")
                .build();

        List<CommLinkModel> commLinkModel = GtApplication.getInstance().getStorIOSQLite()
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
            Timber.d("No new articles. Skip update.");
            getAllCommLinksFromDb();
            if (mCallback != null) {
                mCallback.onUpdateFinished(0);
            }
            return;
        }

        Observable.from(newArticles)
                .observeOn(Schedulers.io())
                .takeWhile(article -> {
                    // emits the observable received if its newer than the latest one received from database
                    return article.getDate() > mLatestCommLinkDateInDb;
                })
                .map(this::getCommLinkModel)
                .filter(commLinkModel1 -> commLinkModel1 != null)
                .toList() // this will replace also the onCompleted call
                .subscribe(commLinkModels -> {
                    GtApplication.getInstance().getStorIOSQLite()
                            .put()
                            .objects(commLinkModels)
                            .prepare()
                            .asRxObservable()
                            .subscribe(commLinkModelPutResults -> {
                                getAllCommLinksFromDb();
                                if (mCallback != null) {
                                    mCallback.onUpdateFinished(commLinkModelPutResults.results().size());
                                }
                            }, throwable -> {
                                onError();
                                Timber.d("Error putting CommLinkModels into database: \n %s", throwable.getCause().toString());
                            });
                }, throwable -> {
                    onError();
                    Timber.d("Error1: %s", throwable.getCause());
                    Timber.d("onError: %s", throwable.toString());
                });
    }

    /**
     * Creates a {@link CommLinkModel} from an PkRSS Article
     *
     * @param a Article to create the CommLinkModel from
     * @return Article's CommlinkModel
     */
    private CommLinkModel getCommLinkModel(Article a) {
        int id = Utility.getId(a.getSource().toString());
        Timber.d("Getting model for comm link id %s from API", id);

        CommLinkModel cm;
        try {
            cm = CommLinkApiService.Factory.getInstance()
                    .getCommLink(id)
                    .toBlocking()
                    .first();
        } catch (Exception e) {
            // TODO: notify API to update the commlinks as there is a new one available
            // but hasn't been processed yet
            Timber.d("%s", e.getMessage());
            return null;
        }

        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();

        for (Wrapper wrapper : cm.getWrappers()) {
            wrapper.commLinkId = cm.commLinkId;
            if (wrapper.getContentBlock1() != null) {
                wrapper.getContentBlock1().genDbData();
                PutResult putResult = storio.put()
                        .object(wrapper.getContentBlock1())
                        .prepare()
                        .executeAsBlocking();
                wrapper.contentBlock1DbId = putResult.insertedId();
            }
            if (wrapper.getContentBlock2() != null) {
                wrapper.getContentBlock2().genDbData();
                PutResult putResult = storio.put()
                        .object(wrapper.getContentBlock2())
                        .prepare()
                        .executeAsBlocking();
                wrapper.contentBlock2DbId = putResult.insertedId();
            }
            if (wrapper.getContentBlock4() != null) {
                PutResult putResult = storio.put()
                        .object(wrapper.getContentBlock4())
                        .prepare()
                        .executeAsBlocking();
                wrapper.contentBlock4DbId = putResult.insertedId();
            }
        }

        /**
         * Optimization potential:
         * Save the wrappers to a list and put them to DB all at once after every comm link has been parsed
         * Not sure its worth the extra hassle, since it'll only have huge chunk of data on first run anyway
         */
        storio.put()
                .objects(cm.getWrappers())
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(wrapperPutResults -> {
                    // do nothing
                }, throwable -> {
                    Timber.d("Error putting %s wrapper to DB", cm.commLinkId);
                    Timber.d(throwable.getCause().toString());
                    Timber.d(throwable.toString());
                });

        return cm;
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

    /**
     * Retrieves a single comm link
     *
     * @param id The comm link id
     * @return Observable for the content
     */
    public Observable<CommLinkModel> getCommLink(Long id) {
        Query q = Query.builder()
                .table(CommLinkModelTable.TABLE)
                .where(CommLinkModelTable.COLUMN_COMM_LINK_ID + " = ?")
                .whereArgs(id)
                .build();

        return GtApplication.getInstance().getStorIOSQLite()
                .get()
                .object(CommLinkModel.class)
                .withQuery(q)
                .prepare()
                .asRxObservable();
    }

    /**
     * Get all content wrappers for a specific comm link ID
     *
     * @param id The comm link id
     * @return an observable for the wrapper list
     */
    public Observable<List<Wrapper>> getCommLinkContentWrappers(Long id) {
        Query q = Query.builder()
                .table(ContentWrapperTable.TABLE)
                .where(CommLinkModelTable.COLUMN_COMM_LINK_ID + " = ?")
                .whereArgs(id)
                .build();

        return GtApplication.getInstance().getStorIOSQLite()
                .get()
                .listOfObjects(Wrapper.class)
                .withQuery(q)
                .withGetResolver(new ContentWrapperGetResolver())
                .prepare()
                .asRxObservable();
    }

    private void onError() {
        if (mCallback != null) {
            mCallback.onUpdateError();
        }
    }

    public interface UpdateProgressCallback {
        void onUpdateStarted();

        void onUpdateError();

        void onUpdateFinished(Integer numDbUpdated);
    }
}