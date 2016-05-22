package space.galactictavern.app.actions;

import com.badlogic.gdx.math.Vector2;
import com.hardsoftstudio.rxflux.action.RxAction;
import com.hardsoftstudio.rxflux.action.RxActionCreator;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.util.SubscriptionManager;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.Secrets;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.core.retrofit.CommLinkApiService;
import space.galactictavern.app.core.retrofit.CommLinkWrapperApiService;
import space.galactictavern.app.core.retrofit.ForumsApiService;
import space.galactictavern.app.core.retrofit.OrganizationApiService;
import space.galactictavern.app.core.retrofit.ShipApiService;
import space.galactictavern.app.core.retrofit.StarMapService;
import space.galactictavern.app.core.retrofit.UserApiService;
import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.models.commlink.ContentBlock1;
import space.galactictavern.app.models.commlink.ContentBlock2;
import space.galactictavern.app.models.commlink.Wrapper;
import space.galactictavern.app.models.favorites.Favorite;
import space.galactictavern.app.models.forums.Forum;
import space.galactictavern.app.models.forums.ForumSectioned;
import space.galactictavern.app.models.forums.ForumThreadPost;
import space.galactictavern.app.models.ship.Ship;
import space.galactictavern.app.models.user.UserSearchHistoryEntry;
import space.galactictavern.app.stores.CommLinkStore;
import space.galactictavern.app.stores.ShipStore;
import space.galactictavern.app.stores.db.tables.FavoritesTable;
import space.galactictavern.app.stores.db.tables.user.UserSearchHistoryEntryTable;
import space.galactictavern.mapcore.map.data.SystemsResultset;
import timber.log.Timber;


/**
 * Responsible for managing all actions used in the application
 */
public class GtActionCreator extends RxActionCreator implements Actions {
    /**
     * @param dispatcher {@link Dispatcher}
     * @param manager    {@link SubscriptionManager}
     */
    public GtActionCreator(Dispatcher dispatcher, SubscriptionManager manager) {
        super(dispatcher, manager);
    }

    /**
     * Gets a single comm link
     *
     * @param id the comm link id. Note this is not the SQLite id
     */
    @Override
    public void getCommLink(Long id) {
        final RxAction action = newRxAction(GET_COMM_LINK, Keys.COMM_LINK_ID, id);
        if (hasRxAction(action)) return;

        Observable<CommLinkModel> commLinkObservable =
                CommLinkApiService.Factory.getInstance().getCommLink(Secrets.GT_API_SECRET, id);
        Observable<List<Wrapper>> wrappersObservable =
                CommLinkWrapperApiService.Factory.getInstance().getCommLinkWrappers(id);
        Observable<Favorite> favoriteObservable =
                getFavoritesInternal(Favorite.TYPE_COMM_LINK, String.valueOf(id));

        Observable.zip(commLinkObservable, wrappersObservable, favoriteObservable,
                (Func3<CommLinkModel, List<Wrapper>, Favorite, Object>) (commLinkModel, wrappers1, favorite) -> {
                    for (Wrapper wrapper : wrappers1) {
                        if (wrapper.getContentBlock2() != null &&
                                wrapper.getContentBlock2().headerImageType == ContentBlock2.TYPE_SLIDESHOW) {
                            int size = wrapper.getContentBlock2().getHeaderImages().size();
                            wrapper.getContentBlock2().getHeaderImages().remove(size - 1);
                            wrapper.getContentBlock2().getHeaderImages().remove(0);
                        }
                    }

                    if (favorite != null) {
                        commLinkModel.favorite = true;
                    }
                    commLinkModel.wrappers = wrappers1;
                    action.getData().put(Keys.COMM_LINK, commLinkModel);
                    postRxAction(action);
                    return commLinkModel;
                });
    }

    /**
     * Initiates the get comm link fetching through the API
     * Once the comm links are retrieved it posts a new {@link RxAction} to update {@link CommLinkStore}
     *
     * @param lastCommLinkPublished Published date of the last received comm link
     * @param maxResults            Maximum number of results returned by the API
     */
    @SuppressWarnings("Convert2streamapi")
    @Override
    public void getCommLinks(Long lastCommLinkPublished, int maxResults) {
        Timber.d("Starting fetch comm link action");

        final RxAction action = newRxAction(GET_COMM_LINKS);
        if (hasRxAction(action)) return;

        getFavoritesInternal(Favorite.TYPE_COMM_LINK)
                .subscribeOn(Schedulers.io())
                .subscribe(favorites -> {
                    addRxAction(action, CommLinkApiService.Factory.getInstance().getCommLinks(
                            Secrets.GT_API_SECRET, lastCommLinkPublished, maxResults)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(comm_links -> {
                                for (CommLinkModel comm_link : comm_links) {
                                    if (favorites.containsKey(String.valueOf(comm_link.getCommLinkId()))) {
                                        comm_link.favorite = true;
                                    }
                                }
                                action.getData().put(Keys.COMM_LINKS,
                                        new ArrayList<>(Arrays.asList(comm_links)));
                                postRxAction(action);
                            }, throwable -> {
                                postError(action, throwable);
                            }));
                });
    }

