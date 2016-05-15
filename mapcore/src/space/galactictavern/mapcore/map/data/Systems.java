package space.galactictavern.mapcore.map.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Systems {

    @SerializedName("resultset")
    @Expose
    public List<SystemsResultset> resultset = new ArrayList<SystemsResultset>();
    @SerializedName("rowcount")
    @Expose
    public Integer rowcount;
    @SerializedName("startrow")
    @Expose
    public Integer startrow;
    @SerializedName("offset")
    @Expose
    public Integer offset;
    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("totalrows")
    @Expose
    public String totalrows;
    @SerializedName("pagecount")
    @Expose
    public Object pagecount;
    @SerializedName("pagesize")
    @Expose
    public Integer pagesize;
    @SerializedName("estimatedrows")
    @Expose
    public Boolean estimatedrows;

}
