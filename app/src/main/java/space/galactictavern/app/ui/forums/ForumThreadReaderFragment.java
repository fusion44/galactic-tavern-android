package space.galactictavern.app.ui.forums;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.models.forums.ForumThread;
import space.galactictavern.app.models.forums.ForumThreadPost;
import space.galactictavern.app.stores.ForumStore;
import timber.log.Timber;

/**
 * A fragment representing a single thread reader screen.
 * This fragment is either contained in a {@link ForumThreadListActivity}
 * in two-pane mode (on tablets) or a {@link ForumThreadReaderActivity}
 * on handsets.
 */
public class ForumThreadReaderFragment extends Fragment implements OnMoreListener {
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

    private SuperRecyclerView mRecyclerView;
    private ForumThreadPostsRecyclerViewAdapter mAdapter;

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

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mForumId.content);
            }
        }
    }

    private void setupRecyclerView(List<ForumThreadPost> posts) {
        if (mCurrentPage == 1) {
            LinearLayoutManager llm = new LinearLayoutManager(
                    getContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(llm);
            mAdapter = new ForumThreadPostsRecyclerViewAdapter(posts);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.addItems(posts);
            mRecyclerView.setLoadingMore(false);
        }
    }

    @Override
    public void onStart() {
        mCurrentPage = 1;
        Dispatcher dispatcher = GtApplication.getInstance().getRxFlux().getDispatcher();
        List<ForumThreadPost> posts = ForumStore.get(dispatcher).getPosts(mForumThreadId, mCurrentPage);
        if (posts.size() == 0) {
            GtApplication.getInstance().getActionCreator().getForumThreadPosts(mForumThreadId, mCurrentPage);
        } else {
            setupRecyclerView(posts);
        }

        super.onStart();
    }

    public void addPosts(long threadId, int page, List<ForumThreadPost> posts) {
        if (posts == null || posts.size() == 0 || threadId != mForumThreadId) {
            // Either something has gone wrong if the thread has no posts in it.
            return;
        }

        if (page == mCurrentPage + 1) {
            mCurrentPage = page;
        }

        setupRecyclerView(posts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("create view");
        //mWebView = (WebView) inflater.inflate(
        //        R.layout.forum_thread_reader_fragment, container, false);
        mRecyclerView = (SuperRecyclerView) inflater.inflate(
                R.layout.fragment_forum_thread_reader, container, false);
        return mRecyclerView;
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        GtApplication.getInstance().getActionCreator().getForumThreadPosts(mForumThreadId, mCurrentPage);
    }

    /**
     * Adapter for displaying {@link ForumThread} data items to the RecyclerView
     */
    public class ForumThreadPostsRecyclerViewAdapter
            extends RecyclerView.Adapter<ForumThreadPostsRecyclerViewAdapter.ViewHolder> {

        private final List<ForumThreadPost> mValues;

        public ForumThreadPostsRecyclerViewAdapter(List<ForumThreadPost> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.forum_thread_post_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.bind(mValues.get(position));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        void addItems(List<ForumThreadPost> posts) {
            int lastPos = mValues.size();
            mValues.addAll(posts);
            notifyItemRangeInserted(lastPos, posts.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public ForumThreadPost post;
            @Bind(R.id.forumPostCreatorTextView)
            TextView postCreatorTextView;
            @Bind(R.id.forumPostCreationDateTextView)
            TextView postCreationDateTextView;
            @Bind(R.id.forumPostContentTextView)
            TextView postContentTextView;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                ButterKnife.bind(this, view);
            }

            public void bind(ForumThreadPost post) {
                this.post = post;
                postCreatorTextView.setText(post.author.moniker);
                postCreationDateTextView.setText(
                        Utility.getFormattedRelativeTimeSpan(
                                getActivity(), post.post.postTime * 1000L));
                postContentTextView.setText(Html.fromHtml(post.post.postText));
            }

            @Override
            public String toString() {
                return super.toString() + " Forum Thread Post '" + this.post.post.postId + "'";
            }
        }
    }
}