    /**
     * Initiates the process to get the content wrappers for a specific comm link.
     *
     * @param id The comm link id
     */
    @Override
    public void getCommLinkContentWrappers(Long id) {
        final RxAction action = newRxAction(GET_COMM_LINK_CONTENT_WRAPPERS, Keys.COMM_LINK_ID, id);
        if (hasRxAction(action)) return;

        addRxAction(action, CommLinkWrapperApiService.Factory.getInstance().getCommLinkWrappers(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(parts -> {
                    for (Wrapper wrapper : parts) {
                        ContentBlock1 cb1 = wrapper.getContentBlock1();
                        if (cb1 != null &&
                                cb1.getContent().size() > 0 &&
                                cb1.getContent().get(0).contains("embed-container youtube")) {
                            String[] split = cb1.getContent().get(0).split("youtube.com/embed/");
                            if (split.length > 1 && split[1].contains("?wmode=")) {
                                cb1.youtube = true;
                                cb1.youtubeVideoId = split[1].split("wmode=")[0].replace("?", "");
                                ArrayList<String> strings = new ArrayList<>();
                                String content = ""
                                        + "<a href=\"https://www.youtube.com/watch?v=" + cb1.youtubeVideoId + "\" >"
                                        + "<img src=\"http://img.youtube.com/vi/" + cb1.youtubeVideoId + "/0.jpg\">"
                                        + "</a>";
                                strings.add(content);
                                cb1.setContent(strings);
                            }
                        }
                        if (wrapper.getContentBlock2() != null &&
                                wrapper.getContentBlock2().headerImageType == ContentBlock2.TYPE_SLIDESHOW) {
                            int size = wrapper.getContentBlock2().getHeaderImages().size();
                            wrapper.getContentBlock2().getHeaderImages().remove(size - 1);
                            wrapper.getContentBlock2().getHeaderImages().remove(0);
                        }
                    }
                    action.getData().put(Keys.COMM_LINK_CONTENT_WRAPPERS, new ArrayList<>(parts));
                    GtActionCreator.this.postRxAction(action);
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

        getFavoritesInternal(Favorite.TYPE_SHIP)
                .observeOn(Schedulers.io())
                .subscribe(favorites -> {
                    // We first need all favorite ships before we can get the ship data,
                    // as we have to set the models favorite attribute
                    // TODO: A much much more elegant solution would be a Custom StorIO TypeMapping!
                    addRxAction(action, ShipApiService.Factory.getInstance().getShips()
                            .subscribeOn(Schedulers.io())
                            .map(shipData -> {
                                for (Ship ship : shipData.ships) {
                                    if (favorites.containsKey(ship.titlecontainer.title))
                                        ship.favorite = true;
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
                });
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
                    if (user.data != null) {
                        action.getData().put(Keys.USER_DATA_SEARCH_SUCCESSFUL, true);
                        action.getData().put(Keys.USER_DATA, user);
                    } else {
                        action.getData().put(Keys.USER_DATA_SEARCH_SUCCESSFUL, false);
                    }
                    action.getData().put(Keys.USER_HANDLE, userHandle);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error searching for user: %s", throwable.getCause());
                    postError(action, throwable);
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getUserSearchHistory() {
        RxAction action = newRxAction(GET_USER_SEARCH_HISTORY);
        if (hasRxAction(action)) return;

        Query q = Query.builder()
                .table(UserSearchHistoryEntryTable.TABLE)
                .build();

        GtApplication.getInstance().getStorIOSQLite()
                .get()
                .listOfObjects(UserSearchHistoryEntry.class)
                .withQuery(q)
                .prepare()
                .asRxObservable()
                .map(list -> {
                    ArrayList<UserSearchHistoryEntry> alist = new ArrayList<>(list);
                    Collections.reverse(alist);
                    return alist;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userSearchHistoryEntries -> {
                    action.getData().put(Keys.USER_SEARCH_HISTORY_ENTRIES,
                            new ArrayList<>(userSearchHistoryEntries));
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error getting last user searches: %s", throwable.getCause());
                    postError(action, throwable);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pushNewUserSearchToDb(UserSearchHistoryEntry e) {
        // TODO: Add logic to the method to delete oldest entry if we have more entries than defined in MAX_USER_SEARCH_ENTRIES
        // also search for the handle and move the entry to the top instead of creating a duplicate

        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();

        storio.put()
                .object(e)
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(wrapperPutResults -> {
                    getUserSearchHistory();
                }, throwable -> {
                    Timber.d("Error putting %s entry to DB", e.handle);
                    Timber.d(throwable.getCause().toString());
                    Timber.d(throwable.toString());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getOrganizationById(String id) {
        RxAction action = newRxAction(GET_ORGANIZATION_BY_ID, Keys.ORGANIZATION_ID, id);

        if (hasRxAction(action)) return;

        addRxAction(action, OrganizationApiService.Factory.getInstance().getOrganization(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(organization -> {
                    action.getData().put(Keys.ORGANIZATION_DATA, organization);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error getting organization with id %s", id);
                    Timber.d(throwable.getCause().toString());
                    postError(action, throwable);
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getStarMapBootUpData() {
        RxAction action = newRxAction(GET_STARMAP_BOOT_UP_DATA);

        if (hasRxAction(action)) return;

        addRxAction(action, StarMapService.Factory.getInstance().getBootupData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(starMapData -> {
                    // calculate the star map origin (center point) for applying a scale factor later on
                    Vector2 origin = new Vector2();
                    for (SystemsResultset s : starMapData.data.systems.resultset) {
                        origin.add(s.positionX, s.positionY);
                    }

                    origin.x = origin.x / starMapData.data.systems.rowcount;
                    origin.y = origin.y / starMapData.data.systems.rowcount;
                    starMapData.data.origin = origin;

                    Vector2 dist = new Vector2();
                    HashMap<Integer, SystemsResultset> systemHashMap = new HashMap<>();
                    for (SystemsResultset s : starMapData.data.systems.resultset) {
                        // calculate distance between starmap origin and system
                        dist.x = s.positionX - origin.x;
                        dist.y = s.positionY - origin.y;

                        // scale the distance by factor x
                        dist.scl(80);

                        // set systems origin to the scaled distance relative to starmap origin
                        s.positionX = dist.x + origin.x;
                        s.positionY = dist.y + origin.y;

                        // Add the system to a HashMap so we can retrieve single systems by id
                        systemHashMap.put(s.id, s);
                        s.generateBoundingCircle();
                    }
                    starMapData.data.systemHashMap = systemHashMap;

                    action.getData().put(Keys.STARMAP_BOOTUP_DATA, starMapData);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error getting Starmap boot up data");
                    Timber.d(throwable.getCause().toString());
                    postError(action, throwable);
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getForumsAll() {
        RxAction action = newRxAction(GET_FORUMS_ALL);

        if (hasRxAction(action)) return;

        addRxAction(action, ForumsApiService.Factory.getInstance().getForums()
                .subscribeOn(Schedulers.io())
                .map(forumsObject -> {
                    ArrayList<ForumSectioned> newList =
                            new ArrayList<>();
                    String section = Utility.getForumSectionForForumId(
                            GtApplication.getInstance(), forumsObject.data.get(0).forumId);
                    String nextSection;

                    ForumSectioned f = new ForumSectioned();
                    f.type = ForumSectioned.TYPE_SECTION_HEADER;
                    f.spanCount = GtApplication.getContext().getResources()
                            .getInteger(R.integer.forum_list_column_count);
                    f.section = section;

                    newList.add(f);

                    List<Forum> data = forumsObject.data;
                    for (int i = 0; i < data.size(); i++) {
                        Forum forum = data.get(i);
                        if (forum.forumDiscussionCountString != null && !forum.forumDiscussionCountString.equals("")) {
                            forum.forumDiscussionCount = Integer.parseInt(forum.forumDiscussionCountString);
                        } else {
                            forum.forumDiscussionCount = 0;
                        }
                        if (forum.forumPostCountString != null && !forum.forumPostCountString.equals("")) {
                            forum.forumPostCount = Integer.parseInt(forum.forumPostCountString);
                        } else {
                            forum.forumPostCount = 0;
                        }

                        f = new ForumSectioned();
                        f.type = ForumSectioned.TYPE_FORUM;
                        f.forum = forum;
                        f.section = section;
                        newList.add(f);

                        // peek next => if distinct add a new section header item
                        if (data.size() > i + 1) {
                            nextSection = Utility.getForumSectionForForumId(
                                    GtApplication.getContext(),
                                    data.get(i + 1).forumId
                            );
                            if (!section.equals(nextSection)) {
                                section = nextSection;
                                f = new ForumSectioned();
                                f.type = ForumSectioned.TYPE_SECTION_HEADER;
                                f.section = section;
                                f.spanCount = GtApplication.getContext().getResources()
                                        .getInteger(R.integer.forum_list_column_count);
                                newList.add(f);
                            }
                        }
                    }

                    return newList;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(forums -> {
                    action.getData().put(Keys.FORUM_DATA_ALL, forums);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error getting forums data");
                    Timber.d(throwable.getCause().toString());
                    postError(action, throwable);
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getForumThreads(String forumId, int page) {
        RxAction action = newRxAction(GET_FORUM_THREADS, Keys.FORUM_ID, forumId, Keys.PAGINATION_CURRENT_PAGE, page);

        if (hasRxAction(action)) return;

        // The API's pagination feature is broken, so we abstract the start and end-page away from here on
        addRxAction(action, ForumsApiService.Factory.getInstance().getTreads(forumId, page, page)
                .subscribeOn(Schedulers.io())
                .map(forumsObject -> forumsObject.data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(threads -> {
                    if (threads == null) {
                        // if threads is null here create an empty list to indicate the end of the
                        // data stream. There are no more forum threads.
                        threads = new ArrayList<>(0);
                    }
                    action.getData().put(Keys.FORUM_ID, forumId);
                    action.getData().put(Keys.PAGINATION_CURRENT_PAGE, page);
                    action.getData().put(Keys.FORUM_THREADS_FOR_PAGE, threads);

                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error getting threads data for Forum %s", forumId);
                    Timber.d(throwable.getCause().toString());
                    postError(action, throwable);
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getForumThreadPosts(long threadId, int page) {
        RxAction action = newRxAction(GET_FORUM_THREAD_POSTS, Keys.FORUM_THREAD_ID,
                threadId, Keys.PAGINATION_CURRENT_PAGE, page);

        if (hasRxAction(action)) return;

        // The API's pagination feature is broken, so we abstract the start and end-page away from here on
        addRxAction(action, ForumsApiService.Factory.getInstance().getPosts(threadId, page, page)
                .subscribeOn(Schedulers.io())
                .map(postsObject -> {
                    ArrayList<ForumThreadPost> posts = new ArrayList<>();
                    for (ForumThreadPost forumThreadPost : postsObject.data) {
                        if (forumThreadPost.author != null
                                && forumThreadPost.author.handle != null
                                && !forumThreadPost.author.handle.equals("")) {
                            posts.add(forumThreadPost);
                        }
                    }
                    postsObject.data = posts;
                    return postsObject.data;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    action.getData().put(Keys.FORUM_THREAD_ID, threadId);
                    action.getData().put(Keys.PAGINATION_CURRENT_PAGE, page);
                    action.getData().put(Keys.FORUM_THREAD_POSTS_FOR_PAGE, posts);
                    postRxAction(action);
                }, throwable -> {
                    Timber.d("Error getting posts data for Thread %s", threadId);
                    Timber.d(throwable.getCause().toString());
                    Timber.d("Stacktrace \n %s", throwable.getCause().getStackTrace());
                    postError(action, throwable);
                }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFavorite(Favorite favorite) {
        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();

        storio.put()
                .object(favorite)
                .prepare()
                .asRxObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wrapperPutResults -> {
                    if (favorite.type == Favorite.TYPE_SHIP) {
                        // Update the Ship data model
                        ShipStore shipStore = ShipStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());
                        Ship ship = shipStore.getShipById(favorite.reference);
                        ship.favorite = true;

                        // Post a new RxAction to notify all listeners to notify all
                        // Stores and Dispatchers of the change
                        RxAction a = newRxAction(SHIP_DATA_UPDATED);
                        ArrayList<Ship> shipList = new ArrayList<>(1);
                        shipList.add(ship);
                        a.getData().put(Keys.SHIP_DATA_LIST, shipList);
                        postRxAction(a);
                    } else if (favorite.type == Favorite.TYPE_COMM_LINK) {
                        CommLinkStore commLinkStore = CommLinkStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());
                        CommLinkModel cl = commLinkStore.getCommLink(Long.valueOf(favorite.reference));
                        cl.favorite = true;

                        // update the data object
                        RxAction a = newRxAction(COMM_LINK_DATA_UPDATED);
                        ArrayList<CommLinkModel> clList = new ArrayList<>(1);
                        clList.add(cl);
                        a.getData().put(Keys.COMM_LINKS, clList);
                        postRxAction(a);
                    }
                }, throwable -> {
                    Timber.d("Error putting reference %s to DB", favorite.reference);
                    Timber.d(throwable.getCause().toString());
                    Timber.d(throwable.toString());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getFavorites() {
        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();
        Query q = Query.builder()
                .table(FavoritesTable.TABLE)
                .build();

        storio.get()
                .listOfObjects(Favorite.class)
                .withQuery(q)
                .prepare()
                .asRxObservable()
                .observeOn(Schedulers.io())
                .subscribe(favorites -> {
                    Timber.d("Got %s faves", favorites.size());
                }, throwable -> {
                    Timber.d("Error getting favorites from DB");
                    Timber.d(throwable.getCause().toString());
                    Timber.d(throwable.toString());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFavorite(Favorite favorite) {
        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();
        DeleteQuery q = DeleteQuery.builder()
                .table(FavoritesTable.TABLE)
                .where(FavoritesTable.COLUMN_TYPE + "=? and "
                        + FavoritesTable.COLUMN_REFERENCE + "=?")
                .whereArgs(favorite.type, favorite.reference)
                .build();

        storio.delete()
                .byQuery(q)
                .prepare()
                .asRxSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deleteResult -> {
                    if (favorite.type == Favorite.TYPE_SHIP) {
                        // Update the Ship data model
                        ShipStore shipStore = ShipStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());
                        Ship ship = shipStore.getShipById(favorite.reference);
                        ship.favorite = false;

                        // Post a new RxAction to notify all listeners to notify all
                        // Stores and Dispatchers of the change
                        RxAction a = newRxAction(SHIP_DATA_UPDATED);
                        ArrayList<Ship> shipList = new ArrayList<>(1);
                        shipList.add(ship);
                        a.getData().put(Keys.SHIP_DATA_LIST, shipList);
                        postRxAction(a);
                    } else if (favorite.type == Favorite.TYPE_COMM_LINK) {
                        CommLinkStore commLinkStore = CommLinkStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());
                        CommLinkModel cl = commLinkStore.getCommLink(Long.valueOf(favorite.reference));
                        cl.favorite = false;

                        // update the data object
                        RxAction a = newRxAction(COMM_LINK_DATA_UPDATED);
                        ArrayList<CommLinkModel> clList = new ArrayList<>(1);
                        clList.add(cl);
                        a.getData().put(Keys.COMM_LINKS, clList);
                        postRxAction(a);
                    }
                });
    }

    /**
     * This is for use the GtActionCreator only.
     * This will also add the Favorite to the store
     *
     * @return An rx.Single for the favorites list
     */
    private Single<HashMap<String, Favorite>> getFavoritesInternal(int type) {
        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();
        Query q = Query.builder()
                .table(FavoritesTable.TABLE)
                .where(FavoritesTable.COLUMN_TYPE + "=?")
                .whereArgs(type)
                .build();

        return storio.get()
                .listOfObjects(Favorite.class)
                .withQuery(q)
                .prepare()
                .asRxSingle()
                .map(favorites -> {
                    HashMap<String, Favorite> hm = new HashMap<>(favorites.size());
                    for (Favorite favorite : favorites) {
                        hm.put(favorite.reference, favorite);
                    }
                    return hm;
                });
    }

    /**
     * Search for a favorite when the exact reference is known
     *
     * @param type  Type of the Favorite. For example {@link Favorite#TYPE_COMM_LINK}
     * @param value Reference value of the Favorite
     * @return @return An rx.Single for the favorites
     */
    private Observable<Favorite> getFavoritesInternal(int type, String value) {
        DefaultStorIOSQLite storio = GtApplication.getInstance().getStorIOSQLite();
        Query q = Query.builder()
                .table(FavoritesTable.TABLE)
                .where(FavoritesTable.COLUMN_TYPE + "=? and " + FavoritesTable.COLUMN_REFERENCE + "=?")
                .whereArgs(type, value)
                .build();

        return storio.get()
                .object(Favorite.class)
                .withQuery(q)
                .prepare()
                .asRxObservable();
    }
}
