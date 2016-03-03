package me.stammberger.starcitizencompact.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.stammberger.starcitizencompact.models.common.RequestStats;

public class User {

    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("request_stats")
    @Expose
    public RequestStats requestStats;

}
