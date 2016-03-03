package me.stammberger.starcitizencompact.models.orgs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.stammberger.starcitizencompact.models.common.RequestStats;

public class Organization {

    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("request_stats")
    @Expose
    public RequestStats requestStats;

}
