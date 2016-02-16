package me.stammberger.starcitizencompact.stores.db.tables.commlink;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel.CommLinkModelContent}
 */
public class CommLinkModelTable {
    public static final String TABLE = "comm_links";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_SOURCE_URI = "source_uri";

    @NonNull
    public static final String COLUMN_COMM_LINK_ID = "comm_link_id";

    @NonNull
    public static final String COLUMN_TITLE = "title";

    @NonNull
    public static final String COLUMN_PUBLISHED_DATE = "published_date";

    @NonNull
    public static final String COLUMN_TYPE = "type";

    @NonNull
    public static final String COLUMN_SUMMARY = "summary";

    @NonNull
    public static final String COLUMN_BACKDROP_URL = "backdrop_url";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_SOURCE_URI + " TEXT NOT NULL, "
                + COLUMN_COMM_LINK_ID + " INTEGER NOT NULL, "
                + COLUMN_BACKDROP_URL + " TEXT NOT NULL, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_PUBLISHED_DATE + " TEXT NOT NULL, "
                + COLUMN_TYPE + " TEXT NOT NULL, "
                + COLUMN_SUMMARY + " TEXT NOT NULL"
                + ");";
    }
}
