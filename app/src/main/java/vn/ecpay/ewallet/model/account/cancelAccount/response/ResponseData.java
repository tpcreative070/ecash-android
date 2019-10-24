
package vn.ecpay.ewallet.model.account.cancelAccount.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

    @SerializedName("walletId")
    private Long mWalletId;

    public Long getWalletId() {
        return mWalletId;
    }

    public void setWalletId(Long walletId) {
        mWalletId = walletId;
    }

}
