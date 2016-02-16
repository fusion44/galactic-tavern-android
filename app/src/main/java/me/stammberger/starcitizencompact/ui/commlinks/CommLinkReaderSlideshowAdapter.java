package me.stammberger.starcitizencompact.ui.commlinks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import me.stammberger.starcitizencompact.R;
import timber.log.Timber;

/**
 * An Adapter for displaying images in RecyclerViews. Mainly for use within {@link CommLinkReaderAdapter}
 * to embed horizontally scrollable galleries in vertical RecyclerView
 */
public class CommLinkReaderSlideshowAdapter extends RecyclerView.Adapter<CommLinkReaderSlideshowAdapter.ImageViewHolder> implements RequestListener<String, GlideDrawable> {
    /**
     * Context for Glide image loading library
     */
    private final Context context;

    /**
     * Array of URL's to images.
     */
    private List<String> links;

    public CommLinkReaderSlideshowAdapter(Context context) {
        super();
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_comm_link_reader_native_slideshow_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.link = links.get(position);
        Glide.with(context)
                .load(holder.link)
                .listener(this)
                .into(holder.imageView);

        Timber.d("Binding  %s", links.get(position));
    }

    @Override
    public int getItemCount() {
        if (links == null) {
            return 0;
        }

        return links.size();
    }

    /**
     * Sets the list of string which will be displayed in this slideshow
     *
     * @param links Array of Url's as Strings
     */
    public void setLinks(List<String> links) {
        this.links = links;
        notifyDataSetChanged();
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
     * Simple ViewHolder to display an image.
     */
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        String link;

        /**
         * Constructor of the ImageHolder
         *
         * @param view View to display the image in. Must be an instance of {@link ImageView}
         */
        public ImageViewHolder(View view) {
            super(view);
            if (view instanceof ImageView) {
                imageView = (ImageView) view;
            } else {
                throw new IllegalArgumentException("View must be of type ImageView");
            }
        }
    }
}
