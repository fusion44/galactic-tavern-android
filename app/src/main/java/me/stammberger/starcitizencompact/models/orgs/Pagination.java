package me.stammberger.starcitizencompact.models.orgs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("start_page")
    @Expose
    public Integer startPage;
    @SerializedName("end_page")
    @Expose
    public Integer endPage;
    @SerializedName("items_per_page")
    @Expose
    public Integer itemsPerPage;
    @SerializedName("sort_method")
    @Expose
    public String sortMethod;
    @SerializedName("sort_direction")
    @Expose
    public String sortDirection;

}
