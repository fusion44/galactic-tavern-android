package me.stammberger.starcitizencompact.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class StarMapData {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("msg")
    @Expose
    public String msg;
    @SerializedName("success")
    @Expose
    public Integer success;
    @SerializedName("data")
    @Expose
    public Data data;

}
