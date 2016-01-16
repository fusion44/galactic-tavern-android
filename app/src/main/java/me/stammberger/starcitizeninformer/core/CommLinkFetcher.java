package me.stammberger.starcitizeninformer.core;

import android.content.Context;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.SciApplication;
import rx.Observable;
import rx.subjects.AsyncSubject;
import timber.log.Timber;

/**
 * Helper class to get all the comm links using PkRSS
 * This basically serves as a proxy class to turn PkRSS's output to an observable
 */
public class CommLinkFetcher implements Callback {
    final AsyncSubject<ArrayList<Article>> mSubject;
    private final Context mContext;

    public CommLinkFetcher() {
        mContext = SciApplication.getContext();
        PkRSS.with(mContext)
                .load("https://robertsspaceindustries.com/comm-link/rss")
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

        ArrayList<Article> aList = new ArrayList<>();
        aList.addAll(newArticles);

        mSubject.onNext(aList);
        mSubject.onCompleted();
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
    public Observable<ArrayList<Article>> observable() {
        Timber.d("Observer subscribed for comm links");

        return mSubject.asObservable();
    }
}
