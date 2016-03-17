package me.stammberger.starcitizencompact.ui.ships;

import android.content.Intent;
import android.os.Bundle;
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

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.models.ship.Ship;
import me.stammberger.starcitizencompact.models.ship.ShipData;
import me.stammberger.starcitizencompact.stores.ShipStore;

/**
 * Container fragment for the ship RecyclerView
 */
public class ShipListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ShipListRecyclerViewAdapter.OnListFragmentInteractionListener,
        ShipFilterDialog.ShipFilterDialogClickListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int VIEWER_ACTIVITY_RESULT = 0;

    private int mColumnCount = 2;
    private SuperRecyclerView mRecyclerView;
    private ShipStore mShipStore;
    private ShipListRecyclerViewAdapter mShipListRecyclerViewAdapter;
    private ArrayList<String> mCurrentFilterList = new ArrayList<>();
    private boolean mIsLargeLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShipListFragment() {
        setHasOptionsMenu(true);
    }

    @SuppressWarnings("unused")
    public static ShipListFragment newInstance(int columnCount) {
        ShipListFragment fragment = new ShipListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ship_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsLargeLayout = getActivity().getResources().getBoolean(R.bool.is_large_layout);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ship_list, container, false);

        // Set the adapter
        if (view instanceof SuperRecyclerView) {
            mRecyclerView = (SuperRecyclerView) view;
            mShipStore = ShipStore.get(SciApplication.getInstance().getRxFlux().getDispatcher());


            // Check if the store has the articles already loaded
            ShipData shipData = mShipStore.getAllShips();
            if (shipData.ships.size() == 0) {
                // See comm link fragment for explanation
                SciApplication.getInstance().getActionCreator().getAllShips();
            } else {
                setShipData(shipData);
            }
        }
        return view;
    }

    /**
     * Setup the RecyclerView's Adapter and animations
     *
     * @param shipData {@link ShipData} object
     */
    private void setupRecyclerView(ShipData shipData) {
        if (shipData == null) {
            throw new NullPointerException("Ship data is null");
        }

        mShipListRecyclerViewAdapter
                = new ShipListRecyclerViewAdapter(getContext(), shipData.ships, this, mIsLargeLayout);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), mColumnCount);
        gridLayoutManager.setSpanSizeLookup(mShipListRecyclerViewAdapter.getSpanSizeLookup());
        mRecyclerView.setLayoutManager(gridLayoutManager);

        SlideInBottomAnimationAdapter slideInAdapter
                = new SlideInBottomAnimationAdapter(mShipListRecyclerViewAdapter);

        slideInAdapter.setDuration(500);
        slideInAdapter.setInterpolator(new DecelerateInterpolator());

        mRecyclerView.setAdapter(slideInAdapter);
    }

    /**
     * Called when swipe to refresh gesture was accomplished
     */
    @Override
    public void onRefresh() {
        mRecyclerView.getSwipeToRefresh().setRefreshing(false);
    }

    /**
     * Set ships models after construction of the fragment
     *
     * @param shipData The ship data for the Adapter
     */
    public void setShipData(ShipData shipData) {
        if (mColumnCount == 1) {
            setupRecyclerView(shipData);
        } else {
            setupRecyclerView(calculateSpanCount(shipData));
        }
    }

    /**
     * Implementation of the {@link ShipListRecyclerViewAdapter.OnListFragmentInteractionListener}
     * for reacting to clicks to the RecyclerView
     *
     * @param item The clicked item
     */
    @Override
    public void onListFragmentInteraction(Ship item, ImageView view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this.getActivity(), view, item.shipimgsmall);
        Intent i = new Intent(this.getContext(), ShipDetailViewerActivity.class);
        i.putExtra(ShipDetailViewerActivity.SHIP_ITEM, item.titlecontainer.title);
        getActivity().startActivityForResult(i, VIEWER_ACTIVITY_RESULT, options.toBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ship_list_filter) {
            ShipFilterDialog shipFilterDialog = ShipFilterDialog.newInstance(this, mCurrentFilterList);
            shipFilterDialog.show(getActivity().getFragmentManager(), "TAG");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This will calculate the span counts
     *
     * @param shipData {@link ShipData} object containing the ships
     * @return The {@link ShipData} object with calculated span counts
     */
    private ShipData calculateSpanCount(ShipData shipData) {
        int currentColumn = 0;

        for (int i = 0; i < shipData.ships.size(); i++) {
            Ship current = shipData.ships.get(i);
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

            if (i + 1 == shipData.ships.size() && currentColumn == 0) {
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

        return shipData;
    }

    @Override
    public void doPositiveClick(List<String> filterList) {
        mCurrentFilterList = new ArrayList<>(filterList);
        if (filterList.size() == 0) {
            mShipListRecyclerViewAdapter.setModels(mShipStore.getAllShips().ships);
            return;
        }

        ArrayList<Ship> filteredList = new ArrayList<>();
        for (Ship ship : mShipStore.getAllShips().ships) {
            for (String filter : mCurrentFilterList) {
                if (ship.titlecontainer.manufacturer.equals(filter)) {
                    filteredList.add(ship);
                    break;
                }
            }
        }

        mShipListRecyclerViewAdapter = new ShipListRecyclerViewAdapter(
                getContext(), filteredList, this, mIsLargeLayout);
        mRecyclerView.setAdapter(mShipListRecyclerViewAdapter);
    }

    @Override
    public void doNegativeClick() {
        // change nothing
    }
}
