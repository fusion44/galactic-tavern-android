package me.stammberger.starcitizencompact.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class TunnelsResultset {

    @SerializedName("direction")
    @Expose
    public String direction;
    @SerializedName("exit")
    @Expose
    public Exit exit;
    @SerializedName("entry_id")
    @Expose
    public String entryId;
    @SerializedName("name")
    @Expose
    public Object name;
    @SerializedName("entry")
    @Expose
    public Entry entry;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("size")
    @Expose
    public String size;
    @SerializedName("exit_id")
    @Expose
    public String exitId;

}
