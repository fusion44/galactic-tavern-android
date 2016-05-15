package space.galactictavern.app.stores.db.tables.commlink;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel.ContentBlock2}
 */
public class ContentBlock4Table {
    public static final String TABLE = "comm_link_content_block_4";

    @NonNull
    public static final String COLUMN_ID = "_id";


    @NonNull
    public static final String COLUMN_TEXT = "header_text";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_TEXT + " TEXT NOT NULL"
                + ");";
    }
}