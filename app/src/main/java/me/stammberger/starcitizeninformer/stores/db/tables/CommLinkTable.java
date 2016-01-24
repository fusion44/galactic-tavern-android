package me.stammberger.starcitizeninformer.stores.db.tables;


import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel}
 */
public class CommLinkTable {

    @NonNull
    public static final String TABLE = "comm_links";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_SOURCE_URI = "source_uri";

    @NonNull
    public static final String COLUMN_TITLE = "title";

    @NonNull
    public static final String COLUMN_DATE = "date";

    @NonNull
    public static final String COLUMN_DESCRIPTION = "description";

    @NonNull
    public static final String COLUMN_TAGS = "tags";

    @NonNull
    public static final String COLUMN_BACKDROP_URL = "backdrop_url";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_SOURCE_URI + " TEXT NOT NULL UNIQUE, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_DATE + " UNSIGNED BIG INTEGER NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + COLUMN_TAGS + " TEXT NOT NULL, "
                + COLUMN_BACKDROP_URL + " TEXT NOT NULL "
                + ");";
    }
}
