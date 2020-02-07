
package vn.ecpay.ewallet.model.contact;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class QRContact {
    @SerializedName("fullname")
    private String mFullname;
    @SerializedName("personMobiPhone")
    private String mPersonMobiPhone;
    @SerializedName("publicKey")
    private String mPublicKey;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("walletId")
    private Long mWalletId;

    public String getFullname() {
        return mFullname;
    }

    public void setFullname(String fullname) {
        mFullname = fullname;
    }

    public String getPersonMobiPhone() {
        return mPersonMobiPhone;
    }

    public void setPersonMobiPhone(String personMobiPhone) {
        mPersonMobiPhone = personMobiPhone;
    }

    public String getPublicKey() {
        return mPublicKey;
    }

    public void setPublicKey(String publicKey) {
        mPublicKey = publicKey;
    }

    public String getTerminalInfo() {
        return mTerminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        mTerminalInfo = terminalInfo;
    }

    public Long getWalletId() {
        return mWalletId;
    }

    public void setWalletId(Long walletId) {
        mWalletId = walletId;
    }

}
