package me.stammberger.galactictavern.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForumThreadRecentPoster {

    @SerializedName("citizen_number")
    @Expose
    public Integer citizenNumber;
    @SerializedName("handle")
    @Expose
    public String handle;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("post_time")
    @Expose
    public Integer postTime;

}
