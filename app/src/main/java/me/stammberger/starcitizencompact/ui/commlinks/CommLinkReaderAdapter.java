package me.stammberger.starcitizencompact.ui.commlinks;

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

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.core.Utility;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModel;
import me.stammberger.starcitizencompact.models.commlink.ContentBlock2;
import me.stammberger.starcitizencompact.models.commlink.Wrapper;
import me.stammberger.starcitizencompact.ui.GlideImageGetter;
import timber.log.Timber;


/**
 * This Adapter is responsible for managing the Views in the CommLinkReader RecyclerView
 * It'll discern between {@link SingleViewHolder} and {@link SlideshowViewHolder} when creating and binding a view.
 */
public class CommLinkReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final CommLinkModel mCommLinkModel;

    public CommLinkReaderAdapter(CommLinkModel model) {
        super();
        mCommLinkModel = model;
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ContentBlock2.TYPE_SINGLE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_comm_link_reader_native_single, parent, false);
                return new SingleViewHolder(view);
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

        public SingleViewHolder(View view) {
            super(view);
            this.view = view;
            this.headerTextView = (TextView) view.findViewById(R.id.commLinkWrapperHeaderText);
            this.backdropImageView = (ImageView) view.findViewById(R.id.commLinkWrapperBackdrop);
            this.contentTextView = (TextView) view.findViewById(R.id.commLinkWrapperTextBody);
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
                if (wrapper.getContentBlock2().headerImageType != ContentBlock2.TYPE_SINGLE) {
                    throw new IllegalArgumentException("Content block 2 must be of type Single");
                }
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
