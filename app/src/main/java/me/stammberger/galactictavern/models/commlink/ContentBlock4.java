package me.stammberger.galactictavern.models.commlink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock1Table;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock4Table;

/**
 * This will simply hold the header text
 */
@StorIOSQLiteType(table = ContentBlock4Table.TABLE)
public class ContentBlock4 {

    /**
     * SQLite ID of this clock. Null if not yet saved to SQLite
     * The {@link Wrapper} will store this id to this DB entry
     */
    @StorIOSQLiteColumn(name = ContentBlock1Table.COLUMN_ID, key = true)
    public Long id;

    /**
     * The header text.
     */
    @SerializedName("header")
    @Expose
    @StorIOSQLiteColumn(name = ContentBlock4Table.COLUMN_TEXT)
    public String header;

    /**
     * No args constructor for use in serialization
     */
    public ContentBlock4() {
    }

    /**
     * @param header Header text
     */
    public ContentBlock4(String header) {
        this.header = header;
    }

    /**
     * @return The header text
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header The header text
     */
    public void setHeader(String header) {
        this.header = header;
    }
}
