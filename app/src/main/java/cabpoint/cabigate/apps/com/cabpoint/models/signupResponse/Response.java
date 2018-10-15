
package cabpoint.cabigate.apps.com.cabpoint.models.signupResponse;

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
    @SerializedName("keymatch")
    @Expose
    private String keymatch;
    @SerializedName("OPT")
    @Expose
    private String oPT;
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

    public String getKeymatch() {
        return keymatch;
    }

    public void setKeymatch(String keymatch) {
        this.keymatch = keymatch;
    }

    public String getOPT() {
        return oPT;
    }

    public void setOPT(String oPT) {
        this.oPT = oPT;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
