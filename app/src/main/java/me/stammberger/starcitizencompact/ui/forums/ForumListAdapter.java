package me.stammberger.starcitizencompact.ui.forums;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.models.forums.Forum;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Forum} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ForumListAdapter extends RecyclerView.Adapter<ForumListAdapter.ViewHolder>
        implements RequestListener<String, GlideDrawable> {

    private final List<Forum> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public ForumListAdapter(Context c, List<Forum> items,
                            OnListFragmentInteractionListener listener) {
        mContext = c;
        mValues = items;
        mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_forum_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder h, int position) {
        h.bind(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        Timber.d(e.getMessage());
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }

    /**
     * Interface for communication between list items and the host fragment
     */
    public interface OnListFragmentInteractionListener {
        /**
         * @param item that has been clicked on
         */
        void onListFragmentInteraction(Forum item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        @Bind(R.id.forumTitleTextView)
        public TextView titleTextView;
        @Bind(R.id.forumActivityTextView)
        public TextView activityTextView;
        @Bind(R.id.forumDescriptionTextView)
        public TextView descriptionTextView;
        @Bind(R.id.forumNewActivityCountTextView)
        public TextView newDiscussionTextView;


        public Forum item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + "ViewHolder " + item.forumTitle;
        }

        public void bind(Forum forum) {
            item = forum;
            titleTextView.setText(forum.forumTitle);
            descriptionTextView.setText(forum.forumDescription);

            activityTextView.setText(mContext.getString(R.string.forum_activity_text,
                    forum.forumDiscussionCount, forum.forumPostCount));

            // TODO: Figure out a solid way to count new posts since last visit
            newDiscussionTextView.setText("99");

            view.setOnClickListener(v -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(item);
                }
            });
        }
    }
}

