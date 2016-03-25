package me.stammberger.galactictavern.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("handle")
    @Expose
    public String handle;
    @SerializedName("citizen_number")
    @Expose
    public String citizenNumber;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("moniker")
    @Expose
    public String moniker;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("enlisted")
    @Expose
    public Long enlisted;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("title_image")
    @Expose
    public String titleImage;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("website_link")
    @Expose
    public String websiteLink;
    @SerializedName("website_title")
    @Expose
    public String websiteTitle;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("region")
    @Expose
    public String region;
    @SerializedName("fluency")
    @Expose
    public List<String> fluency = new ArrayList<>();
    @SerializedName("discussion_count")
    @Expose
    public Integer discussionCount;
    @SerializedName("post_count")
    @Expose
    public Integer postCount;
    @SerializedName("last_forum_visit")
    @Expose
    public Long lastForumVisit;
    @SerializedName("forum_roles")
    @Expose
    public List<String> forumRoles = new ArrayList<>();
    @SerializedName("organizations")
    @Expose
    public List<UserOrganizationObject> userOrganizationObjects = new ArrayList<>();
    @SerializedName("date_added")
    @Expose
    public Long dateAdded;
    @SerializedName("last_scrape_date")
    @Expose
    public Long lastScrapeDate;
}
