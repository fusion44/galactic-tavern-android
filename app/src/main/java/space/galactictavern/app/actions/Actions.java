package space.galactictavern.app.actions;

import space.galactictavern.app.models.favorites.Favorite;
import space.galactictavern.app.models.user.UserSearchHistoryEntry;
import space.galactictavern.app.stores.UserStore;

/**
 * Contains all actions used by the application
 */
public interface Actions {
    String GET_COMM_LINK = "get_comm_link";
    String GET_COMM_LINKS = "get_comm_links";
    String GET_COMM_LINK_CONTENT_WRAPPERS = "get_comm_link_content_wrappers";

    // called when data from a comm link is updated. For example Favorite is added / removed.
    String COMM_LINK_DATA_UPDATED = "comm_link_data_updated";

    /**
     * Action id for getting all Ships
     */
    String GET_SHIP_DATA_ALL = "get_ship_data_all";

    /**
     * Action id when Ship data was updated
     */
    String SHIP_DATA_UPDATED = "ship_data_updated";

    /**
     * Action id for retrieving a user data object from the API
     */
    String GET_USER_BY_USER_HANDLE = "get_user_by_user_handle";
    /**
     * An action with this id might also be created by the system when a new entry is added to the database
     */
    String GET_USER_SEARCH_HISTORY = "get_user_search_history";

    /**
     * Action id for retrieving an organizations data object from the API
     */
    String GET_ORGANIZATION_BY_ID = "get_organization_by_id";

    /**
     * Action id for retrieving all forums data
     */
    String GET_FORUMS_ALL = "get_all_forums";

    /**
     * Action id for retrieving all thread for a given forum id and data page
     */
    String GET_FORUM_THREADS = "get_forum_threads";

    /**
     * Action id for retrieving all posts for a given thread and data page
     */
    String GET_FORUM_THREAD_POSTS = "get_forum_thread_posts";

    /**
     * Action id for retrieving the star map boot-up data
     */
    String GET_STARMAP_BOOT_UP_DATA = "get_starmap_boot_up_data";

    /**
     * Fetches a single comm link
     *
     * @param id the comm link id. Note this is not the SQLite id
     */
    void getCommLink(Long id);

    /**
     * Fetches all comm links available
     */
    void getCommLinks(Long lastCommLinkId, int maxResults);

    /**
     * Fetches all {@link .models.commlink.Wrapper} for a particular comm link
     *
     * @param comm_link_id The id of the comm link
     */
    void getCommLinkContentWrappers(Long comm_link_id);

    /**
     * Fetches all available ships
     */
    void getAllShips();

    /**
     * Searches for a user by its handle
     *
     * @param userHandle String with the handle
     */
    void getUserByUserHandle(String userHandle);

    /**
     * Retrieves search history from DB and puts it into {@link UserStore}
     */
    void getUserSearchHistory();

    /**
     * Pushes a new search entry to the DB.
     * Last entry will be discarded.
     *
     * @param e the {@link UserSearchHistoryEntry} object
     */
    void pushNewUserSearchToDb(UserSearchHistoryEntry e);

    /**
     * Searches for an organization by its ID
     *
     * @param id of the organization
     */
    void getOrganizationById(String id);

    /**
     * Gets basic boot-up data for displaying the starmap
     */
    void getStarMapBootUpData();

    /**
     * Get all forum names and the basic forum data
     */
    void getForumsAll();

    /**
     * Gets all threads of the specified forum at the specified data page
     *
     * @param forumId The Forum ID
     * @param page    The page id for the data
     */
    void getForumThreads(String forumId, int page);

    /**
     * Gets all posts of the specified thread at the specified data page
     *
     * @param threadId The Forum ID
     * @param page     The page id for the data
     */
    void getForumThreadPosts(long threadId, int page);

    /**
     * Adds a Favorite to Database
     *
     * @param favorite The data object to add
     */
    void addFavorite(Favorite favorite);

    /**
     * Gets all Favorites saved to the Database
     */
    void getFavorites();

    /**
     * Removes a Favorite with the specified data
     *
     * @param f Favorite to remove
     */
    void removeFavorite(Favorite f);
}
