package me.stammberger.starcitizencompact.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizencompact.actions.Actions;
import me.stammberger.starcitizencompact.actions.Keys;
import me.stammberger.starcitizencompact.models.forums.Forum;

/**
 * Stores all forum data once they've been loaded by an action
 * This will NOT trigger the loading process if forums are requested
 * To adhere to the Flux contract loading must be triggered by the view:
 * https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
 * <p>
 * {@link RxStore#postChange(RxStoreChange)} will update the listener classes. Called in {@link ForumStore#onRxAction(RxAction)}
 * <p>
 * This is a Singleton class
 */
public class ForumStore extends RxStore implements ForumStoreInterface {
    public static final String ID = "ForumStore";
    private static ForumStore mInstance;
    List<Forum> mForums = new ArrayList<>();

    public ForumStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The {@link ForumStore} instance
     */
    public static synchronized ForumStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new ForumStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * Gets all forums available in the store.
     *
     * @return List with forum data objects. Empty list if forums haven't been loaded yet.
     */
    @Override
    public List<Forum> getForums() {
        return mForums;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_FORUMS_ALL:
                List<Forum> forums = (List<Forum>) action.getData().get(Keys.FORUM_DATA_ALL);
                if (forums != null) {
                    mForums = forums;
                }
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}