package space.galactictavern.app.ui.forums;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.models.forums.ForumThread;
import space.galactictavern.app.stores.ForumStore;
import space.galactictavern.app.ui.RxFluxActivity;

/**
 * An activity representing a list of {@link ForumThread}. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ForumThreadReaderActivity} representing
 * ForumThread details. Basically, this will display the posts made to this ForumThread.
 * On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ForumThreadListActivity extends RxFluxActivity implements OnMoreListener {
    public static final String KEY_FORUM_ID = "forum_id";
    public static final String VIEWER_TYPE_WEB_VIEW = "web_view";
    public static final String VIEWER_TYPE_RECYCLER_VIEW = "recycler_view";
    public static String viewerType = VIEWER_TYPE_WEB_VIEW;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SuperRecyclerView mRecyclerView;
    private int mCurrentPage;
    private String mForumId;
    private ForumThreadsRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_thread_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = (SuperRecyclerView) findViewById(R.id.list);
        mRecyclerView.setOnMoreListener(this);

        mForumId = getIntent().getStringExtra(KEY_FORUM_ID);

        mCurrentPage = 1;
        List<ForumThread> threads = ForumStore.get(mRxFlux.getDispatcher()).getThreads(mForumId, mCurrentPage);
        if (threads.size() == 0) {
            GtApplication.getInstance().getActionCreator().getForumThreads(mForumId, mCurrentPage);
        } else {
            setupRecyclerView(threads);
        }

        if (findViewById(R.id.forumThreadReaderContainer) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRxStoreChanged(RxStoreChange change) {
        super.onRxStoreChanged(change);
        RxAction rxAction = change.getRxAction();
        if (change.getStoreId().equals(ForumStore.ID)) {
            if (rxAction.getType().equals(Actions.GET_FORUM_THREADS)) {
                int page = (int) rxAction.getData().get(Keys.PAGINATION_CURRENT_PAGE);
                if (page == mCurrentPage + 1) {
                    mCurrentPage = page;
                }
                List<ForumThread> threads = (List<ForumThread>) rxAction.getData().get(Keys.FORUM_THREADS_FOR_PAGE);
                setupRecyclerView(threads);
            }
        }
    }

    private void setupRecyclerView(List<ForumThread> threads) {
        if (mCurrentPage == 1) {
            LinearLayoutManager llm = new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(llm);
            mAdapter = new ForumThreadsRecyclerViewAdapter(threads);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.addItems(threads);
            mRecyclerView.setLoadingMore(false);
        }
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        GtApplication.getInstance().getActionCreator().getForumThreads(mForumId, mCurrentPage + 1);
    }

    /**
     * Adapter for displaying {@link ForumThread} data items to the RecyclerView
     */
    public class ForumThreadsRecyclerViewAdapter
            extends RecyclerView.Adapter<ForumThreadsRecyclerViewAdapter.ViewHolder> {

        private final List<ForumThread> mValues;

        public ForumThreadsRecyclerViewAdapter(List<ForumThread> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_forum_thread_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.bind(mValues.get(position));

            holder.view.setOnClickListener(v -> {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putLong(ForumThreadReaderFragment.ARG_THREAD_ID, holder.forumThread.threadId);
                    Fragment fragment;
                    if (viewerType.equals(VIEWER_TYPE_WEB_VIEW)) {
                        fragment = new ForumThreadReaderWebViewFragment();
                    } else {
                        fragment = new ForumThreadReaderFragment();
                    }
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.forumThreadReaderContainer, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ForumThreadReaderActivity.class);
                    intent.putExtra(ForumThreadReaderFragment.ARG_THREAD_ID, holder.forumThread.threadId);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        void addItems(List<ForumThread> threads) {
            int lastPos = mValues.size();
            mValues.addAll(threads);
            notifyItemRangeInserted(lastPos, threads.size());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;

            @BindView(R.id.forumThreadListAvatarImageView)
            public ImageView avatarImageView;
            @BindView(R.id.forumThreadListTopicTextView)
            public TextView titleTextView;
            @BindView(R.id.forumThreadListViewCount)
            public TextView viewCountTextView;
            @BindView(R.id.forumThreadListPostCount)
            public TextView postCountTextView;
            @BindView(R.id.forumThreadListPostDate)
            public TextView postDateTextView;

            public ForumThread forumThread;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                ButterKnife.bind(this, view);
            }

            public void bind(ForumThread forumThread) {
                this.forumThread = forumThread;
                Glide.with(view.getContext())
                        .load(forumThread.originalPoster.avatar)
                        .into(avatarImageView);

                titleTextView.setText(forumThread.threadTitle);
                viewCountTextView.setText(String.valueOf(forumThread.threadViews));
                postCountTextView.setText(String.valueOf(forumThread.threadReplies));
                postDateTextView.setText(
                        Utility.getFormattedRelativeTimeSpan(
                                getBaseContext(), (long) forumThread.recentPost.postTime * 1000L));
            }

            @Override
            public String toString() {
                return super.toString() + " '" + titleTextView.getText() + "'";
            }
        }
    }
}
