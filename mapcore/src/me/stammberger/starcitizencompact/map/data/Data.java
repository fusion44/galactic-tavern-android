package me.stammberger.starcitizencompact.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Data {

    @SerializedName("species")
    @Expose
    public Species species;
    @SerializedName("affiliations")
    @Expose
    public Affiliations affiliations;
    @SerializedName("tunnels")
    @Expose
    public Tunnels tunnels;
    @SerializedName("config")
    @Expose
    public Config config;
    @SerializedName("systems")
    @Expose
    public Systems systems;
    public HashMap<Integer, SystemsResultset> systemHashMap = new HashMap<Integer, SystemsResultset>();
}
