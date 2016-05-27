package space.galactictavern.app.ui.maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;
import java.util.List;

import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.stores.StarmapStore;
import space.galactictavern.app.ui.RxFluxActivity;
import space.galactictavern.mapcore.map.data.StarMapData;

public class MapActivity extends RxFluxActivity implements
        AndroidFragmentApplication.Callbacks, RxViewDispatch {
    /**
     * Instance of the MapFragment
     */
    private MapFragment mFragment;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            /**
             * This activity has a transparent status bar so we have to move the
             * toolbar down accordingly
             */
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        setTitle("");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Not implemented yet ...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        Dispatcher d = GtApplication.getInstance().getRxFlux().getDispatcher();
        StarMapData bootUpData = StarmapStore.get(d).getBootUpData();
        if (bootUpData.data == null) {
            GtApplication.getInstance().getActionCreator().getStarMapBootUpData();
        } else {
            mFragment.setStarMapData(StarmapStore.get(d).getBootUpData());
        }
    }

    @Override
    public void onAttachFragment(Fragment f) {
        mFragment = (MapFragment) f;
        super.onAttachFragment(f);
    }

    @Override
    public void exit() {

    }

    @Override
    public void onRxStoreChanged(@NonNull RxStoreChange change) {
        switch (change.getStoreId()) {
            case StarmapStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_STARMAP_BOOT_UP_DATA:
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.map_fragment_content);
                        if (f instanceof MapFragment) {
                            Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();
                            ((MapFragment) f).setStarMapData(StarmapStore.get(dispatcher).getBootUpData());
                        } else {
                            Utility.reportFirebaseCrash(new Throwable(
                                    "MapFragment is actually not MapFragment."));
                        }
                }
                break;
        }
    }

    @Override
    public void onRxError(@NonNull RxError error) {

    }

    @Override
    public void onRxViewRegistered() {

    }

    @Override
    public void onRxViewUnRegistered() {

    }

    @Nullable
    @Override
    public List<RxStore> getRxStoreListToRegister() {
        Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();
        ArrayList<RxStore> stores = new ArrayList<>();
        stores.add(StarmapStore.get(dispatcher));
        return stores;
    }

    @Nullable
    @Override
    public List<RxStore> getRxStoreListToUnRegister() {
        return null;
    }


    /**
     * Get status bar height.
     * http://blog.raffaeu.com/archive/2015/04/11/android-and-the-transparent-status-bar.aspx
     *
     * @return Height in dp
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
