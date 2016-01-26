package me.stammberger.starcitizeninformer.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;

import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkContentPartTable;
import timber.log.Timber;

/**
 * Holds all information about the main content of the comm link
 * Depending on {@link #type} different fields will be filled with data.
 */
@StorIOSQLiteType(table = CommLinkContentPartTable.TABLE)
public class CommLinkModelContentPart implements Parcelable {
    /**
     * Establishes the part as an text block. Use for {@link #type}
     */
    public static final int CONTENT_TYPE_TEXT_BLOCK = 0;

    /**
     * Establishes the part as a slideshow. Use for {@link #type}
     */
    public static final int CONTENT_TYPE_SLIDESHOW = 1;
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommLinkModelContentPart> CREATOR = new Parcelable.Creator<CommLinkModelContentPart>() {
        @Override
        public CommLinkModelContentPart createFromParcel(Parcel in) {
            return new CommLinkModelContentPart(in);
        }

        @Override
        public CommLinkModelContentPart[] newArray(int size) {
            return new CommLinkModelContentPart[size];
        }
    };
    /**
     * If object was not inserted into db, id will be null
     */
    @Nullable
    @StorIOSQLiteColumn(name = CommLinkContentPartTable.COLUMN_ID, key = true)
    public Long id;
    /**
     * The url of the {@link CommLinkModel} to which this part belongs to.
     */
    @StorIOSQLiteColumn(name = CommLinkContentPartTable.COLUMN_SOURCE_URI, key = true)
    public String sourceUrl;
    /**
     * Defines the type of data present.
     * Must be one of {@link #CONTENT_TYPE_TEXT_BLOCK} or {@link #CONTENT_TYPE_SLIDESHOW}
     */
    @StorIOSQLiteColumn(name = CommLinkContentPartTable.COLUMN_TYPE)
    public int type;
    /**
     * A String which is either an encoded list of URL's or a String with RSS formatting depending on {@link #type}.
     * Do not use directly as it will be encoded for database storage but needs to be public for StorIO
     * Always use getters and setters corresponding to {@link #type}
     * Use {@link android.text.Html#fromHtml(String)} to pass the text to an {@link android.widget.TextView} to get proper formatting
     */
    @StorIOSQLiteColumn(name = CommLinkContentPartTable.COLUMN_CONTENT)
    public String content;

    public CommLinkModelContentPart() {
    }

    public CommLinkModelContentPart(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public CommLinkModelContentPart(Parcel in) {
        this.type = in.readInt();
        this.content = in.readString();
    }

    /**
     * Gets the text content.
     *
     * @return A String with the text content. Empty String if type is wrong.
     */
    public String getTextContent() {
        if (content != null && type == CONTENT_TYPE_TEXT_BLOCK) {
            return content;
        } else {
            Timber.e("Trying to receive links from a CommLinkContentPart of wrong type!");
            return "";
        }
    }

    /**
     * Sets the text content. Automatically sets {@link #type} to {@link #CONTENT_TYPE_TEXT_BLOCK}
     *
     * @param content String with text contents
     */
    public void setTextContent(String content) {
        this.type = CONTENT_TYPE_TEXT_BLOCK;
        this.content = content;
    }

    /**
     * Sets the slideshow links. Automatically sets {@link #type} to {@link #CONTENT_TYPE_SLIDESHOW}
     *
     * @param links ArrayList of strings with links
     */
    public void setSlideShowLinks(ArrayList<String> links) {
        type = CONTENT_TYPE_SLIDESHOW;
        for (int i1 = 0; i1 < links.size(); i1++) {
            String s = links.get(i1);
            if (i1 + 1 < links.size()) {
                if (content == null) {
                    content = s + CommLinkModel.DATA_SEPARATOR;
                } else {
                    content += s + CommLinkModel.DATA_SEPARATOR;
                }
            } else {
                if (content == null) {
                    content = s;
                } else {
                    content += s;
                }
            }
        }
    }

    /**
     * Gets the links for this part
     *
     * @return String Array of links
     */
    public String[] getSlideshowLinks() {
        if (content != null && type == CONTENT_TYPE_SLIDESHOW) {
            return content.split(CommLinkModel.DATA_SEPARATOR);
        } else {
            Timber.e("Trying to receive links from a CommLinkContentPart of wrong type!");
            return new String[]{""};
        }
    }

    @Override
    public int describeContents() {
        return type;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(content);
    }
}