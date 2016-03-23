package me.stammberger.starcitizencompact.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import me.stammberger.starcitizencompact.actions.Actions;
import me.stammberger.starcitizencompact.actions.Keys;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModel;
import me.stammberger.starcitizencompact.models.commlink.Wrapper;

/**
 * Stores all comm links once they've been loaded by an action
 * This will NOT trigger the loading process if comm links are requested
 * To adhere to the Flux contract loading must be triggered by the view:
 * https://raw.githubusercontent.com/lgvalle/lgvalle.github.io/master/public/images/flux-graph-complete.png
 * <p>
 * {@link RxStore#postChange(RxStoreChange)} will update the listener classes. Called in {@link CommLinkStore#onRxAction(RxAction)}
 * <p>
 * This is a Singleton class
 */
public class CommLinkStore extends RxStore implements CommLinkStoreInterface {
    public static final String ID = "CommLinkStore";
    private static CommLinkStore mInstance;
    private LinkedHashMap<Long, CommLinkModel> mCommLinks;
    private HashMap<Long, List<Wrapper>> mCommLinkContentWrappers;
    private ArrayList<CommLinkModel> mFavorites = new ArrayList<>();

    /**
     * Private constructor. Use @CommLinkStore.get to retrieve an instance
     *
     * @param dispatcher The RxFlux dispatcher
     */
    private CommLinkStore(Dispatcher dispatcher) {
        super(dispatcher);
        mCommLinks = new LinkedHashMap<>();
        mCommLinkContentWrappers = new HashMap<>();
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
    public CommLinkModel getCommLink(Long id) {
        return mCommLinks.get(id) == null ? new CommLinkModel() : mCommLinks.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<CommLinkModel> getCommLinks() {
        return mCommLinks == null ? new ArrayList<>() : new ArrayList<>(mCommLinks.values());
    }

    /**
     * {@inheritDoc}
     *
     * @param commLinkId
     */
    @Override
    public List<Wrapper> getCommLinkContentWrappers(Long commLinkId) {
        return mCommLinkContentWrappers.get(commLinkId) == null ? new ArrayList<>()
                : mCommLinkContentWrappers.get(commLinkId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommLinkModel> getFavorites() {
        return mFavorites;
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
                ArrayList<CommLinkModel> m = (ArrayList<CommLinkModel>) action.getData().get(Keys.COMM_LINKS);
                for (int i = 0; i < m.size(); i++) {
                    CommLinkModel cl = m.get(i);
                    mCommLinks.put(cl.commLinkId, cl);
                    if (cl.favorite) {
                        mFavorites.add(cl);
                    }
                }
                break;
            case Actions.GET_COMM_LINK_CONTENT_WRAPPERS:
                ArrayList<Wrapper> wrappers
                        = (ArrayList<Wrapper>) action.getData().get(Keys.COMM_LINK_CONTENT_WRAPPERS);
                Long commLinkId = action.get(Keys.COMM_LINK_ID);

                if (wrappers.size() != 0) {
                    this.mCommLinkContentWrappers.put(commLinkId, wrappers);
                }

                break;
            case Actions.COMM_LINK_DATA_UPDATED:
                ArrayList<CommLinkModel> models =
                        (ArrayList<CommLinkModel>) action.getData().get(Keys.COMM_LINKS);
                for (CommLinkModel model : models) {
                    if (model.favorite & !mFavorites.contains(model)) {
                        mFavorites.add(model);
                    } else if (!model.favorite && mFavorites.contains(model)) {
                        mFavorites.remove(model);
                    }
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