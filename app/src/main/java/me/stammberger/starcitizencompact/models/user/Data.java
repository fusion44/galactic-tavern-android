package me.stammberger.starcitizencompact.models.user;

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
    public String enlisted;
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
    public String region;
    @SerializedName("fluency")
    @Expose
    public List<String> fluency = new ArrayList<>();
    @SerializedName("discussion_count")
    @Expose
    public Object discussionCount;
    @SerializedName("post_count")
    @Expose
    public Object postCount;
    @SerializedName("last_forum_visit")
    @Expose
    public String lastForumVisit;
    @SerializedName("forum_roles")
    @Expose
    public List<String> forumRoles = new ArrayList<>();
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
