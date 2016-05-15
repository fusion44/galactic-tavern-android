package space.galactictavern.app.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.mapcore.map.data.StarMapData;

/**
 * Stores the star map data once they've been loaded from the API
 * To adhere to the Flux contract loading must be triggered by the view:
 * https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
 * <p>
 * {@link RxStore#postChange(RxStoreChange)} will update the listener classes. Called in {@link StarmapStore#onRxAction(RxAction)}
 * <p>
 * This is a Singleton class
 */
public class StarmapStore extends RxStore implements StarMapStoreInterface {
    public static final String ID = "StarmapStore";
    private static StarmapStore mInstance;
    private StarMapData mBootUpData;

    /**
     * Private constructor. Use {@link #get(Dispatcher)} to retrieve an instance
     *
     * @param dispatcher The RxFlux dispatcher
     */
    private StarmapStore(Dispatcher dispatcher) {
        super(dispatcher);
        mBootUpData = new StarMapData();
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The StarmapStore instance
     */
    public static synchronized StarmapStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new StarmapStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * Gets the boot up data
     *
     * @return {@link StarMapData} object with the boot up data. Empty if data hasn't been loaded yet.
     */
    @Override
    public StarMapData getBootUpData() {
        return mBootUpData;
    }

    /**
     * Method is called when the loading action has finished running.
     *
     * @param action RxAction that has finished loading. Must contain the {@link StarMapData} object.
     */
    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_STARMAP_BOOT_UP_DATA:
                mBootUpData = (StarMapData) action.getData().get(Keys.STARMAP_BOOTUP_DATA);
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}

