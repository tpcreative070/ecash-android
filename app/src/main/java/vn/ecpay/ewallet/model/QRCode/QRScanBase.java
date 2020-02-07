package vn.ecpay.ewallet.model.QRCode;

import com.google.gson.annotations.SerializedName;

public class QRScanBase {
    @SerializedName("content")
    private String mContent;
    @SerializedName("cycle")
    private int mCycle;
    @SerializedName("fullname")
    private String mFullname;
    @SerializedName("personMobiPhone")
    private String mPersonMobiPhone;
    @SerializedName("publicKey")
    private String mPublicKey;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("total")
    private int mTotal;
    @SerializedName("walletId")
    private String mWalletId;
    @SerializedName("type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getCycle() {
        return mCycle;
    }

    public void setCycle(int cycle) {
        mCycle = cycle;
    }

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

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String walletId) {
        mWalletId = walletId;
    }
}


