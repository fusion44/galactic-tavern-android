package space.galactictavern.app.models.forums;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import space.galactictavern.app.models.common.RequestStats;

public class Forums {

    @SerializedName("data")
    @Expose
    public List<Forum> data = new ArrayList<>();
    @SerializedName("request_stats")
    @Expose
    public RequestStats requestStats;

}
