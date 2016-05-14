package me.stammberger.starcitizencompact.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Starfield {

    @SerializedName("color2")
    @Expose
    public String color2;
    @SerializedName("color1")
    @Expose
    public String color1;
    @SerializedName("sizeMin")
    @Expose
    public Integer sizeMin;
    @SerializedName("radius")
    @Expose
    public Integer radius;
    @SerializedName("count")
    @Expose
    public Integer count;
    @SerializedName("sizeMax")
    @Expose
    public Integer sizeMax;

}
