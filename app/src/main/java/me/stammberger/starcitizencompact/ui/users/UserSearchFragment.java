package me.stammberger.starcitizencompact.ui.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

/**
 * This Fragment handles all the UI for searching for a user.
 */
public class UserSearchFragment extends Fragment implements FloatingSearchView.OnSearchListener, UserSearchHistoryAdapter.OnListFragmentInteractionListener {
    FloatingSearchView mSearchView;
    RecyclerView mSuccessfulSearchHistoryRecyclerView;
    private User mUser;
    private ArrayList<UserSearchHistoryEntry> mUserSearchEntries;

    public UserSearchFragment() {
        // Required empty public constructor
    }

    public static UserSearchFragment newInstance() {
        UserSearchFragment fragment = new UserSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SciApplication.getInstance().getActionCreator().getUserSearchHistory();

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        mSearchView = (FloatingSearchView) v.findViewById(R.id.userSearchView);
        mSearchView.setOnSearchListener(this);

        mSuccessfulSearchHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.userSearchResultsHistoryRecyclerView);
        LinearLayoutManager mgr = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        mSuccessfulSearchHistoryRecyclerView.setLayoutManager(mgr);
        return v;
    }

    /**
     * Called when the user initiated a search
     *
     * @param charSequence The user handle to search for
     */
    @Override
    public void onSearchAction(CharSequence charSequence) {
        SciApplication.getInstance().getActionCreator().getUserByUserHandle(charSequence.toString());
        mSearchView.setActivated(false);
    }

    /**
     * After a search was conducted this method will be called with the search result.
     *
     * @param successful Determines whether the user was found or not
     * @param handle     The search text (Uase handle)
     * @param user       The user object if successful. When unsuccessful the User.data object will be null
     */
    public void setUser(boolean successful, String handle, User user) {
        UserSearchHistoryEntry use = new UserSearchHistoryEntry();
        use.handle = handle;
        use.searchDate = DateTime.now().getMillis();
        use.successful = successful;

        if (successful) {
            mUser = user;
            use.avatarUrl = user.data.avatar;

            Intent i = new Intent(getContext(), UserDetailSlidingActivity.class);
            i.putExtra(UserDetailSlidingActivity.USER_HANDLE, handle);
            getActivity().startActivity(i);
        } else {
            mUser = null;
            use.avatarUrl = "";
        }

        SciApplication.getInstance().getActionCreator().pushNewUserSearchToDb(use);

    }

    /**
     * Sets the search history present on this installation. Necessary to fill both RecyclerViews
     * with content.
     *
     * @param entries The search history items
     */
    @SuppressWarnings("Convert2streamapi") // no streams for Java 1.7 :(
    public void setUserSearchHistory(ArrayList<UserSearchHistoryEntry> entries) {
        mUserSearchEntries = entries;

        ArrayList<UserSearchHistoryEntry> successfullEntries = new ArrayList<>();
        for (UserSearchHistoryEntry e : mUserSearchEntries) {
            if (e.successful) {
                successfullEntries.add(e);
            }
        }

        UserSearchHistoryAdapter a = new UserSearchHistoryAdapter(
                getContext(), mUserSearchEntries, this);
        mSearchView.setAdapter(a);

        // This RecyclerView will only hold the successful searches.
        a = new UserSearchHistoryAdapter(
                getContext(), successfullEntries, this);
        mSuccessfulSearchHistoryRecyclerView.setAdapter(a);
    }

    /**
     * Called when a history item is clicked. Can either be in the search box recycler view or the
     * RecyclerView embedded into the fragment directly
     *
     * @param entry that has been clicked on
     */
    @Override
    public void onListFragmentInteraction(UserSearchHistoryEntry entry) {
        onSearchAction(entry.handle);
    }
}
