package me.stammberger.starcitizeninformer.ui.commlinks;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.models.commlink.CommLinkModel;
import me.stammberger.starcitizeninformer.models.commlink.Wrapper;
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
            case Wrapper.TYPE_SINGLE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_comm_link_reader_native_single, parent, false);
                return new SingleViewHolder(view);
            case Wrapper.TYPE_SLIDESHOW:
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
        public final TextView contentTextView;
        public Wrapper model;

        public SingleViewHolder(View view) {
            super(view);
            this.view = view;
            this.contentTextView = (TextView) view.findViewById(R.id.commLinkWrapperTextBody);
        }


        /**
         * Binds the ViewHolder
         *
         * @param wrapper The {@link CommLinkModel} to display. Must be of type {@link Wrapper#TYPE_SINGLE }
         */
        public void bindWrapper(Wrapper wrapper) {
            if (wrapper == null || wrapper.getContentBlock2().getHeaderImageType() != Wrapper.TYPE_SINGLE) {
                throw new IllegalArgumentException("Wrapper must be of type Wrapper#TYPE_SINGLE");
            }

            this.model = wrapper;
            this.contentTextView.setText(Html.fromHtml(wrapper.getContentBlock1().getContent().get(0)));
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
         * @param wrapper The {@link Wrapper} to display. Must be of type {@link Wrapper#TYPE_SLIDESHOW }
         */
        public void bindSlideShow(Wrapper wrapper) {
            if (wrapper.getContentBlock2().getHeaderImageType() != Wrapper.TYPE_SLIDESHOW) {
                throw new IllegalArgumentException("CommLinkContentPart must be of type CONTENT_TYPE_SLIDESHOW");
            }

            this.wrapper = wrapper;
            mAdapter.setLinks(wrapper.getContentBlock2().getHeaderImages());
        }
    }
}
