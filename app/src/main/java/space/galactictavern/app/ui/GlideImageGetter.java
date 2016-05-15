package space.galactictavern.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import java.util.HashSet;
import java.util.Set;

import space.galactictavern.app.R;
import space.galactictavern.app.core.Utility;

/**
 * Implements an {@link Html.ImageGetter} to load embedded images into TextViews
 * <p>
 * Please see https://github.com/bumptech/glide/issues/550 for further explanation.
 */
public final class GlideImageGetter implements Html.ImageGetter, Drawable.Callback {
    private static Set<ImageGetterViewTarget> mTargets;
    private final Context mContext;
    private final TextView mTextView;

    public GlideImageGetter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;

        clear(); // Cancel all previous request
        mTargets = new HashSet<>();
        mTextView.setTag(R.id.drawable_callback_tag, this);
    }

    public static GlideImageGetter get(View view) {
        return (GlideImageGetter) view.getTag(R.id.drawable_callback_tag);
    }

    @SuppressWarnings("Convert2streamapi") // no stream API :(
    public void clear() {
        GlideImageGetter prev = get(mTextView);
        if (prev == null) return;

        for (ImageGetterViewTarget target : mTargets) {
            Glide.clear(target);
        }
    }

    @Override
    public Drawable getDrawable(String url) {
        final UrlDrawable urlDrawable = new UrlDrawable();

        String fullUrl = url;
        if (!url.startsWith(Utility.RSI_BASE_URL)) {
            fullUrl = Utility.RSI_BASE_URL + url;
        }

        Glide.with(mContext)
                .load(fullUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new ImageGetterViewTarget(mTextView, urlDrawable));

        return urlDrawable;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        mTextView.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {

    }

    private static class ImageGetterViewTarget extends ViewTarget<TextView, GlideDrawable> {

        private final UrlDrawable mDrawable;
        private Request request;

        private ImageGetterViewTarget(TextView view, UrlDrawable drawable) {
            super(view);
            mTargets.add(this); // Add ViewTarget into Set
            this.mDrawable = drawable;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            // Resize images - Scale image proportionally to fit TextView width
            float width;
            float height;
            if (resource.getIntrinsicWidth() >= getView().getWidth()) {
                float downScale = (float) resource.getIntrinsicWidth() / getView().getWidth();
                width = (float) resource.getIntrinsicWidth() / downScale;
                height = (float) resource.getIntrinsicHeight() / downScale;
            } else {
                float multiplier = (float) getView().getWidth() / resource.getIntrinsicWidth();
                width = (float) resource.getIntrinsicWidth() * multiplier;
                height = (float) resource.getIntrinsicHeight() * multiplier;
            }
            Rect rect = new Rect(0, 0, Math.round(width), Math.round(height));

            resource.setBounds(rect);

            mDrawable.setBounds(rect);
            mDrawable.setDrawable(resource);

            if (resource.isAnimated()) {
                // set callback to drawable in order to
                // signal its container to be redrawn
                // to show the animated GIF

                mDrawable.setCallback(get(getView()));
                resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                resource.start();
            }

            getView().setText(getView().getText());
            getView().invalidate();
        }

        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public void setRequest(Request request) {
            this.request = request;
        }
    }

    final class UrlDrawable extends Drawable implements Drawable.Callback {

        private GlideDrawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            if (mDrawable != null) {
                mDrawable.setAlpha(alpha);
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            if (mDrawable != null) {
                mDrawable.setColorFilter(cf);
            }
        }

        @Override
        public int getOpacity() {
            if (mDrawable != null) {
                return mDrawable.getOpacity();
            }
            return 0;
        }

        public void setDrawable(GlideDrawable drawable) {
            if (this.mDrawable != null) {
                this.mDrawable.setCallback(null);
            }
            drawable.setCallback(this);
            this.mDrawable = drawable;
        }

        @Override
        public void invalidateDrawable(Drawable who) {
            if (getCallback() != null) {
                getCallback().invalidateDrawable(who);
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            if (getCallback() != null) {
                getCallback().scheduleDrawable(who, what, when);
            }
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            if (getCallback() != null) {
                getCallback().unscheduleDrawable(who, what);
            }
        }
    }
}