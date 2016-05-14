package me.stammberger.starcitizencompact.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Thumbnail {

    @SerializedName("images")
    @Expose
    public ArrayList<Images> images;
    @SerializedName("slug")
    @Expose
    public String slug;
    @SerializedName("source")
    @Expose
    public String source;

}
