package me.stammberger.galactictavern.models.user;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import me.stammberger.galactictavern.stores.db.tables.user.UserSearchHistoryEntryTable;

/**
 * Represents a recent search for a user in the API
 * <p>
 * We don't want to store the whole User object to the database to save space.
 * This can be pulled from the API each time a search is conducted.
 * Also, data might be updated on the backend server.
 */
@StorIOSQLiteType(table = UserSearchHistoryEntryTable.TABLE)
public class UserSearchHistoryEntry {
    @StorIOSQLiteColumn(name = UserSearchHistoryEntryTable.COLUMN_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = UserSearchHistoryEntryTable.COLUMN_SUCCESSFUL)
    public Boolean successful;

    @StorIOSQLiteColumn(name = UserSearchHistoryEntryTable.COLUMN_HANDLE)
    public String handle;

    @StorIOSQLiteColumn(name = UserSearchHistoryEntryTable.COLUMN_SEARCH_DATE)
    public Long searchDate;

    @StorIOSQLiteColumn(name = UserSearchHistoryEntryTable.COLUMN_AVATAR_URL)
    public String avatarUrl;
}
