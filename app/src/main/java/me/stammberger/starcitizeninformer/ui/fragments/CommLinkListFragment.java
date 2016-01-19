package me.stammberger.starcitizeninformer.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.ui.adapters.CommLinkRecyclerViewAdapter;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CommLinkListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_COMM_LINKS = "comm_links";

    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private SuperRecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommLinkListFragment() {
    }

    @SuppressWarnings("unused")
    public static CommLinkListFragment newInstance(int columnCount, ArrayList<CommLinkModel> commLinks) {
        CommLinkListFragment fragment = new CommLinkListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        if (commLinks != null) {
            args.putParcelableArrayList(ARG_COMM_LINKS, commLinks);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comm_link_list, container, false);

        // Set the adapter
        if (view instanceof SuperRecyclerView) {
            mRecyclerView = (SuperRecyclerView) view;
            mRecyclerView.setRefreshListener(this);
            if (getArguments() != null && getArguments().containsKey(ARG_COMM_LINKS)) {
                ArrayList<CommLinkModel> list = getArguments().getParcelableArrayList(ARG_COMM_LINKS);
                setupRecyclerView(list);
            }
        }
        return view;
    }

    /**
     * Setup the RecyclerView's Adapter and animations
     *
     * @param commLinks Comm link list for the Adapter
     */
    private void setupRecyclerView(ArrayList<CommLinkModel> commLinks) {
        if (commLinks == null) {
            throw new NullPointerException("Comm links are null");
        }

        CommLinkRecyclerViewAdapter commLinksAdapter
                = new CommLinkRecyclerViewAdapter(getContext(), commLinks, mListener);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
        gridLayoutManager.setSpanSizeLookup(commLinksAdapter.getSpanSizeLookup());
        mRecyclerView.setLayoutManager(gridLayoutManager);

        SlideInBottomAnimationAdapter slideInAdapter
                = new SlideInBottomAnimationAdapter(commLinksAdapter);

        slideInAdapter.setDuration(500);
        slideInAdapter.setInterpolator(new DecelerateInterpolator());

        mRecyclerView.setAdapter(slideInAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when swipe to refresh gesture was accomplished
     */
    @Override
    public void onRefresh() {
        Timber.d("Refreshing comm links.");
        mRecyclerView.getSwipeToRefresh().setRefreshing(false);
    }

    /**
     * Set comm links after construction of the fragment
     *
     * @param commLinks The comm links for the Adapter
     */
    public void setCommLinks(ArrayList<CommLinkModel> commLinks) {
        setupRecyclerView(commLinks);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(CommLinkModel item);
    }
}
