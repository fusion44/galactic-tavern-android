package space.galactictavern.app.stores.db.tables.commlink;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel.ContentBlock2}
 */
public class ContentWrapperTable {
    public static final String TABLE = "content_wrappers";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_COMM_LINK_ID = "comm_link_id";

    @NonNull
    public static final String COLUMN_ID_BLOCK_4 = "id_block_4";

    @NonNull
    public static final String COLUMN_ID_BLOCK_2 = "id_block_2";

    @NonNull
    public static final String COLUMN_ID_BLOCK_1 = "id_block_1";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_COMM_LINK_ID + " INTEGER NOT NULL, "
                + COLUMN_ID_BLOCK_4 + " INTEGER, "
                + COLUMN_ID_BLOCK_2 + " INTEGER, "
                + COLUMN_ID_BLOCK_1 + " INTEGER"
                + ");";
    }
}