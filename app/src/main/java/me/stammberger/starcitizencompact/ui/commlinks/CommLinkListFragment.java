package me.stammberger.starcitizencompact.ui.commlinks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModel;
import me.stammberger.starcitizencompact.stores.CommLinkStore;
import timber.log.Timber;

/**
 * Container fragment for the comm link RecyclerView
 */
public class CommLinkListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        CommLinkListRecyclerViewAdapter.OnListFragmentInteractionListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int READER_ACTIVITY_RESULT = 0;

    private int mColumnCount = 2;
    private SuperRecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommLinkListFragment() {
    }

    @SuppressWarnings("unused")
    public static CommLinkListFragment newInstance(int columnCount) {
        CommLinkListFragment fragment = new CommLinkListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
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
            CommLinkStore commLinkStore = CommLinkStore.get(SciApplication.getInstance().getRxFlux().getDispatcher());

            // Check if the store has the articles already loaded
            ArrayList<CommLinkModel> commLinks = commLinkStore.getCommLinks();
            if (commLinks.size() == 0) {
                // if not, instruct the action creator to start the fetch  comm links process
                // The ActionCreator will create an action which will get the rss articles
                // and put them in the CommLinkStore which will post a change which will trigger
                // MainActivity.onRxStoreChanged with the data
                // The articles will then be RxStoreChange argument of onRxStoreChange
                // https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
                SciApplication.getInstance().getActionCreator().getCommLinks();
            } else {
                setCommLinks(commLinks);
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

        CommLinkListRecyclerViewAdapter commLinksAdapter
                = new CommLinkListRecyclerViewAdapter(getContext(), commLinks, this);

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
        setupRecyclerView(calculateSpanCount(commLinks));
    }

    /**
     * Implementation of the {@link CommLinkListRecyclerViewAdapter.OnListFragmentInteractionListener}
     * for reacting to clicks to the RecyclerView
     *
     * @param item The clicked item
     */
    @Override
    public void onListFragmentInteraction(CommLinkModel item, ImageView view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this.getActivity(), view, item.getMainBackdrop());
        Intent i = new Intent(this.getContext(), CommLinkReaderActivity.class);
        i.putExtra(CommLinkReaderActivity.COMM_LINK_ITEM, item.commLinkId);
        getActivity().startActivityForResult(i, READER_ACTIVITY_RESULT, options.toBundle());
    }


    /**
     * Calculates the span count comm links. Takes several keywords into account like
     * "around the verse" or "released" to guess which item might be more interesting
     * to the user. This will basically sort the list.
     *
     * @param aList List of comm links
     * @return The sorted list
     */
    private ArrayList<CommLinkModel> calculateSpanCount(ArrayList<CommLinkModel> aList) {
        int currentColumn = 0;

        for (int i = 0; i < aList.size(); i++) {
            CommLinkModel current = aList.get(i);
            int spanSize = 1;

            if (i == 0 || mColumnCount == 2 && currentColumn == 0) {
                // if we are in two column mode, check whether this item is displayed in first column
                // if yes, check whether it will be displayed with two columns
                int rand = (int) (Math.random() * 3);
                if (rand == 2) {
                    spanSize = 2;

                    // count one up as one additional column is used up.
                    // This basically leads to resetting at the end of this for loop on case of two columns
                    currentColumn++;
                }
            } else if (mColumnCount == 3) {
                if (currentColumn == 0 || currentColumn == 1) {
                    int rand = (int) (Math.random() * 3);
                    if (rand == 2) {
                        spanSize = 2;
                        currentColumn++; // count one up as one additional column is used up
                    }
                }
            }

            if (i + 1 == aList.size() && currentColumn == 0) {
                // if the last item is in the left column force it to span all columns
                spanSize = mColumnCount;
            }

            currentColumn++;
            // reset column counter.
            if (currentColumn >= mColumnCount) {
                currentColumn = 0;
            }

            current.spanCount = spanSize;
        }

        return aList;
    }
}