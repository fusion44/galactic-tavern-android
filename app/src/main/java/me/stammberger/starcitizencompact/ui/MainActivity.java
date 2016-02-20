package me.stammberger.starcitizencompact.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.actions.Actions;
import me.stammberger.starcitizencompact.stores.CommLinkStore;
import me.stammberger.starcitizencompact.stores.ShipStore;
import me.stammberger.starcitizencompact.ui.commlinks.CommLinkListFragment;
import me.stammberger.starcitizencompact.ui.ships.ShipListFragment;
import timber.log.Timber;


/**
 * This is the manager {@link AppCompatActivity}. It will receive all store change notifications and
 * pass those on to the currently displayed {@link Fragment}
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , RxViewDispatch {

    /**
     * Instance of {@link CommLinkStore} for retrieving comm links
     */
    private CommLinkStore mCommLinkStore;

    /**
     * instance of {@link ShipStore} for retrieving ship data
     */
    private ShipStore mShipStore;

    /**
     * Currently displayed {@link Fragment} for whenever {@link MainActivity#onRxStoreChanged(RxStoreChange)}
     * is called and data needs to be passed on.
     */
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        openShipsFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_comm_link) {
            openCommLinkFragment();
        } else if (id == R.id.nav_ships) {
            openShipsFragment();
        } else if (id == R.id.nav_users) {

        } else if (id == R.id.nav_forums) {

        } else if (id == R.id.nav_orgs) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_settings) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Creates and shows the {@link CommLinkListFragment}
     */
    private void openCommLinkFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mCurrentFragment = CommLinkListFragment.newInstance(2);
        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_comm_link);
        }
    }

    /**
     * Creates and shows the {@link ShipListFragment}
     */
    private void openShipsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mCurrentFragment = ShipListFragment.newInstance(1);
        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_ships);
        }
    }

    @Override
    public void onRxStoreChanged(RxStoreChange change) {
        switch (change.getStoreId()) {
            case CommLinkStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_COMM_LINKS:
                        if (mCurrentFragment != null && mCurrentFragment instanceof CommLinkListFragment) {
                            CommLinkListFragment f = (CommLinkListFragment) mCurrentFragment;
                            f.setCommLinks(mCommLinkStore.getCommLinks());
                        }
                        break;
                }
                break;
            case ShipStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_SHIP_DATA_ALL:
                        if (mCurrentFragment != null && mCurrentFragment instanceof ShipListFragment) {
                            ShipListFragment f = (ShipListFragment) mCurrentFragment;
                            f.setShipData(mShipStore.getAllShips());
                        }
                }
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
        Timber.d("Registering stores");

        Dispatcher dispatcher = SciApplication.getInstance().getRxFlux().getDispatcher();

        mCommLinkStore = CommLinkStore.get(dispatcher);
        mCommLinkStore.register();

        mShipStore = ShipStore.get(dispatcher);
        mShipStore.register();
    }
}