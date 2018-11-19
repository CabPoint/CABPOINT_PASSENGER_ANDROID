
package cabpoint.cabigate.apps.com.cabpoint.models.LogoutInput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutInput {

    @SerializedName("company_serial")
    @Expose
    private String companySerial;
    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("token")
    @Expose
    private String token;

    public String getCompanySerial() {
        return companySerial;
    }

    public void setCompanySerial(String companySerial) {
        this.companySerial = companySerial;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
