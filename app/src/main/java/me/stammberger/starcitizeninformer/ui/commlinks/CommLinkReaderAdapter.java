package me.stammberger.starcitizeninformer.ui.commlinks;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.models.CommLinkModelContentPart;
import timber.log.Timber;


/**
 * This Adapter is responsible for managing the Views in the CommLinkReader RecyclerView
 * It'll discern between {@link TextBlockViewHolder} and {@link SlideshowViewHolder} when creating and binding a view.
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
            case CommLinkModelContentPart.CONTENT_TYPE_TEXT_BLOCK:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_comm_link_reader_native_text_content, parent, false);
                return new TextBlockViewHolder(view);
            case CommLinkModelContentPart.CONTENT_TYPE_SLIDESHOW:
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
        if (holder instanceof TextBlockViewHolder) {
            TextBlockViewHolder vh = (TextBlockViewHolder) holder;
            vh.bindTextBlock(mCommLinkModel.content.get(position));
        } else if (holder instanceof SlideshowViewHolder) {
            SlideshowViewHolder vh = (SlideshowViewHolder) holder;
            vh.bindSlideShow(mCommLinkModel.content.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mCommLinkModel.content.get(position).type;
    }

    @Override
    public int getItemCount() {
        return mCommLinkModel.content.size();
    }

    /**
     * VewHolder which holds the text blocks
     */
    public class TextBlockViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView contentTextView;
        public CommLinkModelContentPart contentPart;

        public TextBlockViewHolder(View view) {
            super(view);
            this.view = view;
            this.contentTextView = (TextView) view.findViewById(R.id.textBlockTextView);
        }


        /**
         * Binds the ViewHolder
         *
         * @param contentPart The {@link CommLinkModelContentPart} to display. Must be of type {@link CommLinkModelContentPart#CONTENT_TYPE_TEXT_BLOCK }
         */
        public void bindTextBlock(CommLinkModelContentPart contentPart) {
            if (contentPart == null
                    || contentPart.type != CommLinkModelContentPart.CONTENT_TYPE_TEXT_BLOCK) {
                throw new IllegalArgumentException("CommLinkContentPart must be of type CONTENT_TYPE_TEXT_BLOCK");
            }

            this.contentPart = contentPart;
            this.contentTextView.setText(Html.fromHtml(contentPart.content));
        }
    }

    /**
     * ViewHolder with an {@link RecyclerView} which will be populated by {@link CommLinkReaderSlideshowAdapter}
     */
    public class SlideshowViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final RecyclerView slideshowRecyclerView;
        private final CommLinkReaderSlideshowAdapter mAdapter;
        public CommLinkModelContentPart contentPart;

        public SlideshowViewHolder(View view) {
            super(view);
            this.view = view;
            slideshowRecyclerView = (RecyclerView) view.findViewById(R.id.slideshowRecyclerView);
            slideshowRecyclerView.setLayoutManager(
                    new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            mAdapter = new CommLinkReaderSlideshowAdapter(view.getContext());
            slideshowRecyclerView.setAdapter(mAdapter);
        }

        /**
         * Shortcut to binding the ViewHolder by itself.
         *
         * @param part The {@link CommLinkModelContentPart} to display. Must be of type {@link CommLinkModelContentPart#CONTENT_TYPE_SLIDESHOW }
         */
        public void bindSlideShow(CommLinkModelContentPart part) {
            if (part.type != CommLinkModelContentPart.CONTENT_TYPE_SLIDESHOW) {
                throw new IllegalArgumentException("CommLinkContentPart must be of type CONTENT_TYPE_SLIDESHOW");
            }

            contentPart = part;
            mAdapter.setLinks(part.getSlideshowLinks());
        }
    }
}
