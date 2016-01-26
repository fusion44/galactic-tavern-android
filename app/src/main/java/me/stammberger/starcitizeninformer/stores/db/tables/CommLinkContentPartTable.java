package me.stammberger.starcitizeninformer.stores.db.tables;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel.CommLinkContentPart}
 */
public class CommLinkContentPartTable {
    @NonNull
    public static final String TABLE = "comm_link_parts";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_SOURCE_URI = "source_uri";

    @NonNull
    public static final String COLUMN_TYPE = "type";

    @NonNull
    public static final String COLUMN_CONTENT = "content";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_SOURCE_URI + " TEXT NOT NULL, "
                + COLUMN_TYPE + " UNSIGNED INTEGER NOT NULL, "
                + COLUMN_CONTENT + " TEXT NOT NULL "
                + ");";
    }
}
