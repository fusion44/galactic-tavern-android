package me.stammberger.starcitizencompact;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hardsoftstudio.rxflux.RxFlux;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import net.danlew.android.joda.JodaTimeAndroid;

import me.stammberger.starcitizencompact.actions.SciActionCreator;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModel;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModelSQLiteTypeMapping;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock1;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock1SQLiteTypeMapping;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock2;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock2SQLiteTypeMapping;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock4;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock4SQLiteTypeMapping;
import me.stammberger.starcitizencompact.models.commlink.Wrapper;
import me.stammberger.starcitizencompact.models.commlink.WrapperStorIOSQLiteDeleteResolver;
import me.stammberger.starcitizencompact.models.commlink.WrapperStorIOSQLitePutResolver;
import me.stammberger.starcitizencompact.models.user.UserSearchHistoryEntry;
import me.stammberger.starcitizencompact.models.user.UserSearchHistoryEntrySQLiteTypeMapping;
import me.stammberger.starcitizencompact.stores.db.DbOpenHelper;
import me.stammberger.starcitizencompact.stores.db.resolvers.ContentWrapperGetResolver;
import timber.log.Timber;


/**
 * Base Android Application
 * <p>
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

    /**
     * Instance of StorIO SQLite provider
     */
    private DefaultStorIOSQLite mStorIOSQLite;

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

        initStorIO();

        JodaTimeAndroid.init(this);

        mInstance = this;
    }

    /**
     * Initializes StorIO with all necessary resolvers
     */
    private void initStorIO() {
        mStorIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DbOpenHelper(this))
                .addTypeMapping(CommLinkModel.class, new CommLinkModelSQLiteTypeMapping())
                .addTypeMapping(ContentBlock1.class, new ContentBlock1SQLiteTypeMapping())
                .addTypeMapping(ContentBlock2.class, new ContentBlock2SQLiteTypeMapping())
                .addTypeMapping(ContentBlock4.class, new ContentBlock4SQLiteTypeMapping())
                .addTypeMapping(Wrapper.class, SQLiteTypeMapping.<Wrapper>builder()
                        .putResolver(new WrapperStorIOSQLitePutResolver())
                        .getResolver(new ContentWrapperGetResolver())
                        .deleteResolver(new WrapperStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(UserSearchHistoryEntry.class, new UserSearchHistoryEntrySQLiteTypeMapping())
                .build();
    }

    public RxFlux getRxFlux() {
        return mRxFlux;
    }

    public SciActionCreator getActionCreator() {
        return mActionCreator;
    }

    public DefaultStorIOSQLite getStorIOSQLite() {
        return mStorIOSQLite;
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
