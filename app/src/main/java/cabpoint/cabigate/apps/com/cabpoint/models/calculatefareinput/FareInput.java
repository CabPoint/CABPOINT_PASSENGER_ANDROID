
package cabpoint.cabigate.apps.com.cabpoint.models.calculatefareinput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FareInput {

    @SerializedName("company_serial")
    @Expose
    private String companySerial;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("pickup_lat")
    @Expose
    private String pickupLat;
    @SerializedName("pickup_lng")
    @Expose
    private String pickupLng;
    @SerializedName("drop_lat")
    @Expose
    private String dropLat;
    @SerializedName("drop_lng")
    @Expose
    private String dropLng;
    @SerializedName("taxi_model")
    @Expose
    private String taxiModel;

    public String getCompanySerial() {
        return companySerial;
    }

    public void setCompanySerial(String companySerial) {
        this.companySerial = companySerial;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(String pickupLng) {
        this.pickupLng = pickupLng;
    }

    public String getDropLat() {
        return dropLat;
    }

    public void setDropLat(String dropLat) {
        this.dropLat = dropLat;
    }

    public String getDropLng() {
        return dropLng;
    }

    public void setDropLng(String dropLng) {
        this.dropLng = dropLng;
    }

    public String getTaxiModel() {
        return taxiModel;
    }

    public void setTaxiModel(String taxiModel) {
        this.taxiModel = taxiModel;
    }

}
