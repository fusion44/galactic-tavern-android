package me.stammberger.starcitizencompact.map.data;

import com.badlogic.gdx.math.Circle;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class SystemsResultset {

    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("aggregated_danger")
    @Expose
    public Integer aggregatedDanger;
    @SerializedName("affiliation")
    @Expose
    public List<Affiliation> affiliation = new ArrayList<Affiliation>();
    @SerializedName("info_url")
    @Expose
    public String infoUrl;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("aggregated_economy")
    @Expose
    public Double aggregatedEconomy;
    @SerializedName("position_z")
    @Expose
    public float positionZ;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("thumbnail")
    @Expose
    public Thumbnail thumbnail;
    @SerializedName("aggregated_size")
    @Expose
    public String aggregatedSize;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("position_x")
    @Expose
    public float positionX;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("position_y")
    @Expose
    public float positionY;
    @SerializedName("time_modified")
    @Expose
    public String timeModified;
    @SerializedName("aggregated_population")
    @Expose
    public Double aggregatedPopulation;

    public Circle boundingCircle;

    public void generateBoundingCircle() {
        boundingCircle = new Circle();
        boundingCircle.set(positionX, positionY, 110f);
    }

    public boolean contains(float x, float y) {
        return boundingCircle.contains(x, y);
    }
}
