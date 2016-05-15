package me.stammberger.galactictavern.stores;

import me.stammberger.starcitizencompact.map.data.StarMapData;

/**
 * Interface for the starmap store
 */
public interface StarMapStoreInterface {

    /**
     * @return {@link me.stammberger.starcitizencompact.map.data.StarMapData} object with boot up data
     */
    StarMapData getBootUpData();
}
