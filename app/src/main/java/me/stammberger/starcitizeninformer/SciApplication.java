package me.stammberger.starcitizeninformer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hardsoftstudio.rxflux.RxFlux;

import net.danlew.android.joda.JodaTimeAndroid;

import me.stammberger.starcitizeninformer.actions.SciActionCreator;
import timber.log.Timber;


/**
 * Base Android Application
 * <p/>
 * This is a Singleton class
 */
public class SciApplication extends Application {
    /**
     * The Singleton instance
     */
    private static SciApplication mInstance;

    /**
     * Main {@link RxFlux} manager class.
     */
    private RxFlux mRxFlux;

    /**
     * Instance or {@link SciActionCreator}
     */
    private SciActionCreator mActionCreator;

    public static SciApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        mRxFlux = RxFlux.init(this);
        mActionCreator = new SciActionCreator(mRxFlux.getDispatcher(), mRxFlux.getSubscriptionManager());

        JodaTimeAndroid.init(this);

        mInstance = this;
    }

    public RxFlux getRxFlux() {
        return mRxFlux;
    }

    public SciActionCreator getActionCreator() {
        return mActionCreator;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            if (t != null) {
                if (priority == Log.ERROR) {
                    Log.e(SciApplication.class.getSimpleName(), t.getMessage(), t);
                } else if (priority == Log.WARN) {
                    Log.w(SciApplication.class.getSimpleName(), t.getMessage(), t);
                }
            }
        }
    }
}
