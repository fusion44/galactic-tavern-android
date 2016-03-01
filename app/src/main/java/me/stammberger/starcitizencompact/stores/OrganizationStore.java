package me.stammberger.starcitizencompact.stores;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.store.RxStore;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import java.util.TreeMap;

import me.stammberger.starcitizencompact.actions.Actions;
import me.stammberger.starcitizencompact.actions.Keys;
import me.stammberger.starcitizencompact.actions.SciActionCreator;
import me.stammberger.starcitizencompact.models.orgs.Organization;

public class OrganizationStore extends RxStore implements OrganizationStoreInterface {
    public static final String ID = "OrganizationStore";
    private static OrganizationStore mInstance;
    private TreeMap<String, Organization> mOrganizationData = new TreeMap<>();

    public OrganizationStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * Creates the singleton instance
     *
     * @param dispatcher RxFlux dispatcher
     * @return The {@link OrganizationStore} instance
     */
    public static synchronized OrganizationStore get(Dispatcher dispatcher) {
        if (mInstance == null) {
            mInstance = new OrganizationStore(dispatcher);
        }

        return mInstance;
    }

    /**
     * Searches for an organization using its handle on RSI.com
     * If this method returns null, the organization is not yet in the store and must be searched for
     * using the {@link SciActionCreator}
     *
     * @param id of the organization
     * @return the {@link Organization} data object
     */
    @Override
    public Organization getOrganization(String id) {
        if (!mOrganizationData.containsKey(id)) {
            return null;
        } else {
            return mOrganizationData.get(id);
        }
    }

    @Override
    public void onRxAction(RxAction action) {
        switch (action.getType()) {
            case Actions.GET_ORGANIZATION_BY_ID:
                String handle = (String) action.getData().get(Keys.ORGANIZATION_ID);
                Organization data = (Organization) action.getData().get(Keys.ORGANIZATION_DATA);
                mOrganizationData.put(handle, data);
                break;
            default:
                // return without posting a change to the store.
                // The data we want wasn't contained in the action
                return;
        }

        postChange(new RxStoreChange(ID, action));
    }
}
