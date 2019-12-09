
package vn.ecpay.ewallet.webSocket.genenSignature;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class WalletSignature extends BaseObject {

    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("mWalletId")
    private String mWalletId;

    public String getTerminalId() {
        return mTerminalId;
    }

    public void setTerminalId(String terminalId) {
        mTerminalId = terminalId;
    }

    public String getTerminalInfo() {
        return mTerminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        mTerminalInfo = terminalInfo;
    }

    public String getWalletId() {
        return mWalletId;
    }

    public void setWalletId(String mWalletId) {
        this.mWalletId = mWalletId;
    }
}
