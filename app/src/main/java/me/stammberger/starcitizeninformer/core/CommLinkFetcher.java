package me.stammberger.starcitizeninformer.core;

import android.content.Context;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.SciApplication;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.models.CommLinkModel.CommLinkContentPart;
import rx.Observable;
import rx.subjects.AsyncSubject;
import timber.log.Timber;

/**
 * Helper class to get all the comm links using PkRSS
 * This basically serves as a proxy class to turn PkRSS's output to an observable
 */
public class CommLinkFetcher implements Callback {
    final AsyncSubject<ArrayList<CommLinkModel>> mSubject;
    private final Context mContext;
    private int mColumnCount;

    public CommLinkFetcher() {
        mContext = SciApplication.getContext();
        mColumnCount = mContext.getResources().getInteger(R.integer.list_column_count);

        PkRSS.with(mContext)
                .load("https://robertsspaceindustries.com/comm-link/rss")
                        //.load("http://192.168.178.95:8000/sci_rss.xml")
                .callback(this)
                .async();

        mSubject = AsyncSubject.create();
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
        Timber.d("Finished loading comm links");

        ArrayList<CommLinkModel> aList = new ArrayList<>();

        for (int i = 0; i < newArticles.size(); i++) {
            Article article = newArticles.get(i);
            CommLinkModel cl = new CommLinkModel();
            cl.sourceUri = article.getSource();
            cl.title = article.getTitle();
            cl.content = processContent(article.getContent());
            cl.date = article.getDate();
            cl.description = article.getDescription();
            cl.tags = (ArrayList<String>) article.getTags();

            if (article.getMediaContent().size() > 0) {
                cl.backdropUrl = article.getMediaContent().get(0).getUrl();
            }

            aList.add(cl);
        }

        mSubject.onNext(calculateSpanCount(aList));
        mSubject.onCompleted();
    }

    /**
     * Splits the RSS content into several parts to make it easier to work with and
     * display in a visually pleasing way within a RecyclerView.
     *
     * @param content Content string directly from the RSS feed
     * @return {@link CommLinkContentPart} {@link ArrayList} Holding all parsed data
     */
    private ArrayList<CommLinkContentPart> processContent(String content) {
        ArrayList<CommLinkContentPart> parts = new ArrayList<>();

        if (content == null) {
            return parts;
        }

        String splitDelimiter = "<a class=\"image  js-open-in-slideshow\" data-source_url=\"";
        String[] split = content.split(splitDelimiter);

        ArrayList<String> slideShowLinks = new ArrayList<>();

        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (s.startsWith("https:")) {
                slideShowLinks.add(s.split("\" rel=")[0]);

                /**
                 * Peek on the next item. If it doesn't start with https: we are finished with the slideshow.
                 * Flush the slideshow and continue with text content.
                 */
                if (split.length == i + 1 || split.length > i + 1 && !split[i + 1].startsWith("https:")) {
                    CommLinkContentPart clcp = new CommLinkContentPart(
                            CommLinkContentPart.CONTENT_TYPE_SLIDESHOW,
                            "",
                            slideShowLinks
                    );
                    parts.add(clcp);
                    slideShowLinks = new ArrayList<>();
                }
            } else {
                CommLinkContentPart clcp = new CommLinkContentPart(
                        CommLinkContentPart.CONTENT_TYPE_TEXT_BLOCK,
                        s,
                        null
                );
                parts.add(clcp);
            }
        }

        return parts;
    }

    /**
     * Calculates the span count comm links. Takes several keywords into account like
     * "around the verse" or "released" to guess which item might be more interesting
     * to the user. This will basically sort the list.
     *
     * @param aList List of comm links
     * @return The sorted list
     */
    private ArrayList<CommLinkModel> calculateSpanCount(ArrayList<CommLinkModel> aList) {
        int currentColumn = 0;

        for (int i = 0; i < aList.size(); i++) {
            CommLinkModel current = aList.get(i);
            int spanSize = 1;

            if (i == 0 || mColumnCount == 2 && currentColumn == 0) {
                // if we are in two column mode, check whether this item is displayed in first column
                // if yes, check whether it will be displayed with two columns
                int rand = (int) (Math.random() * 3);
                if (rand == 2) {
                    spanSize = 2;

                    // count one up as one additional column is used up.
                    // This basically leads to resetting at the end of this for loop on case of two columns
                    currentColumn++;
                }
            } else if (mColumnCount == 3) {
                if (currentColumn == 0 || currentColumn == 1) {
                    int rand = (int) (Math.random() * 3);
                    if (rand == 2) {
                        spanSize = 2;
                        currentColumn++; // count one up as one additional column is used up
                    }
                }
            }

            if (i + 1 == aList.size() && currentColumn == 0) {
                // if the last item is in the left column force it to span all columns
                spanSize = mColumnCount;
            }

            currentColumn++;
            // reset column counter.
            if (currentColumn >= mColumnCount) {
                currentColumn = 0;
            }

            current.spanCount = spanSize;
        }

        return aList;
    }

    /**
     * Called when an error occurred during loading
     */
    @Override
    public void onLoadFailed() {
        mSubject.onError(new Throwable(mContext.getString(R.string.error_failed_to_load_comm_link_rss)));
    }


    /**
     * @return An RxJava observable for which will be called once all articles are loaded
     */
    public Observable<ArrayList<CommLinkModel>> observable() {
        Timber.d("Observer subscribed for comm links");

        return mSubject.asObservable();
    }
}
