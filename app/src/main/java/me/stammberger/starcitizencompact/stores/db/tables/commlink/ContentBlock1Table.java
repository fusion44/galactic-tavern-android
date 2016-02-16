package me.stammberger.starcitizencompact.stores.db.tables.commlink;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel.ContentBlock1}
 */
public class ContentBlock1Table {
    public static final String TABLE = "comm_link_content_block_1";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_CONTENT = "content";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_CONTENT + " TEXT NOT NULL"
                + ");";
    }
}