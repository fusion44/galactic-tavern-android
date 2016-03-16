package me.stammberger.starcitizencompact.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForumThreadPostData {

    @SerializedName("post_time")
    @Expose
    public Long postTime;
    @SerializedName("last_edit_time")
    @Expose
    public Boolean lastEditTime;
    @SerializedName("post_text")
    @Expose
    public String postText;
    @SerializedName("signature")
    @Expose
    public String signature;
    @SerializedName("post_id")
    @Expose
    public Long postId;
    @SerializedName("thread_id")
    @Expose
    public Object threadId;
    @SerializedName("thread_title")
    @Expose
    public Object threadTitle;
    @SerializedName("permalink")
    @Expose
    public String permalink;

}
