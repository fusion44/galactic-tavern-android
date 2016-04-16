package me.stammberger.galactictavern.ui.commlinks;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.actions.Actions;
import me.stammberger.galactictavern.actions.GtActionCreator;
import me.stammberger.galactictavern.actions.Keys;
import me.stammberger.galactictavern.core.Utility;
import me.stammberger.galactictavern.core.chrome.CustomTabActivityHelper;
import me.stammberger.galactictavern.core.chrome.WebviewFallback;
import me.stammberger.galactictavern.models.commlink.CommLinkModel;
import me.stammberger.galactictavern.models.commlink.ContentBlock1;
import me.stammberger.galactictavern.models.commlink.ContentBlock2;
import me.stammberger.galactictavern.models.commlink.ContentBlock4;
import me.stammberger.galactictavern.models.commlink.Wrapper;
import me.stammberger.galactictavern.models.favorites.Favorite;
import me.stammberger.galactictavern.stores.CommLinkStore;
import me.stammberger.galactictavern.stores.db.GtContentProvider;
import me.stammberger.galactictavern.stores.db.tables.FavoritesTable;
import me.stammberger.galactictavern.stores.db.tables.commlink.CommLinkModelTable;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock1Table;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock2Table;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock4Table;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentWrapperTable;
import timber.log.Timber;

/**
 * This Activity will display {@link .models.CommLinkModelContentPart} in an RecyclerView
 * with appropriate styling to make it visually pleasing and easy to read
 */
