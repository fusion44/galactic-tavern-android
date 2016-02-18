package me.stammberger.starcitizencompact.stores;

import me.stammberger.starcitizencompact.models.ship.Ship;
import me.stammberger.starcitizencompact.models.ship.ShipData;

/**
 * Interface for the ship store
 */
public interface ShipStoreInterface {

    /**
     * @return {@link ShipData} object with all ships
     */
    ShipData getAllShips();

    /**
     * Get a ship by ship ID
     *
     * @param id The ship string id
     * @return {@link Ship} object
     */
    Ship getShipById(String id);
}
