package me.stammberger.galactictavern.core;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class CommLinkUpdaterService extends IntentService implements CommLinkFetcher.UpdateProgressCallback {
    private static final String ACTION_UPDATE_COMM_LINK = "galactictavern.core.action.UPDATE_COMM_LINK";
    /**
     * Work around for a PkRSS bug which prevents getting the RSSFeed synchronously.
     * To prevent the intent service from shutdown while waiting for the callback we use this {@link CountDownLatch}
     */
    private CountDownLatch keepAliveLatch = new CountDownLatch(1);
    private CommLinkFetcher mCommLinkFetcher;

    public CommLinkUpdaterService() {
        super("CommLinkUpdaterService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateCommLinks(Context context) {
        Intent intent = new Intent(context, CommLinkUpdaterService.class);
        intent.setAction(ACTION_UPDATE_COMM_LINK);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_COMM_LINK.equals(action)) {
                handleActionUpdateCommLinks();
            }
        }
    }

    private void handleActionUpdateCommLinks() {
        if (mCommLinkFetcher == null) {
            mCommLinkFetcher = new CommLinkFetcher(this, false);
            try {
                keepAliveLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateStarted() {
    }

    @Override
    public void onUpdateError() {
        Timber.d("Error during fetch. Stopping Service");
        keepAliveLatch.countDown();
    }

    @Override
    public void onUpdateFinished(Integer numDbUpdated) {
        Timber.d("Updated %s Articles", numDbUpdated);
        keepAliveLatch.countDown();
    }
}
