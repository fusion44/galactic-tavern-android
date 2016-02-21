package me.stammberger.starcitizencompact.models.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Performance {

    @SerializedName("network_io_time")
    @Expose
    public Integer networkIoTime;
    @SerializedName("processing_time")
    @Expose
    public Double processingTime;
    @SerializedName("total_time")
    @Expose
    public Double totalTime;
    @SerializedName("handler_chain")
    @Expose
    public List<HandlerChain> handlerChain = new ArrayList<>();

}
