package me.stammberger.galactictavern;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

import com.hardsoftstudio.rxflux.RxFlux;
import com.pixplicity.easyprefs.library.Prefs;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import net.danlew.android.joda.JodaTimeAndroid;

import me.stammberger.galactictavern.actions.GtActionCreator;
import me.stammberger.galactictavern.core.CommLinkUpdaterService;
import me.stammberger.galactictavern.models.commlink.CommLinkModel;
import me.stammberger.galactictavern.models.commlink.CommLinkModelSQLiteTypeMapping;
import me.stammberger.galactictavern.models.commlink.ContentBlock1;
import me.stammberger.galactictavern.models.commlink.ContentBlock1SQLiteTypeMapping;
import me.stammberger.galactictavern.models.commlink.ContentBlock2;
import me.stammberger.galactictavern.models.commlink.ContentBlock2SQLiteTypeMapping;
import me.stammberger.galactictavern.models.commlink.ContentBlock4;
import me.stammberger.galactictavern.models.commlink.ContentBlock4SQLiteTypeMapping;
import me.stammberger.galactictavern.models.commlink.Wrapper;
import me.stammberger.galactictavern.models.commlink.WrapperStorIOSQLiteDeleteResolver;
import me.stammberger.galactictavern.models.commlink.WrapperStorIOSQLitePutResolver;
import me.stammberger.galactictavern.models.favorites.Favorite;
import me.stammberger.galactictavern.models.favorites.FavoriteSQLiteTypeMapping;
import me.stammberger.galactictavern.models.forums.Forum;
import me.stammberger.galactictavern.models.forums.ForumSQLiteTypeMapping;
import me.stammberger.galactictavern.models.user.UserSearchHistoryEntry;
import me.stammberger.galactictavern.models.user.UserSearchHistoryEntrySQLiteTypeMapping;
import me.stammberger.galactictavern.stores.db.DbOpenHelper;
import me.stammberger.galactictavern.stores.db.resolvers.ContentWrapperGetResolver;
import timber.log.Timber;


/**
 * Base Android Application
 * <p>
 * This is a Singleton class
 */
public class GtApplication extends Application {
    /**
     * The Singleton instance
     */
    private static GtApplication mInstance;

    /**
     * Main {@link RxFlux} manager class.
     */
    private RxFlux mRxFlux;

    /**
     * Instance or {@link GtActionCreator}
     */
    private GtActionCreator mActionCreator;

    /**
     * Instance of StorIO SQLite provider
     */
    private DefaultStorIOSQLite mStorIOSQLite;

    public static GtApplication getInstance() {
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
        mActionCreator = new GtActionCreator(mRxFlux.getDispatcher(), mRxFlux.getSubscriptionManager());

        initStorIO();

        JodaTimeAndroid.init(this);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        mInstance = this;

        CommLinkUpdaterService.scheduleRepeatedUpdates(this);
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
                .addTypeMapping(Forum.class, new ForumSQLiteTypeMapping())
                .addTypeMapping(Favorite.class, new FavoriteSQLiteTypeMapping())
                .build();
    }

    public RxFlux getRxFlux() {
        return mRxFlux;
    }

    public GtActionCreator getActionCreator() {
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
                    Log.e(GtApplication.class.getSimpleName(), t.getMessage(), t);
                } else if (priority == Log.WARN) {
                    Log.w(GtApplication.class.getSimpleName(), t.getMessage(), t);
                }
            }
        }
    }
}
