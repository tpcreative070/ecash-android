
package vn.ecpay.ewallet.model.account.register;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestRegister extends BaseObject {
    @SerializedName("appName")
    private String mAppName;
    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("ecKeyPublicValue")
    private String mEcKeyPublicValue;
    @SerializedName("firebaseToken")
    private String mFirebaseToken;
    @SerializedName("functionCode")
    private String mFunctionCode;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("keyPublicAlias")
    private String mKeyPublicAlias;
    @SerializedName("nickname")
    private String mNickname;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("personFirstName")
    private String mPersonFirstName;
    @SerializedName("personLastName")
    private String mPersonLastName;
    @SerializedName("personMiddleName")
    private String mPersonMiddleName;
    @SerializedName("personMobilePhone")
    private String mPersonMobilePhone;
    @SerializedName("terminalId")
    private String mTerminalId;
    @SerializedName("terminalInfo")
    private String mTerminalInfo;
    @SerializedName("username")
    private String mUsername;
    @SerializedName("auditNumber")
    private String mAuditNumber;

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
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

    public String getEcKeyPublicValue() {
        return mEcKeyPublicValue;
    }

    public void setEcKeyPublicValue(String ecKeyPublicValue) {
        mEcKeyPublicValue = ecKeyPublicValue;
    }

    public String getFirebaseToken() {
        return mFirebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
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

    public String getKeyPublicAlias() {
        return mKeyPublicAlias;
    }

    public void setKeyPublicAlias(String keyPublicAlias) {
        mKeyPublicAlias = keyPublicAlias;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
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

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getAuditNumber() {
        return mAuditNumber;
    }

    public void setAuditNumber(String mAuditNumber) {
        this.mAuditNumber = mAuditNumber;
    }
}
