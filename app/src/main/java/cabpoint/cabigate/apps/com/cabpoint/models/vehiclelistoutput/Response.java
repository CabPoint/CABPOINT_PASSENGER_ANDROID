
package cabpoint.cabigate.apps.com.cabpoint.models.vehiclelistoutput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("list")
    @Expose
    private java.util.List<cabpoint.cabigate.apps.com.cabpoint.models.vehiclelistoutput.List> list = null;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public java.util.List<cabpoint.cabigate.apps.com.cabpoint.models.vehiclelistoutput.List> getList() {
        return list;
    }

    public void setList(java.util.List<cabpoint.cabigate.apps.com.cabpoint.models.vehiclelistoutput.List> list) {
        this.list = list;
    }

}
