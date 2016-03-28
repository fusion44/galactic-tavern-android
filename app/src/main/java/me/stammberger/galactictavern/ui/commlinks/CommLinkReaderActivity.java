package me.stammberger.galactictavern.ui.commlinks;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    public static final String COMM_LINK_ITEM = "me.stammberger.galactictavern.COMM_LINK_ITEM";
    private CommLinkModel mCommLink;
    private CommLinkStore mCommLinkStore;
    private FloatingActionButton mFab;

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

        Intent i = getIntent();
        if (i != null && i.getAction() != null &&
                i.getAction().equals(ACTION_COMM_LINK_WIDGET_CLICK)) {
            // If this is true, this instance is launched via a widget click -> use a standard Android loader for loading
            Bundle extras = i.getExtras();
            // TODO: implement the loader
        } else {
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
}
