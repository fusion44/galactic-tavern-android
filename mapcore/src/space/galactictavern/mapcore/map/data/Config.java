package space.galactictavern.mapcore.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Config {

    @SerializedName("stars")
    @Expose
    public Stars stars;
    @SerializedName("starfield")
    @Expose
    public Starfield starfield;
    @SerializedName("longRangeScanner")
    @Expose
    public LongRangeScanner longRangeScanner;
    @SerializedName("farPlane")
    @Expose
    public Integer farPlane;
    @SerializedName("nearPlane")
    @Expose
    public Integer nearPlane;
    @SerializedName("routes")
    @Expose
    public List<Route> routes = new ArrayList<Route>();

}
