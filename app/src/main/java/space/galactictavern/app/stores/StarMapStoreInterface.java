package space.galactictavern.app.stores;

import space.galactictavern.mapcore.map.data.StarMapData;

/**
 * Interface for the starmap store
 */
public interface StarMapStoreInterface {

    /**
     * @return {@link StarMapData} object with boot up data
     */
    StarMapData getBootUpData();
}
