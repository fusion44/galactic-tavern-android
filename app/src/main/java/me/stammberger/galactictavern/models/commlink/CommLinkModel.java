
package me.stammberger.galactictavern.models.commlink;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.galactictavern.models.common.BaseModel;
import me.stammberger.galactictavern.stores.db.tables.commlink.CommLinkModelTable;

/**
 * This class represents a logical content group in a comm link.
 * Each comm link might consist of multiple wrappers which represent "subgroups"
 * Each wrapper is divided again into {@link ContentBlock1}, {@link ContentBlock2}
 * and {@link ContentBlock4}. For some reason ContentBlock3 is never used on the RSI site.
 */
@StorIOSQLiteType(table = CommLinkModelTable.TABLE)
public class CommLinkModel extends BaseModel {

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_ID, key = true)
    public Long id;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_SOURCE_URI)
    @SerializedName("source_url")
    @Expose
    public String sourceUrl;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_COMM_LINK_ID)
    @SerializedName("comm_link_id")
    @Expose
    public Long commLinkId;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_TYPE)
    @SerializedName("type")
    @Expose
    public String type;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_PUBLISHED_DATE)
    @SerializedName("published")
    @Expose
    public Long published;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_TITLE)
    @SerializedName("title")
    @Expose
    public String title;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_BACKDROP_URL)
    @SerializedName("main_backdrop")
    @Expose
    public String mainBackdrop;

    @StorIOSQLiteColumn(name = CommLinkModelTable.COLUMN_SUMMARY)
    @SerializedName("summary")
    @Expose
    public String summary;

    @SerializedName("wrappers")
    @Expose
    public List<Wrapper> wrappers = new ArrayList<>();

    /**
     * Depicts whether the user has favorited this comm link or not
     */
    public boolean favorite = false;

    /**
     * No args constructor for use in serialization
     */
    public CommLinkModel() {
    }

    /**
     * @param commLinkId SQLite commLinkId
     * @param summary    Summary
     * @param sourceUrl  Unique Url to RSI site
     * @param title      Content title
     * @param wrappers   All the content wrappers
     * @param published  Publishing date as a Unix timestamp
     * @param type       type
     */
    public CommLinkModel(String type, Long published, String title, String sourceUrl,
                         String summary, List<Wrapper> wrappers, Long commLinkId) {
        this.type = type;
        this.published = published;
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.summary = summary;
        this.wrappers = wrappers;
        this.commLinkId = commLinkId;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The published
     */
    public Long getPublished() {
        return published;
    }

    /**
     * @param published The published
     */
    public void setPublished(Long published) {
        this.published = published;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The comm link source url
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * @param sourceUrl The comm link source url
     */
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    /**
     * @return The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return The wrappers
     */
    public List<Wrapper> getWrappers() {
        return wrappers;
    }

    /**
     * @param wrappers The wrappers
     */
    public void setWrappers(List<Wrapper> wrappers) {
        this.wrappers = wrappers;
    }

    /**
     * @return The backdrop url
     */
    public String getMainBackdrop() {
        return mainBackdrop;
    }

    /**
     * @param mainBackdrop The main back drop url
     */
    public void setMainBackdrop(String mainBackdrop) {
        this.mainBackdrop = mainBackdrop;
    }

    /**
     * @return The comm link id
     */
    public Long getCommLinkId() {
        return commLinkId;
    }

    /**
     * @param id The comm link id
     */
    public void setCommLinkId(Long id) {
        this.commLinkId = id;
    }
}
