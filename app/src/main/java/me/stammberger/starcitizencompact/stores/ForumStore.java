package me.stammberger.starcitizencompact.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import me.stammberger.starcitizencompact.actions.Actions;
import me.stammberger.starcitizencompact.actions.Keys;
import me.stammberger.starcitizencompact.models.forums.ForumSectioned;
import me.stammberger.starcitizencompact.models.forums.ForumThread;
import me.stammberger.starcitizencompact.models.forums.ForumThreadPost;

/**
 * Stores all forum data once they've been loaded by an action
 * This will NOT trigger the loading process if forums are requested
 * To adhere to the Flux contract loading must be triggered by the view:
 * https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
 * <p>
 * {@link RxStore#postChange(RxStoreChange)} will update the listener classes. Called in {@link ForumStore#onRxAction(RxAction)}
 * <p>
 * This is a Singleton class
 */
public class ForumStore extends RxStore implements ForumStoreInterface {
    public static final String ID = "ForumStore";
    private static ForumStore mInstance;
    List<ForumSectioned> mForums = new ArrayList<>();

    /**
     * String: forum ID
     * Integer: Page ID of data
     * List<ForumThread>: List of threads belonging to this forum
     */
    TreeMap<String, TreeMap<Integer, List<ForumThread>>> mThreads = new TreeMap<>();

    /**
     * Long: thread ID
     * Integer: Page ID of data
     * List<ForumThreadPost>: List of posts belonging to this thread
     */
    TreeMap<Long, TreeMap<Integer, List<ForumThreadPost>>> mPosts = new TreeMap<>();

    public ForumStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The {@link ForumStore} instance
     */
    public static synchronized ForumStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new ForumStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * Gets all forums available in the store.
     *
     * @return List with forum data objects. Empty list if forums haven't been loaded yet.
     */
    @Override
    public List<ForumSectioned> getForums() {
        return mForums;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForumThread> getThreads(String forumId, int page) {
        if (mThreads.keySet().contains(forumId)) {
            TreeMap<Integer, List<ForumThread>> forumThreads = mThreads.get(forumId);
            if (forumThreads.keySet().contains(page)) {
                return forumThreads.get(page);
            }
        }
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForumThreadPost> getPosts(long threadId, int page) {
        if (mPosts.keySet().contains(threadId)) {
            TreeMap<Integer, List<ForumThreadPost>> threadPosts = mPosts.get(threadId);
            if (threadPosts.keySet().contains(page)) {
                return threadPosts.get(page);
            }
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_FORUMS_ALL:
                List<ForumSectioned> forums = (List<ForumSectioned>) action.getData().get(Keys.FORUM_DATA_ALL);
                if (forums != null) {
                    mForums = forums;
                }
                break;
            case Actions.GET_FORUM_THREADS:
                String forumId = (String) action.getData().get(Keys.FORUM_ID);
                Integer page = (Integer) action.getData().get(Keys.PAGINATION_CURRENT_PAGE);
                List<ForumThread> forumThreads = (List<ForumThread>) action.getData().get(Keys.FORUM_THREADS_FOR_PAGE);

                TreeMap<Integer, List<ForumThread>> threadsForForum;
                if (mThreads.get(forumId) == null) {
                    threadsForForum = new TreeMap<>();
                } else {
                    threadsForForum = mThreads.get(forumId);
                }

                threadsForForum.put(page, forumThreads);

                mThreads.put(forumId, threadsForForum);
                break;
            case Actions.GET_FORUM_THREAD_POSTS:
                Long threadId = (Long) action.getData().get(Keys.FORUM_THREAD_ID);
                Integer posts_dat_page = (Integer) action.getData().get(Keys.PAGINATION_CURRENT_PAGE);
                List<ForumThreadPost> threadPosts = (List<ForumThreadPost>) action.getData().get(Keys.FORUM_THREAD_POSTS_FOR_PAGE);

                TreeMap<Integer, List<ForumThreadPost>> postsForThread;
                if (mPosts.get(threadId) == null) {
                    postsForThread = new TreeMap<>();
                } else {
                    postsForThread = mPosts.get(threadId);
                }

                postsForThread.put(posts_dat_page, threadPosts);

                mPosts.put(threadId, postsForThread);
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}
