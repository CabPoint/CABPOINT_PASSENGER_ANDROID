
package cabpoint.cabigate.apps.com.cabpoint.models.loginoutput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("companyid")
    @Expose
    private String companyid;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("token")
    @Expose
    private String token;

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
