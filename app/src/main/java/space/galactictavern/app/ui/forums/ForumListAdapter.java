package space.galactictavern.app.ui.forums;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import space.galactictavern.app.R;
import space.galactictavern.app.models.forums.Forum;
import space.galactictavern.app.models.forums.ForumSectioned;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Forum} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ForumListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RequestListener<String, GlideDrawable> {

    private final List<ForumSectioned> mValues;
    private final OnListFragmentInteractionListener mListener;
    private SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup();
    private Context mContext;

    public ForumListAdapter(Context c, List<ForumSectioned> items,
                            OnListFragmentInteractionListener listener) {
        mContext = c;
        mValues = items;
        mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ForumSectioned.TYPE_FORUM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_forum_item, parent, false);
            return new ForumViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_forum_section_header, parent, false);
            return new SectionViewHolder(view);
        }
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, int position) {
        ForumSectioned forumSectioned = mValues.get(position);
        if (forumSectioned.type == ForumSectioned.TYPE_FORUM) {
            if (h instanceof ForumViewHolder) {
                ((ForumViewHolder) h).bind(forumSectioned.forum);
            } else {
                throw new IllegalArgumentException("ViewHolder type is not correct");
            }
        } else {
            if (h instanceof SectionViewHolder) {
                ((SectionViewHolder) h).sectionTextView.setText(forumSectioned.section);
            } else {
                throw new IllegalArgumentException("ViewHolder type is not correct");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position).type;
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


    /**
     * Represents a section header as defined at the RSI forums.
     */
    public class SectionViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        @BindView(R.id.forumSectionHeaderTextView)
        public TextView sectionTextView;


        public Forum item;

        public SectionViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + "HeaderViewHolder " + item.forumTitle;
        }
    }

    /**
     * Represents a Forum item.
     */
    public class ForumViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        @BindView(R.id.forumTitleTextView)
        public TextView titleTextView;
        @BindView(R.id.forumActivityTextView)
        public TextView activityTextView;
        @BindView(R.id.forumDescriptionTextView)
        public TextView descriptionTextView;
        @BindView(R.id.forumNewActivityCountTextView)
        public TextView newDiscussionTextView;


        public Forum item;

        public ForumViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + "ForumViewHolder " + item.forumTitle;
        }

        public void bind(Forum forum) {
            item = forum;
            titleTextView.setText(forum.forumTitle.replace("&amp;", "&"));
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

    private class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return mValues.get(position).spanCount;
        }
    }
}

