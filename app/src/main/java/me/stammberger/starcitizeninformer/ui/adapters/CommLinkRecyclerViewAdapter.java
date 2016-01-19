package me.stammberger.starcitizeninformer.ui.adapters;

import android.content.Context;
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

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.ui.fragments.CommLinkListFragment;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CommLinkModel} and makes a call to the
 * specified {@link CommLinkListFragment.OnListFragmentInteractionListener}.
 */
public class CommLinkRecyclerViewAdapter extends RecyclerView.Adapter<CommLinkRecyclerViewAdapter.ViewHolder> implements RequestListener<String, GlideDrawable> {

    private final SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup();
    private final ArrayList<CommLinkModel> mValues;
    private final CommLinkListFragment.OnListFragmentInteractionListener mListener;
    private Context mContext;

    public CommLinkRecyclerViewAdapter(Context c, ArrayList<CommLinkModel> items,
                                       CommLinkListFragment.OnListFragmentInteractionListener listener) {
        mContext = c;
        mValues = items;
        mListener = listener;
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
        h.titleTextView.setText(h.item.title);

        DateTime dt = new DateTime(h.item.date);
        CharSequence formattedDate = DateUtils.getRelativeTimeSpanString(mContext, dt);
        h.dateTextView.setText(formattedDate);

        Glide.with(mContext)
                .load(h.item.backdropUrl)
                .listener(this)
                .into(h.backdropImageView);

        h.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(h.item);
                }
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
            return super.toString() + "ViewHolder " + item.sourceUri;
        }
    }

    protected class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return mValues.get(position).spanCount;
        }
    }
}
