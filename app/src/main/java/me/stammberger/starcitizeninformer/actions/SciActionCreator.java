package me.stammberger.starcitizeninformer.actions;

import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;

import me.stammberger.starcitizeninformer.core.CommLinkFetcher;
import me.stammberger.starcitizeninformer.stores.CommLinkStore;
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

    @Override
    public void getCommLinkParts(String sourceUrl) {
        Timber.d("Getting comm link parts for %s from DB.", sourceUrl);

        final RxAction action = newRxAction(GET_COMM_LINK_PARTS, Keys.COMM_LINK_PARTS, sourceUrl);
        if (hasRxAction(action)) return;

        if (mCommLinkFetcher == null) {
            mCommLinkFetcher = new CommLinkFetcher();
        }

        addRxAction(action, mCommLinkFetcher.getCommLinkContentParts(sourceUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(parts -> {
                    action.getData().put(Keys.COMM_LINK_PARTS, parts);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("error %s \n %s", throwable.toString(), throwable.getCause());
                    postError(action, throwable);
                }));
    }
}
