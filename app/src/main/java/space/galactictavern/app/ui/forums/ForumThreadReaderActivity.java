package space.galactictavern.app.ui.forums;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.malinskiy.superrecyclerview.OnMoreListener;

import java.util.List;

import space.galactictavern.app.R;
import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.app.models.forums.ForumThreadPost;
import space.galactictavern.app.stores.ForumStore;
import space.galactictavern.app.ui.RxFluxActivity;

/**
 * An activity representing a single thread reader screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ForumThreadListActivity}.
 */
public class ForumThreadReaderActivity extends RxFluxActivity implements OnMoreListener {

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_thread_reader_one_pane);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail mFragment and add it to the activity
            // using a mFragment transaction.
            Bundle arguments = new Bundle();
            long threadId = getIntent().getExtras().getLong(ForumThreadReaderFragment.ARG_THREAD_ID);
            arguments.putLong(ForumThreadReaderFragment.ARG_THREAD_ID, threadId);

            if (ForumThreadListActivity.viewerType.equals(ForumThreadListActivity.VIEWER_TYPE_WEB_VIEW)) {
                mFragment = new ForumThreadReaderWebViewFragment();
            } else {
                mFragment = new ForumThreadReaderFragment();
            }

            mFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.forumThreadReaderContainer, mFragment)
                    .commit();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onRxStoreChanged(RxStoreChange change) {
        super.onRxStoreChanged(change);
        RxAction rxAction = change.getRxAction();
        if (change.getStoreId().equals(ForumStore.ID)) {
            if (rxAction.getType().equals(Actions.GET_FORUM_THREAD_POSTS)) {
                if (ForumThreadListActivity.viewerType.equals(ForumThreadListActivity.VIEWER_TYPE_WEB_VIEW)) {
                    long threadId = (long) rxAction.getData().get(Keys.FORUM_THREAD_ID);
                    int page = (int) rxAction.getData().get(Keys.PAGINATION_CURRENT_PAGE);
                    List<ForumThreadPost> posts =
                            (List<ForumThreadPost>) rxAction.getData().get(Keys.FORUM_THREAD_POSTS_FOR_PAGE);

                    ((ForumThreadReaderFragment) mFragment).addPosts(threadId, page, posts);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }
}
