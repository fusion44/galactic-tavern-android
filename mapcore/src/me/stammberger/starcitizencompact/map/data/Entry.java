package me.stammberger.starcitizencompact.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Entry {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("longitude")
    @Expose
    public String longitude;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("distance")
    @Expose
    public float distance;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("designation")
    @Expose
    public String designation;
    @SerializedName("star_system_id")
    @Expose
    public int starSystemId;
    @SerializedName("name")
    @Expose
    public Object name;

}
