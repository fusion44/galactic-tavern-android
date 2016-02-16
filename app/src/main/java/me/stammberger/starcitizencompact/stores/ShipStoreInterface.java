package me.stammberger.starcitizencompact.stores;

import me.stammberger.starcitizencompact.models.ship.ShipData;

/**
 * Interface for the ship store
 */
public interface ShipStoreInterface {

    /**
     * @return {@link ShipData} object with all ships
     */
    ShipData getAllShips();
}
