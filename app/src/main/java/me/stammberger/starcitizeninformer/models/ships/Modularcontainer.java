package me.stammberger.starcitizeninformer.models.ships;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Generated by: http://www.jsonschema2pojo.org/
 */
public class Modularcontainer {

    @SerializedName("additionals")
    @Expose
    public List<String> additionals = new ArrayList<>();
    @SerializedName("heading")
    @Expose
    public String heading;

}
