package me.stammberger.starcitizencompact.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizencompact.models.common.RequestStats;

public class ForumThreadPosts {

    @SerializedName("data")
    @Expose
    public List<ForumThreadPost> data = new ArrayList<>();
    @SerializedName("request_stats")
    @Expose
    public RequestStats requestStats;

}