public class CommLinkReaderActivity extends AppCompatActivity implements RxViewDispatch, RequestListener<String, GlideDrawable>,
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String ACTION_COMM_LINK_WIDGET_CLICK
            = "me.stammberger.galactictavern.actions.ACTION_COMM_LINK_WIDGET_CLICK";
    public static final String ACTION_COMM_LINK_NOTIFICATION_CLICK
            = "me.stammberger.galactictavern.actions.ACTION_COMM_LINK_NOTIFICATION_CLICK";
    public static final String COMM_LINK_ITEM = "me.stammberger.galactictavern.COMM_LINK_ITEM";
    /**
     * Loader implementation
     */
    static final String[] COMM_LINK__PROJECTION = new String[]{
            CommLinkModelTable.COLUMN_ID,
            CommLinkModelTable.COLUMN_BACKDROP_URL
    };
    static final String[] WRAPPER_PROJECTION = new String[]{
            ContentWrapperTable.COLUMN_ID,
            ContentWrapperTable.COLUMN_COMM_LINK_ID,
            ContentWrapperTable.COLUMN_ID_BLOCK_4,
            ContentWrapperTable.COLUMN_ID_BLOCK_2,
            ContentWrapperTable.COLUMN_ID_BLOCK_1
    };
    static final String[] BLOCK_4_PROJECTION = new String[]{
            ContentBlock4Table.COLUMN_ID,
            ContentBlock4Table.COLUMN_TEXT
    };
    static final String[] BLOCK_2_PROJECTION = new String[]{
            ContentBlock2Table.COLUMN_ID,
            ContentBlock2Table.COLUMN_HEADER_IMAGE_TYPE,
            ContentBlock2Table.COLUMN_IMAGES
    };
    static final String[] BLOCK_1_PROJECTION = new String[]{
            ContentBlock1Table.COLUMN_ID,
            ContentBlock1Table.COLUMN_CONTENT,
    };
    static final String[] FAV_PROJECTION = new String[]{
            FavoritesTable.COLUMN_ID,
            FavoritesTable.COLUMN_REFERENCE
    };
    private static final String TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY = "CommLinkReaderActivity";
    private static final int LOADER_ID_COMM_LINK = 1336;
    private static final int LOADER_ID_WRAPPER_IDS = 1337;
    private static final int LOADER_ID_WRAPPER_BLOCK_4 = 1338;
    private static final int LOADER_ID_WRAPPER_BLOCK_2 = 1339;
    private static final int LOADER_ID_WRAPPER_BLOCK_1 = 1340;
    private static final int LOADER_ID_FAVORITE = 1341;
    private CommLinkModel mCommLink;
    private CommLinkStore mCommLinkStore;
    private FloatingActionButton mFab;
    private long mCommLinkId;
    private HashMap<String, Wrapper> mWrappersB4 = new HashMap<>();
    private HashMap<String, Wrapper> mWrappersB2 = new HashMap<>();
    private HashMap<String, Wrapper> mWrappersB1 = new HashMap<>();
    private boolean mBlock4FinishedLoading = false;
    private boolean mBlock2FinishedLoading = false;
    private boolean mBlock1FinishedLoading = false;
    private boolean mCommLinkFinishedLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_link_reader_native);
        GtApplication.getInstance().getRxFlux().onActivityCreated(this, savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFab = (FloatingActionButton) findViewById(R.id.share_fab);
        if (mFab != null) {
            mFab.setOnClickListener(v -> {
                GtActionCreator actionCreator = GtApplication.getInstance().getActionCreator();
                Favorite f = new Favorite();
                f.type = Favorite.TYPE_COMM_LINK;
                f.date = DateTime.now().getMillis();
                f.reference = String.valueOf(mCommLink.getCommLinkId());

                if (mCommLink.favorite) {
                    actionCreator.removeFavorite(f);
                } else {
                    actionCreator.addFavorite(f);
                }
            });
        }

        String action = getIntent().getAction();
        if (action != null && (action.equals(ACTION_COMM_LINK_WIDGET_CLICK) ||
                action.equals(ACTION_COMM_LINK_NOTIFICATION_CLICK))) {

            if (action.equals(ACTION_COMM_LINK_WIDGET_CLICK)) {
                GtApplication.getInstance().trackEvent(
                        TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY,
                        "openBy",
                        "widget");
            } else {
                GtApplication.getInstance().trackEvent(
                        TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY,
                        "openBy",
                        "notification");
            }

            // If this is true, this instance is launched via a widget click -> use a standard Android loader for loading
            Bundle extras = getIntent().getExtras();
            mCommLinkId = extras.getLong(COMM_LINK_ITEM);
            getLoaderManager().initLoader(LOADER_ID_COMM_LINK, null, this);
            getLoaderManager().initLoader(LOADER_ID_WRAPPER_IDS, null, this);
            getLoaderManager().initLoader(LOADER_ID_FAVORITE, null, this);
        } else {
            GtApplication.getInstance().trackEvent(
                    TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY,
                    "openBy",
                    "comm_link_list");
            Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();
            mCommLinkStore = CommLinkStore.get(dispatcher);
            Long commLinkId = getIntent().getLongExtra(COMM_LINK_ITEM, -1);
            mCommLink = mCommLinkStore.getCommLink(commLinkId);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mCommLink.getTitle());
            }

            ImageView backdropView = (ImageView) findViewById(R.id.comm_link_backdrop);
            Glide.with(this)
                    .load(mCommLink.getMainBackdrop())
                    .listener(this)
                    .into(backdropView);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Utility.isNetworkAvailable(this)) {
                /**
                 * Wait until the image is fully loaded and start again in Glides {@link #onResourceReady(GlideDrawable, String, Target, boolean, boolean)}
                 * This is to make sure the transition is started only after the target image is loaded.
                 * The animation will always look correct this way.
                 */
                supportPostponeEnterTransition();
                backdropView.setTransitionName(mCommLink.getMainBackdrop());
            }

            if (mCommLink.getWrappers().size() == 0) {
                GtApplication.getInstance().getActionCreator()
                        .getCommLinkContentWrappers(mCommLink.getCommLinkId());
            } else {
                setupRecyclerView();
            }

            updateFab();
        }

        if (!Utility.isNetworkAvailable(this)) {
            View v = findViewById(R.id.article_card);
            if (v != null) {
                Snackbar.make(v, R.string.error_no_network_activity_reader, Snackbar.LENGTH_LONG).show();
            }
        }

        Utility.cancelNotifications(this);
    }

    private void updateFab() {
        if (mCommLink.favorite) {
            mFab.setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.ic_star_gold_24dp));
        } else {
            mFab.setImageDrawable(ContextCompat.getDrawable(
                    this, R.drawable.ic_star_black_24dp));
        }
    }

    /**
     * Called by RxFlux whenever a RxStore has received data.
     *
     * @param change The change model with the data
     */
    @SuppressWarnings({"unchecked", "Convert2streamapi"})
    @Override
    public void onRxStoreChanged(RxStoreChange change) {
        switch (change.getStoreId()) {
            case CommLinkStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_COMM_LINK_CONTENT_WRAPPERS:
                        List<Wrapper> wrappers = mCommLinkStore
                                .getCommLinkContentWrappers(mCommLink.getCommLinkId());
                        mCommLink.setWrappers(wrappers);
                        setupRecyclerView();
                        break;
                    case Actions.COMM_LINK_DATA_UPDATED:
                        List<CommLinkModel> updatedModels =
                                (List<CommLinkModel>) change.getRxAction()
                                        .getData().get(Keys.COMM_LINKS);
                        for (CommLinkModel updatedModel : updatedModels) {
                            if (updatedModel == mCommLink) {
                                updateFab();
                            }
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comm_link_reader, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        } else if (id == R.id.action_open_browser) {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            CustomTabActivityHelper.openCustomTab(
                    this,
                    customTabsIntent,
                    Uri.parse(mCommLink.getSourceUrl()),
                    new WebviewFallback());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets RecyclerView up once the comm link and its parts are loaded
     */
    private void setupRecyclerView() {
        CommLinkReaderAdapter cmra = new CommLinkReaderAdapter(mCommLink);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commLinkContentRecyclerView);
        recyclerView.setAdapter(cmra);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cmra.notifyDataSetChanged();
    }

    @Override
    public void onRxError(RxError error) {

    }

    @Override
    public void onRxViewRegistered() {

    }

    @Override
    public void onRxViewUnRegistered() {

    }

    @Override
    public void onRxStoresRegister() {

    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportStartPostponedEnterTransition();
        }

        // we did not manually update the ImageView target, thus return false
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(this);
        if (id == LOADER_ID_COMM_LINK) {
            cl.setUri(GtContentProvider.URI_COMM_LINK);
            cl.setProjection(COMM_LINK__PROJECTION);
            cl.setSelection(CommLinkModelTable.COLUMN_COMM_LINK_ID + "=?");
            cl.setSelectionArgs(new String[]{String.valueOf(mCommLinkId)});
            return cl;
        } else if (id == LOADER_ID_WRAPPER_IDS) {
            cl.setUri(GtContentProvider.URI_COMM_LINK_WRAPPER_IDS);
            cl.setProjection(WRAPPER_PROJECTION);
            cl.setSelection(ContentWrapperTable.COLUMN_COMM_LINK_ID + "=?");
            cl.setSelectionArgs(new String[]{String.valueOf(mCommLinkId)});
            return cl;
        } else if (id == LOADER_ID_WRAPPER_BLOCK_4) {
            cl.setUri(GtContentProvider.URI_COMM_LINK_WRAPPER_BLOCK4);
            ArrayList<String> selectionArgs = args.getStringArrayList(String.valueOf(LOADER_ID_WRAPPER_BLOCK_4));
            String selection = null;
            if (selectionArgs != null) {
                selection = selectionArgs.remove(selectionArgs.size() - 1);
                cl.setSelectionArgs(selectionArgs.toArray(new String[selectionArgs.size()]));
            }
            cl.setProjection(BLOCK_4_PROJECTION);
            cl.setSelection(selection);
            return cl;
        } else if (id == LOADER_ID_WRAPPER_BLOCK_2) {
            cl.setUri(GtContentProvider.URI_COMM_LINK_WRAPPER_BLOCK2);
            ArrayList<String> selectionArgs = args.getStringArrayList(String.valueOf(LOADER_ID_WRAPPER_BLOCK_2));
            String selection = null;
            if (selectionArgs != null) {
                selection = selectionArgs.remove(selectionArgs.size() - 1);
                cl.setSelectionArgs(selectionArgs.toArray(new String[selectionArgs.size()]));
            }
            cl.setProjection(BLOCK_2_PROJECTION);
            cl.setSelection(selection);
            return cl;
        } else if (id == LOADER_ID_WRAPPER_BLOCK_1) {
            cl.setUri(GtContentProvider.URI_COMM_LINK_WRAPPER_BLOCK1);
            ArrayList<String> selectionArgs = args.getStringArrayList(String.valueOf(LOADER_ID_WRAPPER_BLOCK_1));
            String selection = null;
            if (selectionArgs != null) {
                selection = selectionArgs.remove(selectionArgs.size() - 1);
                cl.setSelectionArgs(selectionArgs.toArray(new String[selectionArgs.size()]));
            }
            cl.setProjection(BLOCK_1_PROJECTION);
            cl.setSelection(selection);
            return cl;
        } else if (id == LOADER_ID_FAVORITE) {
            cl.setUri(GtContentProvider.URI_FAVORITE);
            cl.setSelection(FavoritesTable.COLUMN_REFERENCE + "=?");
            cl.setSelectionArgs(new String[]{String.valueOf(mCommLinkId)});
            cl.setProjection(FAV_PROJECTION);
            return cl;
        }

        return null;
    }

    @Override
    protected void onResume() {
        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY);
        super.onResume();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (loader.getId() == LOADER_ID_COMM_LINK) {
            if (mCommLink == null) {
                mCommLink = new CommLinkModel();
            }
            mCommLink.commLinkId = data.getLong(0);
            mCommLink.mainBackdrop = data.getString(1);

            ImageView backdropView = (ImageView) findViewById(R.id.comm_link_backdrop);
            Glide.with(this)
                    .load(mCommLink.mainBackdrop)
                    .into(backdropView);

            mCommLinkFinishedLoading = true;
            Timber.d("");
        } else if (loader.getId() == LOADER_ID_WRAPPER_IDS) {
            if (mCommLink == null) {
                mCommLink = new CommLinkModel();
            }
            String block4statement = "";
            ArrayList<String> block4Values = new ArrayList<>();
            String block2statement = "";
            ArrayList<String> block2Values = new ArrayList<>();
            String block1statement = "";
            ArrayList<String> block1Values = new ArrayList<>();

            for (int i = 0; i < data.getCount(); i++) {
                Wrapper w = new Wrapper();
                w.id = data.getLong(0);
                w.commLinkId = data.getLong(1);
                mCommLink.commLinkId = data.getLong(1);

                String block4 = data.getString(2);
                if (block4 != null && !block4.equals("")) {
                    block4statement += ContentBlock4Table.COLUMN_ID + "=? OR ";
                    block4Values.add(block4);
                    mWrappersB4.put(block4, w);
                }

                String block2 = data.getString(3);
                if (block2 != null && !block2.equals("")) {
                    block2statement += ContentBlock2Table.COLUMN_ID + "=? OR ";
                    block2Values.add(block2);
                    mWrappersB2.put(block2, w);
                }
                String block1 = data.getString(4);
                if (block1 != null && !block1.equals("")) {
                    block1statement += ContentBlock1Table.COLUMN_ID + "=? OR ";
                    block1Values.add(block1);
                    mWrappersB1.put(block1, w);
                }

                mCommLink.wrappers.add(w);
                data.moveToNext();
            }

            Bundle b = new Bundle();

            if (block4Values.size() != 0) {
                // remove the trailing " OR " and add it to the values ArrayList
                // It'll be removed retrieved in onCreateLoader
                block4Values.add(block4statement.substring(0, block4statement.length() - 4));
                b.putStringArrayList(String.valueOf(LOADER_ID_WRAPPER_BLOCK_4), block4Values);
                getLoaderManager().initLoader(LOADER_ID_WRAPPER_BLOCK_4, b, this);

            } else {
                mBlock4FinishedLoading = true;
            }
            if (block2Values.size() != 0) {
                block2Values.add(block2statement.substring(0, block2statement.length() - 4));
                b.putStringArrayList(String.valueOf(LOADER_ID_WRAPPER_BLOCK_2), block2Values);
                getLoaderManager().initLoader(LOADER_ID_WRAPPER_BLOCK_2, b, this);
            } else {
                mBlock2FinishedLoading = true;
            }
            if (block1Values.size() != 0) {
                block1Values.add(block1statement.substring(0, block1statement.length() - 4));
                b.putStringArrayList(String.valueOf(LOADER_ID_WRAPPER_BLOCK_1), block1Values);
                getLoaderManager().initLoader(LOADER_ID_WRAPPER_BLOCK_1, b, this);
            } else {
                mBlock1FinishedLoading = true;
            }
        } else if (loader.getId() == LOADER_ID_WRAPPER_BLOCK_4) {
            data.moveToFirst();
            for (int i = 0; i < data.getCount(); i++) {
                ContentBlock4 b4 = new ContentBlock4();
                b4.id = data.getLong(0);
                b4.header = data.getString(1);
                mWrappersB4.get(b4.id.toString()).setContentBlock4(b4);
            }
            // Not needed anymore
            mWrappersB4.clear();
            mBlock4FinishedLoading = true;
        } else if (loader.getId() == LOADER_ID_WRAPPER_BLOCK_2) {
            data.moveToFirst();
            for (int i = 0; i < data.getCount(); i++) {
                ContentBlock2 b2 = new ContentBlock2();
                b2.id = data.getLong(0);
                b2.headerImageType = data.getInt(1);
                b2.setHeaderImages(Utility.parseStringListFromDbString(
                        data.getString(2), ContentBlock2.DATA_SEPARATOR));
                mWrappersB2.get(b2.id.toString()).setContentBlock2(b2);
            }
            mWrappersB2.clear();
            mBlock2FinishedLoading = true;
        } else if (loader.getId() == LOADER_ID_WRAPPER_BLOCK_1) {
            data.moveToFirst();
            for (int i = 0; i < data.getCount(); i++) {
                ContentBlock1 b1 = new ContentBlock1();
                b1.id = data.getLong(0);
                b1.contentDb = data.getString(1);
                mWrappersB1.get(b1.id.toString()).setContentBlock1(b1);
            }
            // Not needed anymore
            mWrappersB1.clear();
            mBlock1FinishedLoading = true;
        } else if (loader.getId() == LOADER_ID_FAVORITE) {
            if (mCommLink == null) {
                mCommLink = new CommLinkModel();
            }
            data.moveToFirst();
            mCommLink.favorite = data.getCount() != 0;
            updateFab();
        }

        if (mCommLinkFinishedLoading &&
                mBlock4FinishedLoading &&
                mBlock2FinishedLoading &&
                mBlock1FinishedLoading) {
            setupRecyclerView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
