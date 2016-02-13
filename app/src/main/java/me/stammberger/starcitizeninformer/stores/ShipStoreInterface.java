package me.stammberger.starcitizeninformer.stores;

import me.stammberger.starcitizeninformer.models.ship.ShipData;

/**
 * Interface for the ship store
 */
public interface ShipStoreInterface {

    /**
     * @return {@link ShipData} object with all ships
     */
    ShipData getAllShips();
}
