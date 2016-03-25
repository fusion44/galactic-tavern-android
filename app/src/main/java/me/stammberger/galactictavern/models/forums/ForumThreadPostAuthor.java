package me.stammberger.galactictavern.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForumThreadPostAuthor {

    @SerializedName("handle")
    @Expose
    public String handle;
    @SerializedName("citizen_number")
    @Expose
    public String citizenNumber;
    @SerializedName("status")
    @Expose
    public Object status;
    @SerializedName("moniker")
    @Expose
    public String moniker;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("enlisted")
    @Expose
    public Object enlisted;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("title_image")
    @Expose
    public String titleImage;
    @SerializedName("bio")
    @Expose
    public Object bio;
    @SerializedName("website_link")
    @Expose
    public Object websiteLink;
    @SerializedName("website_title")
    @Expose
    public Object websiteTitle;
    @SerializedName("country")
    @Expose
    public Object country;
    @SerializedName("region")
    @Expose
    public Object region;
    @SerializedName("fluency")
    @Expose
    public Object fluency;
    @SerializedName("discussion_count")
    @Expose
    public Object discussionCount;
    @SerializedName("post_count")
    @Expose
    public String postCount;
    @SerializedName("last_forum_visit")
    @Expose
    public Object lastForumVisit;
    @SerializedName("forum_roles")
    @Expose
    public Object forumRoles;
    @SerializedName("organizations")
    @Expose
    public Object organizations;
    @SerializedName("date_added")
    @Expose
    public Object dateAdded;
    @SerializedName("last_scrape_date")
    @Expose
    public Object lastScrapeDate;

}
