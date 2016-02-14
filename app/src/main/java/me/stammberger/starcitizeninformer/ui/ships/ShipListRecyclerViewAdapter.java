package me.stammberger.starcitizeninformer.ui.ships;

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

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.core.Utility;
import me.stammberger.starcitizeninformer.models.ship.Ship;
import me.stammberger.starcitizeninformer.models.ship.ShipData;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ShipData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ShipListRecyclerViewAdapter extends RecyclerView.Adapter<ShipListRecyclerViewAdapter.ViewHolder> implements RequestListener<String, GlideDrawable> {

    private final SpanSizeLookup mSpanSizeLookup = new SpanSizeLookup();
    private final OnListFragmentInteractionListener mListener;
    private List<Ship> mModels;
    private Context mContext;

    public ShipListRecyclerViewAdapter(Context c, List<Ship> ships,
                                       OnListFragmentInteractionListener listener) {
        mContext = c;
        mModels = new ArrayList<>(ships);
        mListener = listener;
    }

    public void setModels(List<Ship> ships) {
        mModels = ships;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ship_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder h, int position) {
        h.bindView(mModels.get(position));
    }

    @Override
    public int getItemCount() {
        return mModels.size();
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
        void onListFragmentInteraction(ShipData item, ImageView view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView shipNameTextView;
        public final TextView shipNameSubTextView;
        public final TextView shipManufacturerTextView;
        public final ImageView shipBackdropImageView;
        public Ship item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            shipNameTextView = (TextView) view.findViewById(R.id.shipNameTextView);
            shipNameSubTextView = (TextView) view.findViewById(R.id.shipNameSubTextView);
            shipManufacturerTextView = (TextView) view.findViewById(R.id.shipManufacturerTextView);
            shipBackdropImageView = (ImageView) view.findViewById(R.id.shipBackdropImageView);
        }

        @Override
        public String toString() {
            return super.toString() + "ViewHolder " + item.toString();
        }

        public void bindView(Ship ship) {
            item = ship;
            shipNameTextView.setText(item.titlecontainer.title);
            shipNameSubTextView.setText(mContext.getString(R.string.subtitle_by_manufacturer,
                    Utility.getFullManufacturerName(mContext, ship.titlecontainer.manufacturer)));

            Glide.with(mContext)
                    .load(Utility.RSI_BASE_URL + item.shipimgsmall)
                    .into(shipBackdropImageView);
        }
    }

    private class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return mModels.get(position).spanCount;
        }
    }
}
