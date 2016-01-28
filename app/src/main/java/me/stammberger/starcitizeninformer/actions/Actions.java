package me.stammberger.starcitizeninformer.actions;

/**
 * Contains all actions used by the application
 */
public interface Actions {
    String GET_COMM_LINKS = "get_comm_links";
    String GET_COMM_LINK_PARTS = "get_comm_link_parts";

    /**
     * Fetches all comm links available
     */
    void getCommLinks();

    /**
     * Fetches all {@link .models.CommLinkModelContentPart} for a particular comm link
     *
     * @param sourceUrl The source url of the comm link
     */
    void getCommLinkParts(String sourceUrl);
}
