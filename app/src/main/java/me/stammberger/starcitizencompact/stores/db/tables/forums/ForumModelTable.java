package me.stammberger.starcitizencompact.stores.db.tables.forums;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO.
 */
public class ForumModelTable {
    public static final String TABLE = "forums";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_FORUM_ID = "forum_id";

    @NonNull
    public static final String COLUMN_FORUM_DISCUSSION_COUNT = "discussion_count";

    @NonNull
    public static final String COLUMN_FORUM_POST_COUNT = "post_count";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_FORUM_ID + " TEXT NOT NULL, "
                + COLUMN_FORUM_DISCUSSION_COUNT + " INTEGER NOT NULL, "
                + COLUMN_FORUM_POST_COUNT + " INTEGER NOT NULL"
                + ");";
    }
}
