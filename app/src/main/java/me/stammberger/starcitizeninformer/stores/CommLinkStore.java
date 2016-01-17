package me.stammberger.starcitizeninformer.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;

import me.stammberger.starcitizeninformer.actions.Actions;
import me.stammberger.starcitizeninformer.actions.Keys;
import me.stammberger.starcitizeninformer.models.CommLinkModel;

/**
 * Stores all comm links once they've been loaded by an action
 * This will NOT trigger the loading process if comm links are requested
 * To adhere to the Flux contract loading must be triggered by the view:
 * https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
 * <p/>
 * {@link RxStore#postChange(RxStoreChange)} will update the listener classes. Called in {@link CommLinkStore#onRxAction(RxAction)}
 * <p/>
 * This is a Singleton class
 */
public class CommLinkStore extends RxStore implements CommLinkStoreInterface {
    public static final String ID = "CommLinkStore";
    private static CommLinkStore mInstance;
    private ArrayList<CommLinkModel> mCommLinks;

    /**
     * Private constructor. Use @CommLinkStore.get to retrieve an instance
     *
     * @param dispatcher The RxFlux dispatcher
     */
    private CommLinkStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The CommLinkStore instance
     */
    public static synchronized CommLinkStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new CommLinkStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<CommLinkModel> getCommLinks() {
        return mCommLinks == null ? new ArrayList<CommLinkModel>() : mCommLinks;
    }

    /**
     * Method is called when the loading action has finished running.
     *
     * @param action RxAction that has finished loading. Must contain the comm link data.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_COMM_LINKS:
                this.mCommLinks = (ArrayList<CommLinkModel>) action.getData().get(Keys.COMM_LINKS);
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}
