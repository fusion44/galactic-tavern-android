package me.stammberger.starcitizeninformer.models.commlink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.core.Utility;
import me.stammberger.starcitizeninformer.stores.db.tables.commlink.ContentBlock1Table;
import me.stammberger.starcitizeninformer.stores.db.tables.commlink.ContentBlock2Table;

/**
 * Content block which represents the header image for each wrapper
 */
@StorIOSQLiteType(table = ContentBlock2Table.TABLE)
public class ContentBlock2 {
    /**
     * This block only has a single image
     */
    public static final int TYPE_SINGLE = 0;

    /**
     * This block has multiple images and is handled as a slideshow
     */
    public static final int TYPE_SLIDESHOW = 1;

    public static String DATA_SEPARATOR = "#";
    /**
     * SQLite ID of this clock. Null if not yet saved to SQLite
     * The {@link Wrapper} will store this id to this DB entry
     */
    @StorIOSQLiteColumn(name = ContentBlock1Table.COLUMN_ID, key = true)
    public Long id;
    /**
     * Stored the type of the image content block
     */
    @SerializedName("header-image-type")
    @Expose
    @StorIOSQLiteColumn(name = ContentBlock2Table.COLUMN_HEADER_IMAGE_TYPE)
    public Integer headerImageType;
    /**
     * The {@link #headerImages} as one single string for storage in SQLite
     */
    @StorIOSQLiteColumn(name = ContentBlock2Table.COLUMN_IMAGES)
    public String headerImagesDb;
    /**
     * Stores a list of urls.
     * There will only be 1 url contained if {@link #headerImageType} is {@link ContentBlock2#TYPE_SINGLE}
     * otherwise there will be multiple images
     */
    @SerializedName("header-images")
    @Expose
    private List<String> headerImages = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public ContentBlock2() {
    }

    /**
     * @param headerImageType The image type.
     * @param headerImages    List of urls to the images
     */
    public ContentBlock2(Integer headerImageType, List<String> headerImages) {
        this.headerImageType = headerImageType;
        this.headerImagesDb = Utility.generateDbStringFromStringList(headerImages, DATA_SEPARATOR);
    }

    /**
     * TODO remove this horrible hack
     */
    public void genDbData() {
        this.headerImagesDb = Utility.generateDbStringFromStringList(headerImages, DATA_SEPARATOR);
    }

    /**
     * @return The image type
     */
    public Integer getHeaderImageType() {
        return headerImageType;
    }

    /**
     * @param headerImageType The image type
     */
    public void setHeaderImageType(Integer headerImageType) {
        this.headerImageType = headerImageType;
    }

    /**
     * @return The image url's
     */
    public List<String> getHeaderImages() {
        if (headerImages.size() == 0 && !headerImagesDb.equals("")) {
            headerImages = Utility.parseStringListFromDbString(headerImagesDb, DATA_SEPARATOR);
        }

        return headerImages;
    }

    /**
     * @param headerImages The image url's
     */
    public void setHeaderImages(List<String> headerImages) {
        this.headerImages = headerImages;
        this.headerImagesDb = Utility.generateDbStringFromStringList(headerImages, DATA_SEPARATOR);
    }
}
