package space.galactictavern.app.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;
import java.util.TreeMap;

import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.GtActionCreator;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.app.models.user.User;
import space.galactictavern.app.models.user.UserSearchHistoryEntry;

public class UserStore extends RxStore implements UserStoreInterface {
    public static final String ID = "UserStore";
    private static UserStore mInstance;
    private TreeMap<String, User> mUserData = new TreeMap<>();
    private ArrayList<UserSearchHistoryEntry> mLastSearches = new ArrayList<>();

    private UserStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The {@link UserStore} instance
     */
    public static synchronized UserStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new UserStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * Searches for a user using his handle on RSI.com
     * If this method returns null, the user is not yet in the store and must be searched for
     * using the {@link GtActionCreator}
     *
     * @return {@link User} object with user data. Null if user with this handle was not found in the store
     */
    @Override
    public User getUser(String handle) {
        if (!mUserData.containsKey(handle)) {
            return null;
        } else {
            return mUserData.get(handle);
        }
    }

    /**
     * Gets a list of recent search terms. Sorted by date of search.
     *
     * @param num Number of entries to get.
     * @return List of search entries. Empty if no recent searches are in database.
     */
    @Override
    @SuppressWarnings("Convert2streamapi") // streams not supported in Java 1.7 :-(
    public ArrayList<UserSearchHistoryEntry> getUserSearchHistory(int num) {
        if (num >= mLastSearches.size()) {
            return mLastSearches;
        } else {
            ArrayList<UserSearchHistoryEntry> entries = new ArrayList<>(num);
            for (int i = 0; i < num; i++) {
                UserSearchHistoryEntry entry = mLastSearches.get(i);
                entries.add(entry);
            }

            return entries;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_USER_BY_USER_HANDLE:
                String handle = (String) action.getData().get(Keys.USER_HANDLE);
                User data = (User) action.getData().get(Keys.USER_DATA);
                mUserData.put(handle, data);
                break;
            case Actions.GET_USER_SEARCH_HISTORY:
                mLastSearches = (ArrayList<UserSearchHistoryEntry>) action.getData().get(Keys.USER_SEARCH_HISTORY_ENTRIES);
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}
