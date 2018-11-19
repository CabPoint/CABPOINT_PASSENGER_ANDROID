
package cabpoint.cabigate.apps.com.cabpoint.models.calculatefareoutput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("total_fare")
    @Expose
    private String totalFare;
    @SerializedName("total_duration")
    @Expose
    private String totalDuration;
    @SerializedName("max_passenger")
    @Expose
    private String maxPassenger;
    @SerializedName("max_luggage")
    @Expose
    private String maxLuggage;
    @SerializedName("availability_status")
    @Expose
    private String availabilityStatus;
    @SerializedName("availability_msg")
    @Expose
    private String availabilityMsg;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getMaxPassenger() {
        return maxPassenger;
    }

    public void setMaxPassenger(String maxPassenger) {
        this.maxPassenger = maxPassenger;
    }

    public String getMaxLuggage() {
        return maxLuggage;
    }

    public void setMaxLuggage(String maxLuggage) {
        this.maxLuggage = maxLuggage;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getAvailabilityMsg() {
        return availabilityMsg;
    }

    public void setAvailabilityMsg(String availabilityMsg) {
        this.availabilityMsg = availabilityMsg;
    }

}
