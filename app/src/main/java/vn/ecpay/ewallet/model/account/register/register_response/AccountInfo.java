
package vn.ecpay.ewallet.model.account.register.register_response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.BaseObject;

@Generated("net.hexar.json2pojo")
public class AccountInfo extends BaseObject implements Serializable {
    @SerializedName("walletId")
    private Long walletId;

    @SerializedName("lastAccessTime")
    private String lastAccessTime;

    @SerializedName("balance")
    private int balance;

    @SerializedName("channelCode")
    private String channelCode;

    @SerializedName("channelId")
    private Long channelId;

    @SerializedName("channelSignature")
    private String channelSignature;

    @SerializedName("customerId")
    private Long customerId;

    @SerializedName("dateCreated")
    private String dateCreated;

    @SerializedName("functionCode")
    private String functionCode;

    @SerializedName("functionId")
    private Long functionId;

    @SerializedName("groupId")
    private Long groupId;

    @SerializedName("id")
    private Long id;

    @SerializedName("idNumber")
    private String idNumber;

    @SerializedName("keyPublicAlias")
    private String keyPublicAlias;

    @SerializedName("ecKeyPublicValue")
    private String ecKeyPublicValue;

    @SerializedName("masterKey")
    private String masterKey;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("password")
    private String password;

    @SerializedName("token")
    private String token;

    @SerializedName("personFirstName")
    private String personFirstName;

    @SerializedName("personLastName")
    private String personLastName;

    @SerializedName("personMiddleName")
    private String personMiddleName;

    @SerializedName("personMobilePhone")
    private String personMobilePhone;

    @SerializedName("terminalId")
    private String terminalId;

    @SerializedName("terminalInfo")
    private String terminalInfo;

    @SerializedName("userId")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("accountIdt")
    private Long accountIdt;

    @SerializedName("transactionCode")
    private String transactionCode;

    @SerializedName("sessionId")
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(String lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelSignature() {
        return channelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        this.channelSignature = channelSignature;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getKeyPublicAlias() {
        return keyPublicAlias;
    }

    public void setKeyPublicAlias(String keyPublicAlias) {
        this.keyPublicAlias = keyPublicAlias;
    }

    public String getEcKeyPublicValue() {
        return ecKeyPublicValue;
    }

    public void setEcKeyPublicValue(String ecKeyPublicValue) {
        this.ecKeyPublicValue = ecKeyPublicValue;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonFirstName() {
        if (personFirstName == null) {
            return Constant.STR_EMPTY;
        } else {
            return personFirstName;
        }
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    public String getPersonLastName() {
        if (personLastName == null) {
            return Constant.STR_EMPTY;
        } else {
            return personLastName;
        }
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public String getPersonMiddleName() {
        if (personMiddleName == null) {
            return Constant.STR_EMPTY;
        } else {
            return personMiddleName;
        }
    }

    public void setPersonMiddleName(String personMiddleName) {
        this.personMiddleName = personMiddleName;
    }

    public String getPersonMobilePhone() {
        return personMobilePhone;
    }

    public void setPersonMobilePhone(String personMobilePhone) {
        this.personMobilePhone = personMobilePhone;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAccountIdt() {
        return accountIdt;
    }

    public void setAccountIdt(Long accountIdt) {
        this.accountIdt = accountIdt;
    }

    @NonNull
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(@NonNull Long walletId) {
        this.walletId = walletId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}
