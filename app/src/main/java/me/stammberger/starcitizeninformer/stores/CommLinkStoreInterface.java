package me.stammberger.starcitizeninformer.stores;


import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.models.commlink.CommLinkModel;
import me.stammberger.starcitizeninformer.models.commlink.Wrapper;

/**
 * Interface for the comm link store
 */
public interface CommLinkStoreInterface {
    /**
     * Gets a single comm link
     *
     * @param id The comm link id. This is not the SQLite id
     * @return the comm link model. Empty model if not found
     */
    CommLinkModel getCommLink(Long id);

    /**
     * @return A list of all loaded comm links. List is empty if nothing has been loaded.
     */
    ArrayList<CommLinkModel> getCommLinks();

    /**
     * @param commLinkId The id of the comm link
     * @return The content wrappers for the comm link
     */
    List<Wrapper> getCommLinkContentWrappers(Long commLinkId);
}
