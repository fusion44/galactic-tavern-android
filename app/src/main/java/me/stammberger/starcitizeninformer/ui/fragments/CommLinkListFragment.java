package me.stammberger.starcitizeninformer.ui.fragments;

import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.core.chrome.CustomTabActivityHelper;
import me.stammberger.starcitizeninformer.core.chrome.WebviewFallback;
import me.stammberger.starcitizeninformer.models.CommLinkModel;
import me.stammberger.starcitizeninformer.ui.adapters.CommLinkRecyclerViewAdapter;
import timber.log.Timber;

/**
 * Container fragment for the comm link RecyclerView
 */
public class CommLinkListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        CommLinkRecyclerViewAdapter.OnListFragmentInteractionListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_COMM_LINKS = "comm_links";

    private int mColumnCount = 2;
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
                = new CommLinkRecyclerViewAdapter(getContext(), commLinks, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
        gridLayoutManager.setSpanSizeLookup(commLinksAdapter.getSpanSizeLookup());
        mRecyclerView.setLayoutManager(gridLayoutManager);

        SlideInBottomAnimationAdapter slideInAdapter
                = new SlideInBottomAnimationAdapter(commLinksAdapter);

        slideInAdapter.setDuration(500);
        slideInAdapter.setInterpolator(new DecelerateInterpolator());

        mRecyclerView.setAdapter(slideInAdapter);
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

    /**
     * Implementation of the {@link CommLinkRecyclerViewAdapter.OnListFragmentInteractionListener}
     * for reacting to clicks to the RecyclerView
     *
     * @param item The clicked item
     */
    @Override
    public void onListFragmentInteraction(CommLinkModel item) {
        // Simply open a custom chrome tab. Using the source from the RSS mostly looks bad.
        // Need to find another solution.
        // TODO: Write a backend app which fetches comm links from RSI.com and makes them available through an API
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                (AppCompatActivity) this.getActivity(), customTabsIntent, item.sourceUri, new WebviewFallback());
    }
}
