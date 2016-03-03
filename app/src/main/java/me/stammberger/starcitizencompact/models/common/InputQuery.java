package me.stammberger.starcitizencompact.models.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InputQuery {

    @SerializedName("system")
    @Expose
    public String system;
    @SerializedName("action")
    @Expose
    public String action;
    @SerializedName("data_source")
    @Expose
    public Object dataSource;
    @SerializedName("target_id")
    @Expose
    public String targetId;
    @SerializedName("api_source")
    @Expose
    public String apiSource;
    @SerializedName("pagination")
    @Expose
    public Pagination pagination;
    @SerializedName("date_range")
    @Expose
    public DateRange dateRange;
    @SerializedName("expedite")
    @Expose
    public Boolean expedite;

}
