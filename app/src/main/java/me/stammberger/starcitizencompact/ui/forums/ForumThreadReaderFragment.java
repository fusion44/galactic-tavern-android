package me.stammberger.starcitizencompact.ui.forums;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;

import java.util.List;
import java.util.TreeMap;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.models.forums.ForumThreadPost;
import me.stammberger.starcitizencompact.stores.ForumStore;
import timber.log.Timber;

/**
 * A fragment representing a single thread reader screen.
 * This fragment is either contained in a {@link ForumThreadListActivity}
 * in two-pane mode (on tablets) or a {@link ForumThreadReaderActivity}
 * on handsets.
 */
public class ForumThreadReaderFragment extends Fragment {
    /**
     * The fragment argument representing the thread ID that this fragment
     * represents.
     */
    public static final String ARG_THREAD_ID = "thread_id";

    /**
     * Thread Id
     */
    private long mForumThreadId;

    /**
     * Current data page
     */
    private int mCurrentPage;

    /**
     * List of all currently fetched posts
     */
    private TreeMap<Integer, List<ForumThreadPost>> mPosts = new TreeMap<>();


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ForumThreadReaderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_THREAD_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mForumThreadId = getArguments().getLong(ARG_THREAD_ID);

            mCurrentPage = 1;
            Dispatcher dispatcher = SciApplication.getInstance().getRxFlux().getDispatcher();
            List<ForumThreadPost> posts = ForumStore.get(dispatcher).getPosts(mForumThreadId, mCurrentPage);
            if (posts.size() == 0) {
                SciApplication.getInstance().getActionCreator().getForumThreadPosts(mForumThreadId, mCurrentPage);
            } else {
                setupRecyclerView();
            }

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mForumId.content);
            }
        }
    }

    private void setupRecyclerView() {
        Timber.d("Setting up RecyclerView");
    }

    public void addPosts(long threadId, int page, List<ForumThreadPost> posts) {
        if (posts == null || posts.size() == 0 || threadId != mForumThreadId) {
            // Either something has gone wrong if the thread has no posts in it.
            return;
        }
        mPosts.put(page, posts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forum_thread_reader_fragment, container, false);

        // Show the dummy content as text in a TextView.
        if (mForumThreadId != -1) {
            ((TextView) rootView.findViewById(R.id.forumThreadReaderTitleTextView)).setText("Text here");
        }

        return rootView;
    }
}
