package me.stammberger.galactictavern.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForumThread {

    @SerializedName("thread_title")
    @Expose
    public String threadTitle;
    @SerializedName("thread_id")
    @Expose
    public Long threadId;
    @SerializedName("thread_replies")
    @Expose
    public Integer threadReplies;
    @SerializedName("thread_views")
    @Expose
    public String threadViews;
    @SerializedName("original_poster")
    @Expose
    public ForumThreadOriginalPoster originalPoster;
    @SerializedName("original_post")
    @Expose
    public ForumThreadOriginalPost originalPost;
    @SerializedName("recent_poster")
    @Expose
    public ForumThreadRecentPoster recentPoster;
    @SerializedName("recent_post")
    @Expose
    public ForumThreadRecentPost recentPost;

}
