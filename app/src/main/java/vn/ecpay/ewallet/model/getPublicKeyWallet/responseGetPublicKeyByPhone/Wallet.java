
package vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Wallet {

    @SerializedName("customerId")
    private Long mCustomerId;
    @SerializedName("ecPublicKey")
    private String mEcPublicKey;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("walletId")
    private Long mWalletId;

    public Long getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(Long customerId) {
        mCustomerId = customerId;
    }

    public String getEcPublicKey() {
        return mEcPublicKey;
    }

    public void setEcPublicKey(String ecPublicKey) {
        mEcPublicKey = ecPublicKey;
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
