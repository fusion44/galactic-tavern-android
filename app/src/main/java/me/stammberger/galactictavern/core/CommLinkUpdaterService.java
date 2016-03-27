package me.stammberger.galactictavern.core;

import android.app.IntentService;
import android.content.Context;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * When the API is ready, the App will be notified by a GCM message when a new comm link is released.
 * This is a workaround until then.
 */
public class CommLinkUpdaterService extends GcmTaskService implements CommLinkFetcher.UpdateProgressCallback {
    public static final String GCM_REPEAT_TAG = "repeat|[7200,1800]";
    private CountDownLatch keepAliveLatch = new CountDownLatch(1);
    private CommLinkFetcher mCommLinkFetcher;

    /**
     * Schedules an update for every 3 hours.
     * This task will survive reboots if android.permission.RECEIVE_BOOT_COMPLETED is granted
     *
     * @param context Current context
     * @param newInterval Update interval in seconds
     */
    public static void scheduleRepeatedUpdates(Context context, int newInterval) {
        try {
            PeriodicTask periodic = new PeriodicTask.Builder()
                    .setService(CommLinkUpdaterService.class)
                    .setPeriod(newInterval) // run every x seconds
                    .setFlex(60 * 30) // task can run 30 minutes before schedule
                    .setTag(GCM_REPEAT_TAG)
                    .setPersisted(true) // Task will survive reboots
                    .setUpdateCurrent(true) // replace current task if another one with this id exists
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED) // run only when connected to a network
                    .setRequiresCharging(false)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(periodic);
            Timber.d("Polling for updates every %s seconds", newInterval);
        } catch (Exception e) {
            Timber.e("scheduling failed");
            e.printStackTrace();
        }
    }

    public static void cancelUpdater(Context context) {
        Timber.d("Canceling all comm link updates");
        GcmNetworkManager
                .getInstance(context)
                .cancelTask(GCM_REPEAT_TAG, CommLinkUpdaterService.class);
    }

    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Timber.d("Running comm link update task");
        handleActionUpdateCommLinks();
        return GcmNetworkManager.RESULT_SUCCESS;
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
