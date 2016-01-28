package me.stammberger.starcitizeninformer.stores;


import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.models.CommLinkModelContentPart;

/**
 * Interface for the comm link store
 */
public interface CommLinkStoreInterface {
    /**
     * @return A list of all loaded comm links. List is empty if nothing has been loaded.
     */
    ArrayList<CommLinkModel> getCommLinks();

    /**
     * @param sourceUrl The source url of the comm link
     * @return The parts for the comm link
     */
    List<CommLinkModelContentPart> getCommLinkModelParts(String sourceUrl);
}
