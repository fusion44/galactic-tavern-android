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
}
