
package vn.ecpay.ewallet.model.account.active;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class AccountActive {

    @SerializedName("accountIdt")
    private String mAccountIdt;
    @SerializedName("approveStatus")
    private String mApproveStatus;
    @SerializedName("auditNumber")
    private String mAuditNumber;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelId")
    private Long mChannelId;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("customerId")
    private String mCustomerId;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("functionId")
    private Long mFunctionId;
    @SerializedName("id")
    private String mId;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("status")
    private String mStatus;
    @SerializedName("userId")
    private String mUserId;

    public String getAccountIdt() {
        return mAccountIdt;
    }

    public void setAccountIdt(String accountIdt) {
        mAccountIdt = accountIdt;
    }

    public String getApproveStatus() {
        return mApproveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        mApproveStatus = approveStatus;
    }

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String auditNumber) {
        mAuditNumber = auditNumber;
    }

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

    public String getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(String customerId) {
        mCustomerId = customerId;
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

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIdNumber() {
        return mIdNumber;
    }

    public void setIdNumber(String idNumber) {
        mIdNumber = idNumber;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

}
