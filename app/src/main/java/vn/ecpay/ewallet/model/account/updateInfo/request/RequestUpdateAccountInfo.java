
package vn.ecpay.ewallet.model.account.updateInfo.request;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestUpdateAccountInfo extends BaseObject {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("personCurrentAddress")
    private String mPersonCurrentAddress;
    @SerializedName("personEmail")
    private String mPersonEmail;
    @SerializedName("personFirstName")
    private String mPersonFirstName;
    @SerializedName("personLastName")
    private String mPersonLastName;
    @SerializedName("personMiddleName")
    private String mPersonMiddleName;
    @SerializedName("personMobilePhone")
    private String mPersonMobilePhone;
    @SerializedName("sessionId")
    private String mSessionId;
    @SerializedName("token")
    private String mToken;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("auditNumber")
    private String mAuditNumber;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public String getFunctionCode() {
        return mFunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        mFunctionCode = functionCode;
    }

    public String getIdNumber() {
        return mIdNumber;
    }

    public void setIdNumber(String idNumber) {
        mIdNumber = idNumber;
    }

    public String getPersonCurrentAddress() {
        return mPersonCurrentAddress;
    }

    public void setPersonCurrentAddress(String personCurrentAddress) {
        mPersonCurrentAddress = personCurrentAddress;
    }

    public String getPersonEmail() {
        return mPersonEmail;
    }

    public void setPersonEmail(String personEmail) {
        mPersonEmail = personEmail;
    }

    public String getPersonFirstName() {
        return mPersonFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        mPersonFirstName = personFirstName;
    }

    public String getPersonLastName() {
        return mPersonLastName;
    }

    public void setPersonLastName(String personLastName) {
        mPersonLastName = personLastName;
    }

    public String getPersonMiddleName() {
        return mPersonMiddleName;
    }

    public void setPersonMiddleName(String personMiddleName) {
        mPersonMiddleName = personMiddleName;
    }

    public String getPersonMobilePhone() {
        return mPersonMobilePhone;
    }

    public void setPersonMobilePhone(String personMobilePhone) {
        mPersonMobilePhone = personMobilePhone;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public void setSessionId(String sessionId) {
        mSessionId = sessionId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String mChannelSignature) {
        this.mChannelSignature = mChannelSignature;
    }

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String mAuditNumber) {
        this.mAuditNumber = mAuditNumber;
    }
}
