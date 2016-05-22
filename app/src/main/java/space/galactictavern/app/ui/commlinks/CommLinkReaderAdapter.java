package space.galactictavern.app.ui.commlinks;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import space.galactictavern.app.R;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.core.chrome.CustomTabActivityHelper;
import space.galactictavern.app.core.chrome.WebviewFallback;
import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.models.commlink.ContentBlock2;
import space.galactictavern.app.models.commlink.Wrapper;
import space.galactictavern.app.ui.GlideImageGetter;
import space.galactictavern.app.ui.InterceptLinkMovementMethod;
import timber.log.Timber;


/**
 * This Adapter is responsible for managing the Views in the CommLinkReader RecyclerView
 * It'll discern between {@link SingleViewHolder} and {@link SlideshowViewHolder} when creating and binding a view.
 */
public class CommLinkReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements InterceptLinkMovementMethod.LinkClickedCallback {
    private final CommLinkModel mCommLinkModel;
    private AppCompatActivity mActivity;

    public CommLinkReaderAdapter(CommLinkModel model, AppCompatActivity c) {
        super();
        mCommLinkModel = model;
        mActivity = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ContentBlock2.TYPE_SINGLE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_comm_link_reader_native_single, parent, false);
                return new SingleViewHolder(view, this);
            case ContentBlock2.TYPE_SLIDESHOW:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_comm_link_reader_native_slideshow, parent, false);
                return new SlideshowViewHolder(view);
            default:
                Timber.d("Unknown viewType");
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (mCommLinkModel.wrappers.get(position).getContentBlock2() != null) {
            return mCommLinkModel.wrappers.get(position).getContentBlock2().headerImageType;
        } else {
            return ContentBlock2.TYPE_SINGLE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SingleViewHolder) {
            SingleViewHolder vh = (SingleViewHolder) holder;
            vh.bindWrapper(mCommLinkModel.wrappers.get(position));
        } else if (holder instanceof SlideshowViewHolder) {
            SlideshowViewHolder vh = (SlideshowViewHolder) holder;
            vh.bindSlideShow(mCommLinkModel.wrappers.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mCommLinkModel.wrappers.size();
    }

    @Override
    public void onLinkClick(String url) {
        if (url.startsWith("https://forums.robertsspaceindustries.com/discussion/")) {
            String[] split = url.replace("https://forums.robertsspaceindustries.com/discussion/", "").split("/");
            if (split.length > 0) {
                Long discussionId = Long.valueOf(split[0]);
                Timber.d("%s", discussionId);
            } else {
                Timber.d("Error parsing discussion id from url %s", url);
            }
        } else if (url.contains("youtube.com")) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mActivity.startActivity(i);
        } else {
            // simple check whether this is a usable link
            if (url.contains("robertsspaceindustries")) {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                CustomTabActivityHelper.openCustomTab(
                        mActivity,
                        customTabsIntent,
                        Uri.parse(url),
                        new WebviewFallback());
            } else {
                // TODO: Check whether the links can be repaired
                View v = mActivity.findViewById(R.id.article_card);
                if (v != null) {
                    Snackbar.make(v, mActivity.getString(R.string.error_malformed_link),
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    /**
     * VewHolder which holds the text blocks
     */
    public class SingleViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView headerTextView;
        public final ImageView backdropImageView;
        public final TextView contentTextView;
        public Wrapper model;
        private boolean mHasBackdrop = true;

        public SingleViewHolder(View view, InterceptLinkMovementMethod.LinkClickedCallback callback) {
            super(view);
            this.view = view;
            this.headerTextView = (TextView) view.findViewById(R.id.commLinkWrapperHeaderText);
            this.backdropImageView = (ImageView) view.findViewById(R.id.commLinkWrapperBackdrop);
            this.contentTextView = (TextView) view.findViewById(R.id.commLinkWrapperTextBody);
            this.contentTextView.setMovementMethod(
                    InterceptLinkMovementMethod.getInstance(callback));
        }


        /**
         * Binds the ViewHolder
         *
         * @param wrapper The {@link CommLinkModel} to display. Must be of type {@link ContentBlock2#TYPE_SINGLE }
         */
        public void bindWrapper(Wrapper wrapper) {
            if (wrapper == null) {
                throw new NullPointerException("Wrapper must not be null");
            }
            if (wrapper.getContentBlock2() == null) {
                mHasBackdrop = false;
                this.backdropImageView.setVisibility(View.GONE);
            } else {
                backdropImageView.setVisibility(View.VISIBLE);
            }

            this.model = wrapper;
            if (wrapper.getContentBlock4() != null &&
                    !wrapper.getContentBlock4().getHeader().equals("")) {
                this.headerTextView.setVisibility(View.VISIBLE);
                this.headerTextView.setText(Html.fromHtml("<h1>" + wrapper.getContentBlock4().getHeader() + "</h1>"));
            } else {
                this.headerTextView.setVisibility(View.GONE);
            }

            if (mHasBackdrop) {
                Glide.with(view.getContext())
                        .load(Utility.RSI_BASE_URL + model.getContentBlock2().getHeaderImages().get(0))
                        .into(backdropImageView);
            }

            if (wrapper.getContentBlock1() != null &&
                    wrapper.getContentBlock1().getContent().size() > 0) {
                contentTextView.setVisibility(View.VISIBLE);
                SpannableStringBuilder b = new SpannableStringBuilder();

                for (String s : wrapper.getContentBlock1().getContent()) {
                    if (s.contains("<img")) {
                        b.append(Html.fromHtml(s,
                                new GlideImageGetter(view.getContext(), contentTextView),
                                null));
                    } else {
                        b.append(Html.fromHtml(s));
                    }
                }

                this.contentTextView.setText(b);

            } else {
                contentTextView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ViewHolder with an {@link RecyclerView} which will be populated by {@link CommLinkReaderSlideshowAdapter}
     */
    public class SlideshowViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final RecyclerView slideshowRecyclerView;
        private final CommLinkReaderSlideshowAdapter mAdapter;
        public Wrapper wrapper;

        public SlideshowViewHolder(View view) {
            super(view);
            this.view = view;
            slideshowRecyclerView = (RecyclerView) view.findViewById(R.id.commLinkSlideshowRecyclerView);
            slideshowRecyclerView.setNestedScrollingEnabled(false);
            slideshowRecyclerView.setLayoutManager(
                    new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            mAdapter = new CommLinkReaderSlideshowAdapter(view.getContext());
            slideshowRecyclerView.setAdapter(mAdapter);
        }

        /**
         * Shortcut to binding the ViewHolder by itself.
         *
         * @param wrapper The {@link Wrapper} to display. Must be of type {@link ContentBlock2#TYPE_SLIDESHOW }
         */
        public void bindSlideShow(Wrapper wrapper) {
            if (wrapper.getContentBlock2().getHeaderImageType() != ContentBlock2.TYPE_SLIDESHOW) {
                throw new IllegalArgumentException("CommLinkContentPart must be of type CONTENT_TYPE_SLIDESHOW");
            }

            this.wrapper = wrapper;
            mAdapter.setLinks(wrapper.getContentBlock2().getHeaderImages());
        }
    }
}
