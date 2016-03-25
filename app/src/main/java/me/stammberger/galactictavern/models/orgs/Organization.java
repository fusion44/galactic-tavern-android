package me.stammberger.galactictavern.models.orgs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.stammberger.galactictavern.models.common.RequestStats;

public class Organization {

    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("request_stats")
    @Expose
    public RequestStats requestStats;

}
