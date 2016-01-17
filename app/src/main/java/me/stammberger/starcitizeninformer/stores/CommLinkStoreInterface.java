package me.stammberger.starcitizeninformer.stores;


import java.util.ArrayList;

import me.stammberger.starcitizeninformer.models.CommLinkModel;

/**
 * Interface for the comm link store
 */
public interface CommLinkStoreInterface {
    /**
     * @return A list of all loaded comm links. List is empty if nothing has been loaded.
     */
    ArrayList<CommLinkModel> getCommLinks();
}
