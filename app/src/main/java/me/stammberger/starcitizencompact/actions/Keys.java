package me.stammberger.starcitizencompact.actions;

import com.hardsoftstudio.rxflux.action.RxAction;

import me.stammberger.starcitizencompact.models.forums.ForumThread;
import me.stammberger.starcitizencompact.models.forums.ForumThreadPosts;


/**
 * Holds all keys for storage and retrieval of data with {@link RxAction#getData()}
 */
public interface Keys {
    /**
     * Key for current data page
     * <p>
     * If the currently requested data is paginated this data point will be
     * set to the current page included in the action
     */
    String PAGINATION_CURRENT_PAGE = "pagination_current_page";

    /**
     * Key for a single comm link
     */
    String COMM_LINK = "comm_link";

    /**
     * Key for comm link data in {@link RxAction#getData()}
     */
    String COMM_LINKS = "comm_links";

    /**
     * Key for a comm link id. If a list of {@link .models.commlink.Wrapper} is transmitted
     * via {@link RxAction#getData()} the comm link id must be transmitted too
     */
    String COMM_LINK_ID = "comm_link_id";

    /**
     * Key for comm link data in {@link RxAction#getData()}
     */
    String COMM_LINK_CONTENT_WRAPPERS = "comm_link_parts";

    /**
     * Key for the {@link me.stammberger.starcitizencompact.models.ship.ShipData} object
     */
    String SHIP_DATA_ALL = "ship_data_all";

    /**
     * Key for Ship data object which has been updated
     */
    String SHIP_DATA_LIST = "ship_data_list";

    /**
     * Key for the user handle
     */
    String USER_HANDLE = "user_handle";

    /**
     * Key for the user data
     */
    String USER_DATA = "user_data";

    /**
     * Key for storing whether a search was successful or not
     */
    String USER_DATA_SEARCH_SUCCESSFUL = "user_data_search_successful";

    /**
     * Key for recent user search data
     */
    String USER_SEARCH_HISTORY_ENTRIES = "recent_user_searches";

    /**
     * Key for the organization id
     */
    String ORGANIZATION_ID = "org_id";

    /**
     * Key for organization data
     */
    String ORGANIZATION_DATA = "organization_data";

    /**
     * Id of the Forum
     */
    String FORUM_ID = "forum_id";

    /**
     * Key for all forum data
     */
    String FORUM_DATA_ALL = "forums_data_all";

    /**
     * Key for {@link ForumThread} data
     */
    String FORUM_THREADS_FOR_PAGE = "forum_threads_for_page";

    /**
     * Key for a {@link ForumThread} id
     */
    String FORUM_THREAD_ID = "forum_thread_id";

    /**
     * Key for {@link ForumThreadPosts} data
     */
    String FORUM_THREAD_POSTS_FOR_PAGE = "forum_threads_for_page";

}
