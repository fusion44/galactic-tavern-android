package space.galactictavern.app.models.commlink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import space.galactictavern.app.stores.db.tables.commlink.ContentWrapperTable;

/**
 * Holds a reference to all content blocks applicable to this content wrapper
 */
@StorIOSQLiteType(table = ContentWrapperTable.TABLE)
public class Wrapper {

    /**
     * SQLite ID of this wrapper. Null if not yet saved to SQLite
     */
    @StorIOSQLiteColumn(name = ContentWrapperTable.COLUMN_ID, key = true)
    public Long id;

    /**
     * Comm link ID where this wrapper belongs to. Null if not yet saved to SQLite
     */
    @StorIOSQLiteColumn(name = ContentWrapperTable.COLUMN_COMM_LINK_ID)
    public Long commLinkId;
    @StorIOSQLiteColumn(name = ContentWrapperTable.COLUMN_ID_BLOCK_1)
    public Long contentBlock1DbId;
    @StorIOSQLiteColumn(name = ContentWrapperTable.COLUMN_ID_BLOCK_2)
    public Long contentBlock2DbId;
    @StorIOSQLiteColumn(name = ContentWrapperTable.COLUMN_ID_BLOCK_4)
    public Long contentBlock4DbId;
    /**
     * Reference to the {@link ContentBlock1} (content body).
     */
    @SerializedName("content-block1")
    @Expose
    private ContentBlock1 contentBlock1;
    /**
     * Reference to the {@link ContentBlock2} (block image).
     */
    @SerializedName("content-block2")
    @Expose
    private ContentBlock2 contentBlock2;
    /**
     * Reference to the {@link ContentBlock4} (header text).
     */
    @SerializedName("content-block4")
    @Expose
    private ContentBlock4 contentBlock4;

    /**
     * No args constructor for use in serialization
     */
    public Wrapper() {

    }

    /**
     * @param contentBlock4 The content block 4
     * @param contentBlock2 The content block 2
     * @param contentBlock1 The content block 1
     */
    public Wrapper(ContentBlock4 contentBlock4, ContentBlock2 contentBlock2, ContentBlock1 contentBlock1) {
        this.contentBlock4 = contentBlock4;
        this.contentBlock2 = contentBlock2;
        this.contentBlock1 = contentBlock1;
    }

    /**
     * @return The contentBlock4
     */
    public ContentBlock4 getContentBlock4() {
        return contentBlock4;
    }

    /**
     * @param contentBlock4 The content-block4
     */
    public void setContentBlock4(ContentBlock4 contentBlock4) {
        this.contentBlock4 = contentBlock4;
    }

    /**
     * @return The contentBlock2
     */
    public ContentBlock2 getContentBlock2() {
        return contentBlock2;
    }

    /**
     * @param contentBlock2 The content-block2
     */
    public void setContentBlock2(ContentBlock2 contentBlock2) {
        this.contentBlock2 = contentBlock2;
    }

    /**
     * @return The contentBlock1
     */
    public ContentBlock1 getContentBlock1() {
        return contentBlock1;
    }

    /**
     * @param contentBlock1 The content-block1
     */
    public void setContentBlock1(ContentBlock1 contentBlock1) {
        this.contentBlock1 = contentBlock1;
    }
}
