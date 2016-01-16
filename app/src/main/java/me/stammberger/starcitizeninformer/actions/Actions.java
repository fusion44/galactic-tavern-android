package me.stammberger.starcitizeninformer.actions;

/**
 * Contains all actions used by the application
 */
public interface Actions {
    String GET_COMM_LINKS = "get_comm_links";

    /**
     * Fetches all comm links available
     */
    void getCommLinks();
}
