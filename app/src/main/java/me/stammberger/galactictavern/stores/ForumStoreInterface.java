package me.stammberger.galactictavern.stores;

import java.util.List;

import me.stammberger.galactictavern.models.forums.ForumSectioned;
import me.stammberger.galactictavern.models.forums.ForumThread;
import me.stammberger.galactictavern.models.forums.ForumThreadPost;

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

    /**
     * Gets {@link ForumThreadPost} data for specified Forum and data page
     *
     * @param threadId Id of the Forum Thread
     * @param page     Page to get the data from
     * @return a List with the requested Posts
     */
    List<ForumThreadPost> getPosts(long threadId, int page);
}
