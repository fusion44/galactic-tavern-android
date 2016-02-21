package me.stammberger.starcitizencompact.actions;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;

import java.util.ArrayList;

import me.stammberger.starcitizencompact.core.CommLinkFetcher;
import me.stammberger.starcitizencompact.core.retrofit.ShipApiService;
import me.stammberger.starcitizencompact.core.retrofit.UserApiService;
import me.stammberger.starcitizencompact.models.ship.Ship;
import me.stammberger.starcitizencompact.stores.CommLinkStore;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Responsible for managing all actions used in the application
 */
public class SciActionCreator extends RxActionCreator implements Actions {
    private CommLinkFetcher mCommLinkFetcher;

    /**
     * @param dispatcher {@link Dispatcher}
     * @param manager    {@link SubscriptionManager}
     */
    public SciActionCreator(Dispatcher dispatcher, SubscriptionManager manager) {
        super(dispatcher, manager);
    }

    /**
     * Gets a single comm link
     *
     * @param id the comm link id. Note this is not the SQLite id
     */
    @Override
    public void getCommLink(Long id) {
        final RxAction action = newRxAction(GET_COMM_LINK, id);
        if (hasRxAction(action)) return;

        if (mCommLinkFetcher == null) {
            mCommLinkFetcher = new CommLinkFetcher();
        }

        addRxAction(action, mCommLinkFetcher.getCommLink(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comm_link -> {
                    action.getData().put(Keys.COMM_LINK, comm_link);
                    postRxAction(action);
                }, throwable -> {
                    postError(action, throwable);
                }));
    }

    /**
     * Initiates the get comm link retrieval through {@link SciActionCreator#mCommLinkFetcher}
     * Creates the CommLinkFetcher field and subscribes to its observable
     * Once the comm links are retrieved it posts a new {@link RxAction} to update {@link CommLinkStore}
     */
    @Override
    public void getCommLinks() {
        Timber.d("Starting fetch comm link action");

        final RxAction action = newRxAction(GET_COMM_LINKS);
        if (hasRxAction(action)) return;

        if (mCommLinkFetcher == null) {
            mCommLinkFetcher = new CommLinkFetcher();
        }

        addRxAction(action, mCommLinkFetcher.getCommLinks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comm_links -> {
                    action.getData().put(Keys.COMM_LINKS, comm_links);
                    postRxAction(action);
                }, throwable -> {
                    postError(action, throwable);
                }));
    }

    /**
     * Initiates the process to get the content wrappers for a specific comm link from the using
     * {@link CommLinkFetcher#getCommLinkContentWrappers(Long)}.
     *
     * @param id The comm link id
     */
    @Override
    public void getCommLinkContentWrappers(Long id) {
        final RxAction action = newRxAction(GET_COMM_LINK_CONTENT_WRAPPERS, Keys.COMM_LINK_ID, id);
        if (hasRxAction(action)) return;

        if (mCommLinkFetcher == null) {
            mCommLinkFetcher = new CommLinkFetcher();
        }

        addRxAction(action, mCommLinkFetcher.getCommLinkContentWrappers(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(parts -> {
                    action.getData().put(Keys.COMM_LINK_CONTENT_WRAPPERS, new ArrayList<>(parts));
                    SciActionCreator.this.postRxAction(action);
                }, throwable -> {
                    Timber.d("error %s \n %s", throwable.toString(), throwable.getCause());
                    postError(action, throwable);
                }));
    }

    /**
     * Initiates the process to fetch all ships from the API
     */
    @Override
    public void getAllShips() {
        RxAction action = newRxAction(GET_SHIP_DATA_ALL);
        if (hasRxAction(action)) return;

        addRxAction(action, ShipApiService.Factory.getInstance().getShips()
                .subscribeOn(Schedulers.io())
                .map(shipData -> {
                    for (Ship ship : shipData.ships) {
                        shipData.shipMap.put(ship.titlecontainer.title, ship);
                    }
                    return shipData;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shipData -> {
                    action.getData().put(Keys.SHIP_DATA_ALL, shipData);
                    postRxAction(action);
                }, throwable -> {
                    postError(action, throwable);
                }));
    }

    /**
     * Searches for a user by its handle
     *
     * @param userHandle String with the handle
     */
    @Override
    public void getUserByUserHandle(String userHandle) {
        RxAction action = newRxAction(GET_USER_BY_USER_HANDLE, Keys.USER_HANDLE, userHandle);
        if (hasRxAction(action)) return;

        addRxAction(action, UserApiService.Factory.getInstance().getUser(userHandle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    action.getData().put(Keys.USER_HANDLE, userHandle);
                    action.getData().put(Keys.USER_DATA, user);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error searching for user: %s", throwable.getCause());
                    postError(action, throwable);
                }));
    }
}
