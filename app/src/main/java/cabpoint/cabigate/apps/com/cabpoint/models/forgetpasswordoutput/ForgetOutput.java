
package cabpoint.cabigate.apps.com.cabpoint.models.forgetpasswordoutput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgetOutput {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("response")
    @Expose
    private Response response;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
