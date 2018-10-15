
package cabpoint.cabigate.apps.com.cabpoint.models.phonecodeinput;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhoneCodeModel {

    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("keymatch")
    @Expose
    private String keymatch;
    @SerializedName("OPT")
    @Expose
    private String oPT;
    @SerializedName("token")
    @Expose
    private String token;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
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
