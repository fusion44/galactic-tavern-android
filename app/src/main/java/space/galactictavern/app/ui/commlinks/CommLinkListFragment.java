package space.galactictavern.app.ui.commlinks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.stores.CommLinkStore;
import timber.log.Timber;

/**
 * Container fragment for the comm link RecyclerView
 */
public class CommLinkListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        CommLinkListRecyclerViewAdapter.OnListFragmentInteractionListener, OnMoreListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int READER_ACTIVITY_RESULT = 0;

    private int mColumnCount = 2;
    private SuperRecyclerView mRecyclerView;
    private boolean mShowingFilteredView = false;
    private CommLinkListRecyclerViewAdapter mCommLinksAdapter;
    private long mLastCommLinkPublished = 0;
    private int mMaxResults = 15;

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

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comm_link_list, container, false);

        // Set the adapter
        if (view instanceof SuperRecyclerView) {
            mRecyclerView = (SuperRecyclerView) view;
            mRecyclerView.setOnMoreListener(this);
            CommLinkStore commLinkStore = CommLinkStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());

            // Check if the store has the articles already loaded
            ArrayList<CommLinkModel> commLinks = commLinkStore.getCommLinks();
            if (commLinks.size() == 0) {
                // if not, instruct the action creator to start the fetch  comm links process
                // The ActionCreator will create an action which will get the rss articles
                // and put them in the CommLinkStore which will post a change which will trigger
                // MainActivity.onRxStoreChanged with the data
                // The articles will then be RxStoreChange argument of onRxStoreChange
                // https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
                GtApplication.getInstance().getActionCreator().getCommLinks(mLastCommLinkPublished, mMaxResults);
            } else {
                addCommLinks(commLinks);
            }
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.comm_link_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Setup the RecyclerView's Adapter and animations
     *
     * @param commLinks Comm link list for the Adapter
     */
    private void setupRecyclerView(List<CommLinkModel> commLinks) {
        if (commLinks == null) {
            throw new NullPointerException("Comm links are null");
        }

        if (mLastCommLinkPublished == 0) {
            mCommLinksAdapter = new CommLinkListRecyclerViewAdapter(getContext(), commLinks, this);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
            gridLayoutManager.setSpanSizeLookup(mCommLinksAdapter.getSpanSizeLookup());
            mRecyclerView.setLayoutManager(gridLayoutManager);

            SlideInBottomAnimationAdapter slideInAdapter
                    = new SlideInBottomAnimationAdapter(mCommLinksAdapter);

            slideInAdapter.setDuration(500);
            slideInAdapter.setInterpolator(new DecelerateInterpolator());

            mRecyclerView.setAdapter(slideInAdapter);
        } else {
            mCommLinksAdapter.addItems(commLinks);
            mRecyclerView.setLoadingMore(false);
        }
    }

    /**
     * Called when swipe to refresh gesture was accomplished
     */
    @Override
    public void onRefresh() {
        Timber.d("Refreshing comm links.");
        mRecyclerView.getSwipeToRefresh().setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!Utility.isNetworkAvailable(getActivity())) {
            Snackbar.make(getView(), R.string.error_no_network_generic, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Add comm links after construction of the fragment
     *
     * @param commLinks New comm links for the Adapter
     */
    public void addCommLinks(ArrayList<CommLinkModel> commLinks) {
        if (mShowingFilteredView) {
            CommLinkStore cls = CommLinkStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());
            setupRecyclerView(calculateSpanCount(cls.getFavorites()));
        } else {
            setupRecyclerView(calculateSpanCount(commLinks));
        }
        mLastCommLinkPublished = commLinks.get(commLinks.size() - 1).published;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_comm_link_filter) {
            if (toggleFilter()) {
                item.setIcon(R.drawable.ic_star_gold_24dp);
            } else {
                item.setIcon(R.drawable.ic_star_black_24dp);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Toggles the filter view
     *
     * @return True if filter is turned on, False if not
     */
    private boolean toggleFilter() {
        CommLinkStore store = CommLinkStore.get(
                GtApplication.getInstance().getRxFlux().getDispatcher());
        if (!mShowingFilteredView) {
            if (mCommLinksAdapter != null) {
                mCommLinksAdapter.replaceItems(calculateSpanCount(store.getFavorites()));
            }

            // Don't request more when showing filtered view -> We should have all comm links already
            mRecyclerView.setOnMoreListener(null);
            mShowingFilteredView = true;
            return true;
        } else {
            if (mCommLinksAdapter != null) {
                mCommLinksAdapter.replaceItems(calculateSpanCount(store.getCommLinks()));
            }

            mRecyclerView.setOnMoreListener(this);
            mShowingFilteredView = false;
            return false;
        }
    }


    /**
     * Calculates the span count comm links. Takes several keywords into account like
     * "around the verse" or "released" to guess which item might be more interesting
     * to the user. This will basically sort the list.
     *
     * @param aList List of comm links
     * @return The sorted list
     */
    private List<CommLinkModel> calculateSpanCount(List<CommLinkModel> aList) {
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

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        GtApplication.getInstance().getActionCreator().getCommLinks(mLastCommLinkPublished, mMaxResults);
    }
}
