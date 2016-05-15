package space.galactictavern.app.stores;


import java.util.ArrayList;
import java.util.List;

import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.models.commlink.Wrapper;

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

    /**
     * @return A list with all favorite comm links
     */
    List<CommLinkModel> getFavorites();
}
