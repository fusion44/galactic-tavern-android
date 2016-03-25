package me.stammberger.galactictavern.stores;

import java.util.List;

import me.stammberger.galactictavern.models.ship.Ship;
import me.stammberger.galactictavern.models.ship.ShipData;

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

    /**
     * Gets all ships which have been favored by the User
     *
     * @return List with Ships
     */
    List<Ship> getFavoriteShips();
}
