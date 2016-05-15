package space.galactictavern.app.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;
import java.util.List;

import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.app.models.ship.Ship;
import space.galactictavern.app.models.ship.ShipData;

/**
 * Stores the ship data once they've been loaded from the API
 * To adhere to the Flux contract loading must be triggered by the view:
 * https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
 * <p>
 * {@link RxStore#postChange(RxStoreChange)} will update the listener classes. Called in {@link ShipStore#onRxAction(RxAction)}
 * <p>
 * This is a Singleton class
 */
public class ShipStore extends RxStore implements ShipStoreInterface {
    public static final String ID = "ShipStore";
    private static ShipStore mInstance;
    private ShipData mShipData;
    private List<Ship> mFavoriteShips;

    /**
     * Private constructor. Use {@link #get(Dispatcher)} to retrieve an instance
     *
     * @param dispatcher The RxFlux dispatcher
     */
    private ShipStore(Dispatcher dispatcher) {
        super(dispatcher);
        mShipData = new ShipData();
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The ShipStore instance
     */
    public static synchronized ShipStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new ShipStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * Gets all available ships
     *
     * @return {@link ShipData} object with all ships. Empty if ships haven't been loaded yet.
     */
    @Override
    public ShipData getAllShips() {
        return mShipData;
    }

    /**
     * Gets a single ship
     *
     * @param id The ship string id
     * @return the {@link Ship} object. Null if ship is not found
     */
    @Override
    public Ship getShipById(String id) {
        if (mShipData.shipMap.size() == 0 || mShipData.shipMap.get(id) == null) {
            return null;
        }

        return mShipData.shipMap.get(id);
    }

    /**
     * Get all Ships the user has favored.
     *
     * @return A list with users favored Ships. Null if data has not been queried yet.
     */
    @Override
    public List<Ship> getFavoriteShips() {
        return mFavoriteShips;
    }

    /**
     * Method is called when the loading action has finished running.
     *
     * @param action RxAction that has finished loading. Must contain the {@link ShipData} object.
     */
    @SuppressWarnings({"unchecked", "Convert2streamapi"})
    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_SHIP_DATA_ALL:
                mShipData = (ShipData) action.getData().get(Keys.SHIP_DATA_ALL);
                mFavoriteShips = new ArrayList<>();
                for (Ship ship : mShipData.ships) {
                    if (ship.favorite) {
                        mFavoriteShips.add(ship);
                    }
                }
                break;
            case Actions.SHIP_DATA_UPDATED:
                // Update the favorites list
                // TODO: Update this list within the {@link GtActionCreator} - off the UI thread
                mFavoriteShips = new ArrayList<>();
                for (Ship ship : mShipData.ships) {
                    if (ship.favorite) {
                        mFavoriteShips.add(ship);
                    }
                }
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}

