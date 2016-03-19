package me.stammberger.starcitizencompact.models.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("children")
    @Expose
    public Object children;

}