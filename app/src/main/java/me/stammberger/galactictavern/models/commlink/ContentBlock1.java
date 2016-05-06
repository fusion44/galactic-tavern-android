package me.stammberger.galactictavern.models.commlink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.galactictavern.core.Utility;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock1Table;

/**
 * This the content block 1 of a {@link Wrapper}.
 * This contains the body of the wrapper in html.
 * The RSI website further divides the body into segments which makes an ArrayList necessary
 */
@StorIOSQLiteType(table = ContentBlock1Table.TABLE)
public class ContentBlock1 {
    public static final String DATA_SEPARATOR = "@#|_|#@";

    /**
     * SQLite ID of this clock. Null if not yet saved to SQLite
     * The {@link Wrapper} will store this id to this DB entry
     */
    @StorIOSQLiteColumn(name = ContentBlock1Table.COLUMN_ID, key = true)
    public Long id;
    /**
     * The {@link #content} as one single string for storage in SQLite
     */
    @StorIOSQLiteColumn(name = ContentBlock1Table.COLUMN_CONTENT)
    public String contentDb = "";
    /**
     * The content (body) of the wrapper
     */
    @SerializedName("content")
    @Expose
    private List<String> content = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public ContentBlock1() {
    }

    /**
     * @param content The content as a String List
     */
    public ContentBlock1(List<String> content) {
        this.content = content;
        this.contentDb = Utility.generateDbStringFromStringList(content, DATA_SEPARATOR);
    }

    /**
     * TODO: remove this horrible hack
     */
    public void genDbData() {
        this.contentDb = Utility.generateDbStringFromStringList(content, DATA_SEPARATOR);
    }

    /**
     * @return The content
     */
    public List<String> getContent() {
        if (content.size() == 0 && !contentDb.equals("")) {
            this.content = Utility.parseStringListFromDbString(contentDb, DATA_SEPARATOR);
        }

        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(List<String> content) {
        this.content = content;
        this.contentDb = Utility.generateDbStringFromStringList(content, DATA_SEPARATOR);
    }
}
