package me.stammberger.starcitizencompact.ui.forums;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.stammberger.starcitizencompact.R;

/**
 * A fragment representing a single thread reader screen.
 * This fragment is either contained in a {@link ForumThreadListActivity}
 * in two-pane mode (on tablets) or a {@link ForumThreadReaderActivity}
 * on handsets.
 */
public class ForumThreadReaderFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Forum Id
     */
    private int mForumThreadId = -1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ForumThreadReaderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mForumId = .ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mForumId.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.forum_thread_reader_fragment, container, false);

        // Show the dummy content as text in a TextView.
        if (mForumThreadId != -1) {
            ((TextView) rootView.findViewById(R.id.forumThreadReaderTitleTextView)).setText("Text here");
        }

        return rootView;
    }
}
