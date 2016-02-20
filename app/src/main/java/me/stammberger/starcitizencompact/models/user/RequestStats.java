package me.stammberger.starcitizencompact.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestStats {

    @SerializedName("request_ip")
    @Expose
    public String requestIp;
    @SerializedName("timestamp")
    @Expose
    public Double timestamp;
    @SerializedName("items_returned")
    @Expose
    public Integer itemsReturned;
    @SerializedName("query_status")
    @Expose
    public String queryStatus;
    @SerializedName("input_query")
    @Expose
    public InputQuery inputQuery;
    @SerializedName("resolved_query")
    @Expose
    public ResolvedQuery resolvedQuery;
    @SerializedName("performance")
    @Expose
    public Performance performance;

}
