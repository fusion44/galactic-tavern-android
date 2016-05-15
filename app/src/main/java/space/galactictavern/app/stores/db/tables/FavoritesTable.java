package space.galactictavern.app.stores.db.tables;

import android.support.annotation.NonNull;


/**
 * Table data class for StorIO.
 * This is for storing a simple favorites data structure
 */
public class FavoritesTable {
    public static final String TABLE = "favorites";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_DATE = "date";

    @NonNull
    public static final String COLUMN_TYPE = "type";

    @NonNull
    public static final String COLUMN_REFERENCE = "id_value";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_DATE + " LONG INTEGER NOT NULL, "
                + COLUMN_TYPE + " INTEGER NOT NULL, "
                + COLUMN_REFERENCE + " STRING UNIQUE NOT NULL"
                + ");";
    }
}
