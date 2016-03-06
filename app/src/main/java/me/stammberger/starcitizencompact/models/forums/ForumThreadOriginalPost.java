package me.stammberger.starcitizencompact.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForumThreadOriginalPost {

    @SerializedName("post_time")
    @Expose
    public Integer postTime;
    @SerializedName("last_edit_time")
    @Expose
    public Integer lastEditTime;
    @SerializedName("post_text")
    @Expose
    public String postText;
    @SerializedName("signature")
    @Expose
    public String signature;
    @SerializedName("post_id")
    @Expose
    public Integer postId;
    @SerializedName("thread_id")
    @Expose
    public Integer threadId;
    @SerializedName("thread_title")
    @Expose
    public String threadTitle;
    @SerializedName("permalink")
    @Expose
    public String permalink;

}
