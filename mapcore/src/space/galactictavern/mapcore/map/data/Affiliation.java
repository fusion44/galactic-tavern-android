package space.galactictavern.mapcore.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Affiliation {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("color")
    @Expose
    public String color;
    @SerializedName("membership.id")
    @Expose
    public String membershipId;
    @SerializedName("name")
    @Expose
    public String name;

}
