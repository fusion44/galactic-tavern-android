package space.galactictavern.app.models.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DateRange {

    @SerializedName("start_date")
    @Expose
    public Integer startDate;
    @SerializedName("end_date")
    @Expose
    public Integer endDate;

}
