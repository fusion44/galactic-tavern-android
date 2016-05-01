package me.stammberger.galactictavern.ui.commlinks;

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
import me.stammberger.galactictavern.models.commlink.Wrapper;
import me.stammberger.galactictavern.models.favorites.Favorite;
import me.stammberger.galactictavern.stores.CommLinkStore;

/**
 * This Activity will display {@link .models.CommLinkModelContentPart} in an RecyclerView
 * with appropriate styling to make it visually pleasing and easy to read
 */
public class CommLinkReaderActivity extends AppCompatActivity implements RxViewDispatch, RequestListener<String, GlideDrawable> {
    public static final String ACTION_COMM_LINK_WIDGET_CLICK
            = "me.stammberger.galactictavern.actions.ACTION_COMM_LINK_WIDGET_CLICK";
    public static final String ACTION_COMM_LINK_NOTIFICATION_CLICK
            = "me.stammberger.galactictavern.actions.ACTION_COMM_LINK_NOTIFICATION_CLICK";
    public static final String COMM_LINK_ITEM = "me.stammberger.galactictavern.COMM_LINK_ITEM";

    private static final String TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY = "CommLinkReaderActivity";

    private CommLinkModel mCommLink;
    private CommLinkStore mCommLinkStore;
    private FloatingActionButton mFab;
    private long mCommLinkId;

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

        mFab = (FloatingActionButton) findViewById(R.id.favorite_fab);
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

        mCommLinkId = getIntent().getLongExtra(COMM_LINK_ITEM, -1);

        Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();
        mCommLinkStore = CommLinkStore.get(dispatcher);
        mCommLink = mCommLinkStore.getCommLink(mCommLinkId);
        if (mCommLink == null) {
            // Comm link not loaded yet. Probably coming from widget or notification
            GtApplication.getInstance().getActionCreator()
                    .getCommLink(mCommLinkId);
        } else if (mCommLink.wrappers.size() == 0) {
            GtApplication.getInstance().getActionCreator()
                    .getCommLinkContentWrappers(mCommLinkId);
        } else {
            // both comm link and its wrapper have been loaded -> seupUi()
            setupUi();
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
        } else {
            GtApplication.getInstance().trackEvent(
                    TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY,
                    "openBy",
                    "comm_link_list");
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
     * Setup UI once all data finished loading
     */
    private void setupUi() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mCommLink.title);
        }

        ImageView backdropView = (ImageView) findViewById(R.id.comm_link_backdrop);
        if (backdropView != null) {
            Glide.with(this)
                    .load(mCommLink.mainBackdrop)
                    .listener(this)
                    .into(backdropView);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Utility.isNetworkAvailable(this)) {
            /**
             * Wait until the image is fully loaded and start again in Glides {@link #onResourceReady(GlideDrawable, String, Target, boolean, boolean)}
             * This is to make sure the transition is started only after the target image is loaded.
             * The animation will always look correct this way.
             */
            supportPostponeEnterTransition();
            if (backdropView != null) {
                backdropView.setTransitionName(mCommLink.mainBackdrop);
            }
        }

        if (mCommLink.getWrappers().size() == 0) {
            GtApplication.getInstance().getActionCreator()
                    .getCommLinkContentWrappers(mCommLink.commLinkId);
        } else {
            setupRecyclerView();
        }

        updateFab();
    }

    /**
     * Sets RecyclerView up once the comm link and its parts are loaded
     */
    private void setupRecyclerView() {
        CommLinkReaderAdapter cmra = new CommLinkReaderAdapter(mCommLink, this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commLinkContentRecyclerView);
        if (recyclerView != null) {
            recyclerView.setAdapter(cmra);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            cmra.notifyDataSetChanged();
        }
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
                    case Actions.GET_COMM_LINK:
                        mCommLink = mCommLinkStore.getCommLink(mCommLinkId);
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
    protected void onResume() {
        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_COMM_LINK_READER_ACTIVITY);
        super.onResume();
    }
}
