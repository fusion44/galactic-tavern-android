package space.galactictavern.app.models.orgs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("sid")
    @Expose
    public String sid;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("logo")
    @Expose
    public String logo;
    @SerializedName("member_count")
    @Expose
    public Integer memberCount;
    @SerializedName("recruiting")
    @Expose
    public String recruiting;
    @SerializedName("archetype")
    @Expose
    public String archetype;
    @SerializedName("commitment")
    @Expose
    public String commitment;
    @SerializedName("roleplay")
    @Expose
    public String roleplay;
    @SerializedName("lang")
    @Expose
    public String lang;
    @SerializedName("primary_focus")
    @Expose
    public String primaryFocus;
    @SerializedName("primary_image")
    @Expose
    public String primaryImage;
    @SerializedName("secondary_focus")
    @Expose
    public String secondaryFocus;
    @SerializedName("secondary_image")
    @Expose
    public String secondaryImage;
    @SerializedName("banner")
    @Expose
    public String banner;
    @SerializedName("headline")
    @Expose
    public String headline;
    @SerializedName("history")
    @Expose
    public String history;
    @SerializedName("manifesto")
    @Expose
    public String manifesto;
    @SerializedName("charter")
    @Expose
    public String charter;
    @SerializedName("cover_image")
    @Expose
    public String coverImage;
    @SerializedName("cover_video")
    @Expose
    public String coverVideo;
    @SerializedName("date_added")
    @Expose
    public Double dateAdded;
    @SerializedName("last_scrape_date")
    @Expose
    public Double lastScrapeDate;

}
