package me.stammberger.starcitizencompact.stores;

import java.util.List;

import me.stammberger.starcitizencompact.models.forums.ForumSectioned;
import me.stammberger.starcitizencompact.models.forums.ForumThread;

public interface ForumStoreInterface {
    /**
     * Gets all Forum data objects available
     */
    List<ForumSectioned> getForums();

    /**
     * Gets {@link ForumThread} data for specified Forum and data page
     *
     * @param forumId Id of the Forum
     * @param page    Page to get the data from
     * @return a List with the requested Threads
     */
    List<ForumThread> getThreads(String forumId, int page);
}
