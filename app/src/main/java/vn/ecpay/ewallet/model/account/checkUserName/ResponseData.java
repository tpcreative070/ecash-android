
package vn.ecpay.ewallet.model.account.checkUserName;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelId")
    private Long mChannelId;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("customerId")
    private Object mCustomerId;
    @SerializedName("exist")
    private String mExist;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("functionId")
    private Long mFunctionId;
    @SerializedName("id")
    private Long mId;
    @SerializedName("userId")
    private String mUserId;
    @SerializedName("username")
    private String mUsername;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public Long getChannelId() {
        return mChannelId;
    }

    public void setChannelId(Long channelId) {
        mChannelId = channelId;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public Object getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(Object customerId) {
        mCustomerId = customerId;
    }

    public String getExist() {
        return mExist;
    }

    public void setExist(String exist) {
        mExist = exist;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public Long getFunctionId() {
        return mFunctionId;
    }

    public void setFunctionId(Long functionId) {
        mFunctionId = functionId;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

}
