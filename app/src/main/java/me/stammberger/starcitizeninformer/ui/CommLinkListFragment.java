package me.stammberger.starcitizeninformer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pkmmte.pkrss.Article;

import java.util.ArrayList;

import me.stammberger.starcitizeninformer.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CommLinkListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_ARTICLES = "articles";

    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CommLinkListFragment() {
    }

    @SuppressWarnings("unused")
    public static CommLinkListFragment newInstance(int columnCount, ArrayList<Article> commLinks) {
        CommLinkListFragment fragment = new CommLinkListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        if (commLinks != null) {
            args.putParcelableArrayList(ARG_ARTICLES, commLinks);
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
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            if (getArguments() != null && getArguments().containsKey(ARG_ARTICLES)) {
                ArrayList<Article> list = getArguments().getParcelableArrayList(ARG_ARTICLES);
                mRecyclerView.setAdapter(new CommLinkRecyclerViewAdapter(list, mListener));
            }
        }
        return view;
    }

    public void setCommLinks(ArrayList<Article> commLinks) {
        if (commLinks == null) {
            throw new NullPointerException("Comm links are null");
        }

        mRecyclerView.setAdapter(new CommLinkRecyclerViewAdapter(commLinks, mListener));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Article item);
    }
}
