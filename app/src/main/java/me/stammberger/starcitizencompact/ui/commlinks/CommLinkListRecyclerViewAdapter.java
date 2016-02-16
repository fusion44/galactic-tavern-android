package me.stammberger.starcitizencompact.ui.commlinks;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModel;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CommLinkModel} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class CommLinkListRecyclerViewAdapter extends RecyclerView.Adapter<CommLinkListRecyclerViewAdapter.ViewHolder> implements RequestListener<String, GlideDrawable> {

    private final SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup();
    private final ArrayList<CommLinkModel> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public CommLinkListRecyclerViewAdapter(Context c, ArrayList<CommLinkModel> items,
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
                .inflate(R.layout.fragment_comm_link_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder h, int position) {
        h.item = mValues.get(position);
        h.titleTextView.setText(h.item.getTitle());

        DateTime dt = new DateTime(h.item.getPublished());
        CharSequence formattedDate = DateUtils.getRelativeTimeSpanString(mContext, dt);
        h.dateTextView.setText(formattedDate);

        if (h.item.getMainBackdrop() != null) {
            Glide.with(mContext)
                    .load(h.item.getMainBackdrop())
                    .listener(this)
                    .into(h.backdropImageView);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            h.backdropImageView.setTransitionName(h.item.getMainBackdrop());
        }

        h.view.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(h.item, h.backdropImageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
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
         * The ImageView is passed for making Activity transitions possible.
         * The ImageView tag and transitionName has been set to its image source url
         * which can be retrieved by the parent Activity/Fragment for use with ActivityOptionsCompat.makeSceneTransitionAnimation
         *
         * @param item that has been clicked on
         * @param view that has been clicked on
         */
        void onListFragmentInteraction(CommLinkModel item, ImageView view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView titleTextView;
        public final TextView dateTextView;
        public final ImageView backdropImageView;
        public CommLinkModel item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            dateTextView = (TextView) view.findViewById(R.id.dateTextView);
            backdropImageView = (ImageView) view.findViewById(R.id.backdropImageView);
        }

        @Override
        public String toString() {
            return super.toString() + "ViewHolder " + item.getSourceUrl();
        }
    }

    private class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return mValues.get(position).spanCount;
        }
    }
}
