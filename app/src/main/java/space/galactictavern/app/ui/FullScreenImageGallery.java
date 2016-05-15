package space.galactictavern.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.loader.MediaLoader;

import java.util.ArrayList;
import java.util.List;

import space.galactictavern.app.R;


public class FullScreenImageGallery extends FragmentActivity {
    public static final String KEY_IMAGES = "images";
    public static final String KEY_POSITION = "position";
    public static final String KEY_TITLE = "title";

    private ScrollGalleryView mGalleryView;
    private ArrayList<String> mImages;
    private int mInitialPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_gallery);
        mImages = getIntent().getStringArrayListExtra(KEY_IMAGES);
        mInitialPosition = getIntent().getIntExtra(KEY_POSITION, 0);

        List<MediaInfo> infos = new ArrayList<>(mImages.size());
        for (String url : mImages) {
            infos.add(MediaInfo.mediaLoader(new GlideImageLoader(url)));
        }

        mGalleryView = (ScrollGalleryView) findViewById(R.id.scroll_gallery_view);
        mGalleryView
                .setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos);
    }

    public class GlideImageLoader implements MediaLoader {

        private String url;

        public GlideImageLoader(String url) {
            this.url = url;
        }

        @Override
        public boolean isImage() {
            return true;
        }

        @Override
        public void loadMedia(Context context, final ImageView imageView, final MediaLoader.SuccessCallback callback) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.placeholder_image)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            callback.onSuccess();
                            return false;
                        }
                    })
                    .into(imageView);
        }

        @Override
        public void loadThumbnail(Context context, ImageView thumbnailView, MediaLoader.SuccessCallback callback) {
            Glide.with(context)
                    .load(url)
                    .override(100, 100)
                    .placeholder(R.drawable.placeholder_image)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            callback.onSuccess();
                            return false;
                        }
                    })
                    .into(thumbnailView);
        }
    }
}
