package me.stammberger.starcitizencompact.ui.forums;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.models.forums.Forum;
import me.stammberger.starcitizencompact.stores.ForumStore;
import timber.log.Timber;

/**
 * Container fragment for the forums RecyclerView
 */
public class ForumListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ForumListAdapter.OnListFragmentInteractionListener {

    private SuperRecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ForumListFragment() {
    }

    public static ForumListFragment newInstance() {
        ForumListFragment fragment = new ForumListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_list, container, false);

        // Set the adapter
        if (view instanceof SuperRecyclerView) {
            mRecyclerView = (SuperRecyclerView) view;
            ForumStore forumStore = ForumStore.get(SciApplication.getInstance().getRxFlux().getDispatcher());

            List<Forum> forums = forumStore.getForums();
            if (forums.size() == 0) {
                SciApplication.getInstance().getActionCreator().getForumsAll();
            } else {
                setForums(forums);
            }
        }
        return view;
    }

    /**
     * Setup the RecyclerView's Adapter and animations
     *
     * @param forums Forums list for the Adapter
     */
    private void setupRecyclerView(List<Forum> forums) {
        if (forums == null) {
            throw new NullPointerException("Forum list is null");
        }

        ForumListAdapter adapter
                = new ForumListAdapter(getContext(), forums, this);

        LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);

        SlideInBottomAnimationAdapter slideInAdapter
                = new SlideInBottomAnimationAdapter(adapter);

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
     * Set forum data objects after construction of the fragment
     *
     * @param forums The forum data objects for the Adapter
     */
    public void setForums(List<Forum> forums) {
        setupRecyclerView(forums);
    }

    @Override
    public void onListFragmentInteraction(Forum item) {
        Timber.d(item.forumId);
    }
}
