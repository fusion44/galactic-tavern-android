package me.stammberger.galactictavern.models.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StandardResponse {

    @SerializedName("successful")
    @Expose
    public boolean successful;

    @SerializedName("payload")
    @Expose
    public String payload;

    @SerializedName("error")
    @Expose
    public String err;

}
