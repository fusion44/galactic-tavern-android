package space.galactictavern.app.stores.db.tables.commlink;

import android.support.annotation.NonNull;

/**
 * Table data class for StorIO. For field name descriptions please refer to {@link .models.CommLinkModel.ContentBlock2}
 */
public class ContentBlock2Table {
    public static final String TABLE = "comm_link_content_block_2";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_HEADER_IMAGE_TYPE = "image_type";

    @NonNull
    public static final String COLUMN_IMAGES = "images";

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_HEADER_IMAGE_TYPE + " INTEGER NOT NULL, "
                + COLUMN_IMAGES + " TEXT NOT NULL"
                + ");";
    }
}