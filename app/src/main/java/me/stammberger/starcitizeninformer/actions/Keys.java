package me.stammberger.starcitizeninformer.actions;

import com.hardsoftstudio.rxflux.action.RxAction;


/**
 * Holds all keys for storage and retrieval of data with {@link RxAction#getData()}
 */
public interface Keys {
    /**
     * Key for comm link data in {@link RxAction#getData()}
     */
    String COMM_LINKS = "comm_links";
}
