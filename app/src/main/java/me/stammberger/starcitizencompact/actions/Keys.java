package me.stammberger.starcitizencompact.actions;

import com.hardsoftstudio.rxflux.action.RxAction;


/**
 * Holds all keys for storage and retrieval of data with {@link RxAction#getData()}
 */
public interface Keys {
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
}
