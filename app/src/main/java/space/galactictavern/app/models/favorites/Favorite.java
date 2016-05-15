package space.galactictavern.app.models.favorites;


import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.models.forums.Forum;
import space.galactictavern.app.models.forums.ForumThread;
import space.galactictavern.app.models.orgs.Organization;
import space.galactictavern.app.models.ship.Ship;
import space.galactictavern.app.models.user.User;
import space.galactictavern.app.stores.db.tables.FavoritesTable;


@StorIOSQLiteType(table = FavoritesTable.TABLE)
public class Favorite {
    /**
     * Defines for reference type {@link CommLinkModel}
     */
    public static final int TYPE_COMM_LINK = 0;

    /**
     * Defines for reference type {@link Ship}
     */
    public static final int TYPE_SHIP = 1;

    /**
     * Defines for reference type {@link User}
     */
    public static final int TYPE_USER = 2;

    /**
     * Defines for reference type {@link Forum}
     */
    public static final int TYPE_FORUM = 3;

    /**
     * Defines for reference type {@link ForumThread}
     */
    public static final int TYPE_FORUM_THREAD = 4;

    /**
     * Defines for reference type {@link Organization}
     */
    public static final int TYPE_ORGANIZATION = 5;

    @StorIOSQLiteColumn(name = FavoritesTable.COLUMN_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = FavoritesTable.COLUMN_DATE)
    public Long date;

    @StorIOSQLiteColumn(name = FavoritesTable.COLUMN_TYPE)
    public Integer type;

    @StorIOSQLiteColumn(name = FavoritesTable.COLUMN_REFERENCE)
    public String reference;
}
