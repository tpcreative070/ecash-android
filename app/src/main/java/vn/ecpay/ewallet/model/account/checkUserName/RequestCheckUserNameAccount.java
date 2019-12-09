
package vn.ecpay.ewallet.model.account.checkUserName;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestCheckUserNameAccount extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("auditNumber")
    private String mAuditNumber;
    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String mAuditNumber) {
        this.mAuditNumber = mAuditNumber;
    }

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

}
