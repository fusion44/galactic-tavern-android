package me.stammberger.starcitizeninformer.ui.commlinks;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.SciApplication;
import me.stammberger.starcitizeninformer.actions.Actions;
import me.stammberger.starcitizeninformer.core.chrome.CustomTabActivityHelper;
import me.stammberger.starcitizeninformer.core.chrome.WebviewFallback;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.stores.CommLinkStore;
import timber.log.Timber;

/**
 * This Activity will display {@link .models.CommLinkModelContentPart} in an RecyclerView
 * with appropriate styling to make it visually pleasing and easy to read
 */
public class CommLinkReaderActivity extends AppCompatActivity implements RxViewDispatch {
    public static final String COMM_LINK_ITEM = "comm_link_item";
    private CommLinkModel mCommLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_link_reader_native);
        SciApplication.getInstance().getRxFlux().onActivityCreated(this, savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCommLink = getIntent().getParcelableExtra(COMM_LINK_ITEM);

        ImageView backdropView = (ImageView) findViewById(R.id.comm_link_backdrop);
        Glide.with(this)
                .load(mCommLink.backdropUrl)
                .into(backdropView);

        if (mCommLink.content == null) {
            SciApplication.getInstance().getActionCreator().getCommLinkParts(mCommLink.sourceUri);
        } else {
            setupRecyclerView();
        }
    }

    /**
     * Called by RxFlux whenever a RxStore has received data.
     *
     * @param change The change model with the data
     */
    @Override
    public void onRxStoreChanged(RxStoreChange change) {
        switch (change.getStoreId()) {
            case CommLinkStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_COMM_LINK_PARTS:
                        Timber.d("Got com link parts");
                        Dispatcher dispatcher = SciApplication.getInstance().getRxFlux().getDispatcher();
                        mCommLink.content =
                                new ArrayList<>(CommLinkStore.get(dispatcher)
                                        .getCommLinkModelParts(mCommLink.sourceUri));
                        setupRecyclerView();
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
                    Uri.parse(mCommLink.sourceUri),
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
}
