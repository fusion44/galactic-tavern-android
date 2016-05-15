package space.galactictavern.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.core.gcm.GcmRegistrationIntentService;
import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.models.ship.Ship;
import space.galactictavern.app.stores.CommLinkStore;
import space.galactictavern.app.stores.ForumStore;
import space.galactictavern.app.stores.OrganizationStore;
import space.galactictavern.app.stores.ShipStore;
import space.galactictavern.app.stores.StarmapStore;
import space.galactictavern.app.stores.UserStore;
import space.galactictavern.app.ui.commlinks.CommLinkListFragment;
import space.galactictavern.app.ui.forums.ForumListFragment;
import space.galactictavern.app.ui.maps.MapFragment;
import space.galactictavern.app.ui.orgs.OrgsFragment;
import space.galactictavern.app.ui.prefs.SettingsActivity;
import space.galactictavern.app.ui.ships.ShipListFragment;
import space.galactictavern.app.ui.users.UserSearchFragment;


/**
 * This is the manager {@link AppCompatActivity}. It will receive all store change notifications and
 * pass those on to the currently displayed {@link Fragment}
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RxViewDispatch, AndroidFragmentApplication.Callbacks {

    private static final String TRACKING_SCREEN_COMM_LINK_FRAGMENT = "CommLinkFragment";
    private static final String TRACKING_SCREEN_SHIPS_FRAGMENT = "ShipsFragment";
    private static final String TRACKING_SCREEN_USERS_FRAGMENT = "UsersFragment";
    private static final String TRACKING_SCREEN_FORUMS_FRAGMENT = "ForumsFragment";
    private static final String TRACKING_SCREEN_ORG_FRAGMENT = "OrgsFragment";
    private static final String TRACKING_SCREEN_MAPS_FRAGMENT = "MapsFragment";

    private static final String KEY_CURRENT_FRAGMENT = "stores_registered";
    /**
     * Instance of {@link CommLinkStore} for retrieving comm links
     */
    private CommLinkStore mCommLinkStore;

    /**
     * instance of {@link ShipStore} for retrieving ship data
     */
    private ShipStore mShipStore;

    /**
     * Instance of {@link UserStore} for retrieving user data
     */
    private UserStore mUserStore;

    /**
     * Instance of {@link OrganizationStore} for retrieving organization data
     */
    private OrganizationStore mOrganizationStore;

    /**
     * Instance of {@link StarmapStore} for retrieving starmap data
     */
    private StarmapStore mStarmapStore;

    /**
     * Instance of {@link ForumStore} for retrieving forum data
     */
    private ForumStore mForumsStore;

    /**
     * Currently displayed {@link Fragment} for whenever {@link MainActivity#onRxStoreChanged(RxStoreChange)}
     * is called and data needs to be passed on.
     */
    private Fragment mCurrentFragment;

    /**
     * Listens for changes to the GCM device registration state
     */
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean mIsReceiverRegistered;

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

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_CURRENT_FRAGMENT)) {
                String fragment = savedInstanceState.getString(KEY_CURRENT_FRAGMENT);
                assert fragment != null;

                if (fragment.equals(CommLinkListFragment.class.getSimpleName())) {
                    openCommLinkFragment();
                } else if (fragment.equals(ShipListFragment.class.getSimpleName())) {
                    openShipsFragment();
                } else if (fragment.equals(UserSearchFragment.class.getSimpleName())) {
                    openUsersFragment();
                } else if (fragment.equals(ForumListFragment.class.getSimpleName())) {
                    openForumsFragment();
                } else if (fragment.equals(OrgsFragment.class.getSimpleName())) {
                    openOrgFragment();
                } else if (fragment.equals(MapFragment.class.getSimpleName())) {
                    openMapFragment();
                }
            }
        } else {
            String f = Prefs.getString(KEY_CURRENT_FRAGMENT, "");
            if (f.equals("") || f.equals(CommLinkListFragment.class.getSimpleName())) {
                openCommLinkFragment();
            } else if (f.equals(ShipListFragment.class.getSimpleName())) {
                openShipsFragment();
            } else if (f.equals(UserSearchFragment.class.getSimpleName())) {
                openUsersFragment();
            } else if (f.equals(ForumListFragment.class.getSimpleName())) {
                openForumsFragment();
            } else if (f.equals(OrgsFragment.class.getSimpleName())) {
                openOrgFragment();
            } else if (f.equals(MapFragment.class.getSimpleName())) {
                openMapFragment();
            }
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = Prefs.getBoolean(
                        GcmRegistrationIntentService.DEVICE_REGISTRATION_TOKEN_SENT, false);
                // TODO: Handle incoming messages if UI is active
            }
        };

        if (Utility.checkPlayServices(this)) {
            Intent intent = new Intent(this, GcmRegistrationIntentService.class);
            startService(intent);
        }

        Utility.cancelNotifications(this);
    }

    private void registerReceiver() {
        if (!mIsReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GcmRegistrationIntentService.DEVICE_REGISTRATION_COMPLETE));
            mIsReceiverRegistered = true;
        }
    }

    @Override
    protected void onResume() {
        if (mCurrentFragment != null) {
            String fragment = mCurrentFragment.getClass().getSimpleName();
            if (fragment.equals(CommLinkListFragment.class.getSimpleName())) {
                GtApplication.getInstance().trackScreen(TRACKING_SCREEN_COMM_LINK_FRAGMENT);
            } else if (fragment.equals(ShipListFragment.class.getSimpleName())) {
                GtApplication.getInstance().trackScreen(TRACKING_SCREEN_SHIPS_FRAGMENT);
            } else if (fragment.equals(UserSearchFragment.class.getSimpleName())) {
                GtApplication.getInstance().trackScreen(TRACKING_SCREEN_USERS_FRAGMENT);
            } else if (fragment.equals(ForumListFragment.class.getSimpleName())) {
                GtApplication.getInstance().trackScreen(TRACKING_SCREEN_FORUMS_FRAGMENT);
            } else if (fragment.equals(OrgsFragment.class.getSimpleName())) {
                GtApplication.getInstance().trackScreen(TRACKING_SCREEN_ORG_FRAGMENT);
            } else if (fragment.equals(MapFragment.class.getSimpleName())) {
                GtApplication.getInstance().trackScreen(TRACKING_SCREEN_MAPS_FRAGMENT);
            }
        }
        registerReceiver();
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        mIsReceiverRegistered = false;
        super.onPause();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentFragment != null) {
            outState.putString(KEY_CURRENT_FRAGMENT, mCurrentFragment.getTag());
        }
        super.onSaveInstanceState(outState);
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
            openUsersFragment();
        } else if (id == R.id.nav_forums) {
            openForumsFragment();
        } else if (id == R.id.nav_orgs) {
            openOrgFragment();
        } else if (id == R.id.nav_starmap) {
            openMapFragment();
        } else if (id == R.id.nav_settings) {
            openSettings();
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

        String simpleClassName = CommLinkListFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            mCurrentFragment = CommLinkListFragment.newInstance(2);
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_comm_link);
        }

        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_COMM_LINK_FRAGMENT);
        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    /**
     * Creates and shows the {@link ShipListFragment}
     */
    private void openShipsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String simpleClassName = ShipListFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            mCurrentFragment = ShipListFragment.newInstance(1);
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_ships);
        }

        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_SHIPS_FRAGMENT);
        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    /**
     * Creates and shows the {@link .users.UserFragment}
     */
    private void openUsersFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String simpleClassName = UserSearchFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            mCurrentFragment = UserSearchFragment.newInstance();
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_users);
        }

        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_USERS_FRAGMENT);
        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    /**
     * Creates and shows the {@link .forums.ForumListFragment}
     */
    private void openForumsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String simpleClassName = ForumListFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            mCurrentFragment = ForumListFragment.newInstance();
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_forums);
        }

        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_FORUMS_FRAGMENT);
        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    /**
     * Creates and shows the {@link .orgs.OrgsFragment}
     */
    private void openOrgFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String simpleClassName = OrgsFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            mCurrentFragment = OrgsFragment.newInstance();
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_organizations);
        }

        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_ORG_FRAGMENT);
        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    /**
     * Creates and shows the {@link .MapsFragment}
     */
    private void openMapFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String simpleClassName = MapFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            mCurrentFragment = MapFragment.newInstance();
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_starmap);
        }

        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_MAPS_FRAGMENT);
        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    private void openSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRxStoreChanged(RxStoreChange change) {
        switch (change.getStoreId()) {
            case CommLinkStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_COMM_LINKS:
                        if (mCurrentFragment != null && mCurrentFragment instanceof CommLinkListFragment) {
                            CommLinkListFragment f = (CommLinkListFragment) mCurrentFragment;
                            ArrayList<CommLinkModel> o = (ArrayList<CommLinkModel>)
                                    change.getRxAction().getData().get(Keys.COMM_LINKS);
                            f.addCommLinks(o);
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
                        break;
                    case Actions.SHIP_DATA_UPDATED:
                        if (mCurrentFragment != null && mCurrentFragment instanceof ShipListFragment) {
                            ShipListFragment f = (ShipListFragment) mCurrentFragment;
                            List<Ship> changedShips =
                                    (List<Ship>) change.getRxAction().getData().get(Keys.SHIP_DATA_LIST);
                            f.updateShipData(changedShips);
                        }
                        break;
                }
                break;
            case UserStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_USER_BY_USER_HANDLE:
                        if (mCurrentFragment != null && mCurrentFragment instanceof UserSearchFragment) {
                            String handle = (String) change.getRxAction().getData().get(Keys.USER_HANDLE);
                            boolean successful = (boolean) change.getRxAction().getData().get(Keys.USER_DATA_SEARCH_SUCCESSFUL);
                            UserSearchFragment f = (UserSearchFragment) mCurrentFragment;
                            f.setUser(successful, handle, mUserStore.getUser(handle));
                        }
                        break;
                    case Actions.GET_USER_SEARCH_HISTORY:
                        if (mCurrentFragment != null && mCurrentFragment instanceof UserSearchFragment) {
                            UserSearchFragment f = (UserSearchFragment) mCurrentFragment;
                            f.setUserSearchHistory(mUserStore.getUserSearchHistory(10));
                        }
                        break;
                }
            case ForumStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_FORUMS_ALL:
                        if (mCurrentFragment != null && mCurrentFragment instanceof ForumListFragment) {
                            ForumListFragment f = (ForumListFragment) mCurrentFragment;
                            f.setForums(mForumsStore.getForums());
                        }
                }
                break;
            case StarmapStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_STARMAP_BOOT_UP_DATA:
                        if (mCurrentFragment != null && mCurrentFragment instanceof MapFragment) {
                            MapFragment f = (MapFragment) mCurrentFragment;
                            f.setStarMapData(mStarmapStore.getBootUpData());
                        }
                }
                break;
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
        Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();

        mCommLinkStore = CommLinkStore.get(dispatcher);
        mCommLinkStore.register();

        mShipStore = ShipStore.get(dispatcher);
        mShipStore.register();

        mUserStore = UserStore.get(dispatcher);
        mUserStore.register();

        mOrganizationStore = OrganizationStore.get(dispatcher);
        mOrganizationStore.register();

        mForumsStore = ForumStore.get(dispatcher);
        mForumsStore.register();

        mStarmapStore = StarmapStore.get(dispatcher);
        mStarmapStore.register();
    }

    @Override
    public void exit() {
    }
}
