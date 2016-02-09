package me.stammberger.starcitizeninformer.actions;

/**
 * Contains all actions used by the application
 */
public interface Actions {
    String GET_COMM_LINK = "get_comm_link";
    String GET_COMM_LINKS = "get_comm_links";
    String GET_COMM_LINK_CONTENT_WRAPPERS = "get_comm_link_content_wrappers";

    /**
     * Fetches a single comm link
     *
     * @param id the comm link id. Note this is not the SQLite id
     */
    void getCommLink(Long id);

    /**
     * Fetches all comm links available
     */
    void getCommLinks();

    /**
     * Fetches all {@link .models.commlink.Wrapper} for a particular comm link
     *
     * @param comm_link_id The id of the comm link
     */
    void getCommLinkContentWrappers(Long comm_link_id);
}
