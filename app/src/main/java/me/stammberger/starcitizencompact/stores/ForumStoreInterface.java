package me.stammberger.starcitizencompact.stores;

import java.util.List;

import me.stammberger.starcitizencompact.models.forums.Forum;

public interface ForumStoreInterface {
    /**
     * Gets all Forum data objects available
     */
    List<Forum> getForums();
}
