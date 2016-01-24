package me.stammberger.starcitizeninformer.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;

import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkTable;
import me.stammberger.starcitizeninformer.ui.commlinks.CommLinkReaderSlideshowAdapter;

/**
 * Stores data necessary for displaying a comm link in a list
 *
 * @StorIOSQLiteType Triggers annotation processor to generate type mapping at compile time
 */
@StorIOSQLiteType(table = CommLinkTable.TABLE)
public class CommLinkModel extends BaseModel implements Parcelable {
    public static final String DATA_SEPARATOR = "#";

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CommLinkModel> CREATOR = new Parcelable.Creator<CommLinkModel>() {
        @Override
        public CommLinkModel createFromParcel(Parcel in) {
            return new CommLinkModel(in);
        }

        @Override
        public CommLinkModel[] newArray(int size) {
            return new CommLinkModel[size];
        }
    };

    /**
     * Permanent {@link Uri} to robertsspaceindustries.com
     */
    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_SOURCE_URI, key = true)
    public String sourceUri;
    /**
     * Title of the comm link
     */

    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_TITLE)
    public String title;
    /**
     * RSS slideshowLinks parts of the comm link.
     */
    public ArrayList<CommLinkContentPart> content;
    /**
     * Release date of the comm link
     */
    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_DATE)
    public long date;

    /**
     * Short slideshowLinks description of the comm link
     */
    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_DESCRIPTION)
    public String description;

    /**
     * Associated tags for the comm links.
     * Separated by {@link #DATA_SEPARATOR}
     */
    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_TAGS)
    public String tags;

    /**
     * Url for the backdrop image.
     */
    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_BACKDROP_URL)
    public String backdropUrl;

    /**
     * Empty constructor necessary for StorIO generated code
     */
    public CommLinkModel() {
    }

    @SuppressWarnings("unchecked")
    protected CommLinkModel(Parcel in) {
        sourceUri = in.readString();
        title = in.readString();
        date = in.readLong();
        description = in.readString();
        tags = in.readString();
        layout = in.readInt();
        spanCount = in.readInt();
        backdropUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sourceUri);
        dest.writeString(title);
        dest.writeLong(date);
        dest.writeString(description);
        dest.writeString(tags);
        dest.writeInt(layout);
        dest.writeInt(spanCount);
        dest.writeString(backdropUrl);
    }

    /**
     * Holds all information about the main content of the comm link
     * Depending on {@link CommLinkContentPart#type} different fields will be filled with data.
     */
    public static class CommLinkContentPart implements Parcelable {
        /**
         * Establishes the part as an text block. Use for {@link #type}
         */
        public static final int CONTENT_TYPE_TEXT_BLOCK = 0;

        /**
         * Establishes the part as a slideshow. Use for {@link #type}
         */
        public static final int CONTENT_TYPE_SLIDESHOW = 1;
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<CommLinkContentPart> CREATOR = new Parcelable.Creator<CommLinkContentPart>() {
            @Override
            public CommLinkContentPart createFromParcel(Parcel in) {
                return new CommLinkContentPart(in);
            }

            @Override
            public CommLinkContentPart[] newArray(int size) {
                return new CommLinkContentPart[size];
            }
        };
        /**
         * Defines the type of data present.
         * Must be one of {@link #CONTENT_TYPE_TEXT_BLOCK} or {@link #CONTENT_TYPE_SLIDESHOW}
         */
        public int type;
        /**
         * A {@link String} with RSS formatting.
         * Use {@link android.text.Html#fromHtml(String)} to pass the text to an {@link android.widget.TextView} to get proper formatting
         */
        public String textContent;
        /**
         * Simply holds String of links to images which will be displayed by {@link CommLinkReaderSlideshowAdapter}
         */
        public ArrayList<String> slideshowLinks;

        public CommLinkContentPart(int type, String textContent, ArrayList<String> slideshowLinks) {
            this.type = type;
            this.textContent = textContent;
            this.slideshowLinks = slideshowLinks;
        }

        public CommLinkContentPart(Parcel in) {
            type = in.readInt();
            textContent = in.readString();
            if (in.readByte() == 0x01) {
                slideshowLinks = new ArrayList<>();
                in.readList(slideshowLinks, String.class.getClassLoader());
            } else {
                slideshowLinks = null;
            }
        }

        @Override
        public int describeContents() {
            return type;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(type);
            dest.writeString(textContent);
            if (slideshowLinks == null) {
                dest.writeByte((byte) (0x00));
            } else {
                dest.writeByte((byte) (0x01));
                dest.writeList(slideshowLinks);
            }
        }
    }
}
