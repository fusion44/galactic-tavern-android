package space.galactictavern.app.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import space.galactictavern.app.models.common.RequestStats;

public class User {

    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("request_stats")
    @Expose
    public RequestStats requestStats;

}
