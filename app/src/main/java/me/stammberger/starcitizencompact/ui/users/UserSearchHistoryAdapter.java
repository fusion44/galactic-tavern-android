package me.stammberger.starcitizencompact.ui.users;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.core.Utility;
import me.stammberger.starcitizencompact.models.user.UserSearchHistoryEntry;

public class UserSearchHistoryAdapter extends RecyclerView.Adapter<UserSearchHistoryAdapter.HistoryEntryViewHolder> {
    private final Context mContext;
    private final OnListFragmentInteractionListener mListener;
    ArrayList<UserSearchHistoryEntry> mEntries = new ArrayList<>();

    public UserSearchHistoryAdapter(Context c,
                                    ArrayList<UserSearchHistoryEntry> entries,
                                    OnListFragmentInteractionListener listener) {
        super();
        mContext = c;
        mEntries = entries;
        mListener = listener;
    }

    @Override
    public HistoryEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user_search_history_entry, parent, false);
        return new HistoryEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryEntryViewHolder holder, int position) {
        holder.bindView(mEntries.get(position));
        holder.itemView.setOnClickListener(
                v -> mListener.onListFragmentInteraction(mEntries.get(position)));
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
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
         * @param entry that has been clicked on
         */
        void onListFragmentInteraction(UserSearchHistoryEntry entry);
    }

    public class HistoryEntryViewHolder extends RecyclerView.ViewHolder {
        public UserSearchHistoryEntry entry;
        public ImageView avatarImageView;
        public TextView handleTextView;
        public TextView dateTextView;

        public HistoryEntryViewHolder(View itemView) {
            super(itemView);
            avatarImageView = (ImageView) itemView.findViewById(R.id.userSearchHistoryAvatarImage);
            handleTextView = (TextView) itemView.findViewById(R.id.userSearchHistoryHandle);
            dateTextView = (TextView) itemView.findViewById(R.id.userSearchHistoryEntryDate);
        }

        public void bindView(UserSearchHistoryEntry e) {
            entry = e;
            if (e.successful) {
                Glide.with(mContext)
                        .load(e.avatarUrl)
                        .into(avatarImageView);
            } else {
                avatarImageView.setImageDrawable(
                        ContextCompat.getDrawable(mContext, R.drawable.ic_remove_circle_outline_24dp));
            }
            handleTextView.setText(e.handle);
            dateTextView.setText(Utility.getFormattedRelativeTimeSpan(mContext, e.searchDate));
        }
    }
}
