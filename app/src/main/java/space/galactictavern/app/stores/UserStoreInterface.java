package space.galactictavern.app.stores;

import java.util.ArrayList;

import space.galactictavern.app.models.user.User;
import space.galactictavern.app.models.user.UserSearchHistoryEntry;

/**
 * Interface for the user search entry store
 */
interface UserStoreInterface {
    /**
     * Searches for a user using his handle on RSI.com
     *
     * @return {@link User} object with user data. Null if user with this handle was not found
     */
    User getUser(String handle);

    /**
     * Gets a list of recent search terms. Sorted by date of search.
     *
     * @param num Number of entries to get.
     * @return List of search entries. Empty if no searches where conducted.
     */
    ArrayList<UserSearchHistoryEntry> getUserSearchHistory(int num);
}

