package me.stammberger.starcitizencompact.ui.users;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mypopsy.widget.FloatingSearchView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.models.user.User;
import me.stammberger.starcitizencompact.models.user.UserSearchHistoryEntry;


public class UserFragment extends Fragment implements FloatingSearchView.OnSearchListener, UserSearchHistoryAdapter.OnListFragmentInteractionListener {
    FloatingSearchView mSearchView;
    private User mUser;
    private ArrayList<UserSearchHistoryEntry> mUserSearchEntries;

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
        SciApplication.getInstance().getActionCreator().getUserSearchHistory();
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

    public void setUser(boolean successful, String handle, User user) {
        UserSearchHistoryEntry use = new UserSearchHistoryEntry();
        use.handle = handle;
        use.searchDate = DateTime.now().getMillis();
        use.successful = successful;

        if (successful) {
            mUser = user;
            use.avatarUrl = user.data.avatar;
        } else {
            mUser = null;
            use.avatarUrl = "";
        }

        SciApplication.getInstance().getActionCreator().pushNewUserSearchToDb(use);
    }

    public void setUserSearchHistory(ArrayList<UserSearchHistoryEntry> entries) {
        mUserSearchEntries = entries;
        UserSearchHistoryAdapter a = new UserSearchHistoryAdapter(
                getContext(), mUserSearchEntries, this);
        mSearchView.setAdapter(a);
    }

    @Override
    public void onListFragmentInteraction(UserSearchHistoryEntry entry) {
        onSearchAction(entry.handle);
    }
}
