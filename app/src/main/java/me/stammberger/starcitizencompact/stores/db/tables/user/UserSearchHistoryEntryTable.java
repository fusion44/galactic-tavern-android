package me.stammberger.starcitizencompact.stores.db.tables.user;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO.
 * This is for storing entries of recently searched users in {@link me.stammberger.starcitizencompact.ui.users.UserFragment}
 */
public class UserSearchHistoryEntryTable {
    public static final String TABLE = "user_search_history_entries";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_SUCCESSFUL = "successful";

    @NonNull
    public static final String COLUMN_HANDLE = "user_handle";

    @NonNull
    public static final String COLUMN_SEARCH_DATE = "search_date";

    @NonNull
    public static final String COLUMN_AVATAR_URL = "avatar_url";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_SUCCESSFUL + " INTEGER NOT NULL, "
                + COLUMN_HANDLE + " TEXT NOT NULL, "
                + COLUMN_SEARCH_DATE + " LONG INTEGER NOT NULL, "
                + COLUMN_AVATAR_URL + " TEXT NOT NULL"
                + ");";
    }
}
