package me.stammberger.starcitizeninformer.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Stores all the data necessary for displaying a comm-link
 */
public class CommLinkModel extends BaseModel implements Parcelable {

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
    public Uri sourceUri;
    /**
     * Title of the comm link
     */
    public String title;
    /**
     * RSS content body of the comm link.
     */
    public String content;
    /**
     * Release date of the comm link
     */
    public long date;
    /**
     * Short content description of the comm link
     */
    public String description;
    /**
     * Associated tags for the comm links
     */
    public ArrayList<String> tags;
    /**
     * Url for the backdrop image.
     */
    public String backdropUrl;

    public CommLinkModel() {
    }

    protected CommLinkModel(Parcel in) {
        sourceUri = (Uri) in.readValue(Uri.class.getClassLoader());
        title = in.readString();
        content = in.readString();
        date = in.readLong();
        description = in.readString();
        if (in.readByte() == 0x01) {
            tags = new ArrayList<>();
            in.readList(tags, String.class.getClassLoader());
        } else {
            tags = null;
        }
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
        dest.writeValue(sourceUri);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(date);
        dest.writeString(description);
        if (tags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tags);
        }
        dest.writeInt(layout);
        dest.writeInt(spanCount);
        dest.writeString(backdropUrl);
    }
}
