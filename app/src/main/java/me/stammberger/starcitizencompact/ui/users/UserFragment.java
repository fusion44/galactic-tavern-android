package me.stammberger.starcitizencompact.ui.users;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mypopsy.widget.FloatingSearchView;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;


public class UserFragment extends Fragment implements FloatingSearchView.OnSearchListener {
    FloatingSearchView mSearchView;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        mSearchView = (FloatingSearchView) v.findViewById(R.id.userSearchView);
        mSearchView.setOnSearchListener(this);
        return v;
    }

    @Override
    public void onSearchAction(CharSequence charSequence) {
        SciApplication.getInstance().getActionCreator().getUserByUserHandle(charSequence.toString());
        mSearchView.setActivated(false);
    }
}
