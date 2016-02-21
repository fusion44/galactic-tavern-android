package me.stammberger.starcitizencompact.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserOrganizationObject {

    @SerializedName("sid")
    @Expose
    public String sid;
    @SerializedName("rank")
    @Expose
    public String rank;
    @SerializedName("stars")
    @Expose
    public String stars;
    @SerializedName("roles")
    @Expose
    public String roles;

}
