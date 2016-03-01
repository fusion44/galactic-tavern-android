package me.stammberger.starcitizencompact.models.orgs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HandlerChain {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("children")
    @Expose
    public Object children;

}
