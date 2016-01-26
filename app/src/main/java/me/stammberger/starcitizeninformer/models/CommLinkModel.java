package me.stammberger.starcitizeninformer.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkTable;

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
     * Row ids of the belonging content parts encoded to a string.
     * Separated by {@link #DATA_SEPARATOR}
     */
    @StorIOSQLiteColumn(name = CommLinkTable.COLUMN_CONTENT_IDS)
    public String contentIds;
    /**
     * RSS Content parts of the comm link.
     */
    public ArrayList<CommLinkModelContentPart> content;
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

    public ArrayList<CommLinkModelContentPart> getContent() {
        return content;
    }

    /**
     * Sets the content parts objects only. Will not update the Ids field.
     *
     * @param content ArrayList with {@link CommLinkModelContentPart}
     */
    public void setContent(@NonNull ArrayList<CommLinkModelContentPart> content) {
        this.content = content;
    }

    /**
     * Sets the content parts and the belonging row Ids
     *
     * @param content ArrayList with {@link CommLinkModelContentPart}
     */
    public void setContentAndIds(@NonNull ArrayList<CommLinkModelContentPart> content) {
        generateContentIdString(content);
        this.content = content;
    }

    public List<Long> getContentIds() {
        ArrayList<Long> list = new ArrayList<>();
        if (contentIds != null && !contentIds.equals("")) {
            String[] split = contentIds.split(DATA_SEPARATOR);
            for (String s : split) {
                Long id = Long.valueOf(s);
                list.add(id);
            }
        }
        return list;
    }

    /**
     * Sets content Ids only without keeping a reference to the objects
     *
     * @param content ArrayList<CommLinkModelContentPart> content
     */
    public void setContentIds(@NonNull ArrayList<CommLinkModelContentPart> content) {
        generateContentIdString(content);
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
     * Provides shortcut method to generate the content part id's string
     *
     * @param content ArrayList<CommLinkModelContentPart> content
     */
    private void generateContentIdString(ArrayList<CommLinkModelContentPart> content) {
        if (contentIds == null || contentIds.equals("")) {
            contentIds = "";
            for (int i = 0; i < content.size(); i++) {
                CommLinkModelContentPart cp = content.get(i);
                if (i + 1 < content.size()) {
                    if (contentIds == null) {
                        contentIds = cp.id + CommLinkModel.DATA_SEPARATOR;
                    } else {
                        contentIds += cp.id + CommLinkModel.DATA_SEPARATOR;
                    }
                } else {
                    if (contentIds == null) {
                        contentIds = String.valueOf(cp.id);
                    } else {
                        contentIds += cp.id;
                    }
                }
            }
        }
    }
}
