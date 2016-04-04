package me.stammberger.galactictavern.ui.forums;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.models.forums.Forum;
import me.stammberger.galactictavern.models.forums.ForumSectioned;
import me.stammberger.galactictavern.stores.ForumStore;

/**
 * Container fragment for the forums RecyclerView
 */
public class ForumListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ForumListAdapter.OnListFragmentInteractionListener {

    private int mListColumnCount = 1;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_list, container, false);

        mListColumnCount = getActivity().getResources().getInteger(R.integer.forum_list_column_count);

        // Set the adapter
        if (view instanceof SuperRecyclerView) {
            mRecyclerView = (SuperRecyclerView) view;
            ForumStore forumStore = ForumStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());

            List<ForumSectioned> forums = forumStore.getForums();
            if (forums.size() == 0) {
                GtApplication.getInstance().getActionCreator().getForumsAll();
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
    private void setupRecyclerView(List<ForumSectioned> forums) {
        if (forums == null) {
            throw new NullPointerException("Forum list is null");
        }

        ForumListAdapter adapter
                = new ForumListAdapter(getContext(), forums, this);
        adapter.setHasStableIds(true);

        if (mListColumnCount == 1) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(llm);
        } else {
            GridLayoutManager glm = new GridLayoutManager(getContext(), mListColumnCount);
            glm.setSpanSizeLookup(adapter.getSpanSizeLookup());
            mRecyclerView.setLayoutManager(glm);
        }

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
    public void setForums(List<ForumSectioned> forums) {
        setupRecyclerView(forums);
    }

    @Override
    public void onListFragmentInteraction(Forum item) {
        Intent i = new Intent(getActivity(), ForumThreadListActivity.class);
        i.putExtra(ForumThreadListActivity.KEY_FORUM_ID, item.forumId);
        GtApplication.getInstance().trackEvent("Forums", "open", item.forumId);
        startActivity(i);
    }
}
