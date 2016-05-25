package space.galactictavern.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.view.View;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
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
                } else if (fragment.equals(LibsFragment.class.getSimpleName())) {
                    openAboutFragment();
                }
            }
        } else {
            String f = Prefs.getString(KEY_CURRENT_FRAGMENT, "");
            if (f.equals("") || !Prefs.getBoolean(getString(R.string.pref_key_onboarding_finished), false)) {
                openOnboardingFragment();
            } else if (f.equals(CommLinkListFragment.class.getSimpleName())) {
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
            } else if (f.equals(LibsFragment.class.getSimpleName())) {
                openAboutFragment();
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
        } else if (id == R.id.nav_about) {
            openAboutFragment();
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

        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    private void openSettings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    /**
     * Creates the About Fragment using {@link LibsFragment}
     */
    private void openAboutFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String simpleClassName = LibsFragment.class.getSimpleName();

        mCurrentFragment = fragmentManager.findFragmentByTag(simpleClassName);
        if (mCurrentFragment == null) {
            Field[] fields = R.string.class.getFields();
            String[] excluded = new String[]{
                    "ActionBarSherlock"
            };

            LibsBuilder b = new LibsBuilder()
                    .withFields(fields);
            b.excludeLibraries = excluded;
            b.withAboutSpecial1(getString(R.string.privacy_policy));
            b.withAboutSpecial1Description(getString(R.string.privacy_policy_text));

            b.withAboutSpecial2(getString(R.string.changelog));
            b.withAboutSpecial2Description(getString(R.string.changelog_text));

            mCurrentFragment = b.supportFragment();
        }

        fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
        fragmentTransaction.commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.navigation_drawer_about);
        }

        Prefs.putString(KEY_CURRENT_FRAGMENT, simpleClassName);
    }

    /**
     * Opens the onboarding fragment
     */
    private void openOnboardingFragment() {
        if (!Prefs.getBoolean(getString(R.string.pref_key_onboarding_finished), false)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            String simpleClassName = OnboardingFragment.class.getSimpleName();
            mCurrentFragment = OnboardingFragment.newInstance();
            fragmentTransaction.replace(R.id.fragment_container, mCurrentFragment, simpleClassName);
            fragmentTransaction.commit();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRxStoreChanged(@NonNull RxStoreChange change) {
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
                    case (Actions.GET_COMM_LINK_FAVORITES):
                        if (mCurrentFragment != null && mCurrentFragment instanceof CommLinkListFragment) {
                            CommLinkListFragment f = (CommLinkListFragment) mCurrentFragment;
                            f.replaceCommLinks(mCommLinkStore.getFavorites());
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

    @Nullable
    @Override
    public List<RxStore> getRxStoreListToRegister() {
        Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();

        mCommLinkStore = CommLinkStore.get(dispatcher);
        mShipStore = ShipStore.get(dispatcher);
        mUserStore = UserStore.get(dispatcher);
        mOrganizationStore = OrganizationStore.get(dispatcher);
        mForumsStore = ForumStore.get(dispatcher);
        mStarmapStore = StarmapStore.get(dispatcher);

        return Arrays.asList(mCommLinkStore, mShipStore, mUserStore,
                mOrganizationStore, mForumsStore, mStarmapStore);
    }

    @Nullable
    @Override
    public List<RxStore> getRxStoreListToUnRegister() {
        return null;
    }

    /**
     * Hacky way to let the MainActivity know that the {@link OnboardingFragment} is
     * finished and can be closed. This will start the {@link CommLinkListFragment}
     */
    public void onboardingFinished() {
        Prefs.putBoolean(getString(R.string.pref_key_onboarding_finished), true);
        openCommLinkFragment();
        View v = findViewById(R.id.fragment_container);
        if (v != null) {
            Snackbar.make(v, getString(R.string.onboarding_thanks), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void exit() {
    }
}
