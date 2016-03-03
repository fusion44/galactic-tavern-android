package me.stammberger.starcitizencompact.actions;

import me.stammberger.starcitizencompact.models.user.UserSearchHistoryEntry;

/**
 * Contains all actions used by the application
 */
public interface Actions {
    String GET_COMM_LINK = "get_comm_link";
    String GET_COMM_LINKS = "get_comm_links";
    String GET_COMM_LINK_CONTENT_WRAPPERS = "get_comm_link_content_wrappers";

    String GET_SHIP_DATA_ALL = "get_ship_data_all";

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
     * Fetches a single comm link
     *
     * @param id the comm link id. Note this is not the SQLite id
     */
    void getCommLink(Long id);

    /**
     * Fetches all comm links available
     */
    void getCommLinks();

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
     * Retrieves search history from DB and puts it into {@link me.stammberger.starcitizencompact.stores.UserStore}
     */
    void getUserSearchHistory();

    /**
     * Pushes a new search entry to the DB. No more than {@value SciActionCreator#MAX_USER_SEARCH_ENTRIES} entries will be stored.
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
     * Get all forum names and the basic forum data
     */
    void getForumsAll();
}
