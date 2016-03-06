package me.stammberger.starcitizencompact.ui.forums;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.actions.Actions;
import me.stammberger.starcitizencompact.actions.Keys;
import me.stammberger.starcitizencompact.models.forums.ForumThread;
import me.stammberger.starcitizencompact.stores.ForumStore;
import me.stammberger.starcitizencompact.ui.RxFluxActivity;

/**
 * An activity representing a list of {@link ForumThread}. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ForumThreadReaderActivity} representing
 * ForumThread details. Basically, this will display the posts made to this ForumThread.
 * On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ForumThreadListActivity extends RxFluxActivity {
    public static final String KEY_FORUM_ID = "forum_id";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView mRecyclerView;

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

        mRecyclerView = (RecyclerView) findViewById(R.id.forumThreadListRecyclerView);

        String forumId = getIntent().getStringExtra(KEY_FORUM_ID);

        List<ForumThread> threads = ForumStore.get(mRxFlux.getDispatcher()).getThreads(forumId, 1);
        if (threads.size() == 0) {
            SciApplication.getInstance().getActionCreator().getForumThreads(forumId, 1);
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
                String o = (String) rxAction.getData().get(Keys.FORUM_ID);
                int page = (int) rxAction.getData().get(Keys.PAGINATION_CURRENT_PAGE);
                List<ForumThread> threads = (List<ForumThread>) rxAction.getData().get(Keys.FORUM_THREADS_FOR_PAGE);
                setupRecyclerView(threads);
            }
        }

    }

    private void setupRecyclerView(List<ForumThread> threads) {
        mRecyclerView.setAdapter(new ForumThreadsRecyclerViewAdapter(threads));
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
                    .inflate(R.layout.forum_thread_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.bind(mValues.get(position));

            holder.view.setOnClickListener(v -> {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(ForumThreadReaderFragment.ARG_ITEM_ID, holder.forumThread.threadId);
                    ForumThreadReaderFragment fragment = new ForumThreadReaderFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.forumThreadReaderContainer, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ForumThreadReaderActivity.class);
                    intent.putExtra(ForumThreadReaderFragment.ARG_ITEM_ID, holder.forumThread.threadId);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;

            @Bind(R.id.forumThreadListAvatarImageView)
            public ImageView avatarImageView;
            @Bind(R.id.forumThreadListTopicTextView)
            public TextView titleTextView;
            @Bind(R.id.forumThreadListViewCount)
            public TextView viewCountTextView;
            @Bind(R.id.forumThreadListPostCount)
            public TextView postCountTextView;

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
            }

            @Override
            public String toString() {
                return super.toString() + " '" + titleTextView.getText() + "'";
            }
        }
    }
}
