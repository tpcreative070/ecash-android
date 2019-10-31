package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import vn.ecpay.ewallet.model.BaseObject;

@Entity(tableName = "PROFILE")
public class Profile_Database extends BaseObject {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "walletId")
    private Long walletId;

    @ColumnInfo(name = "lastAccessTime")
    private String lastAccessTime;

    @ColumnInfo(name = "balance")
    private int balance;

    @ColumnInfo(name = "channelCode")
    private String channelCode;

    @ColumnInfo(name = "channelId")
    private Long channelId;

    @ColumnInfo(name = "channelSignature")
    private String channelSignature;

    @ColumnInfo(name = "customerId")
    private Long customerId;

    @ColumnInfo(name = "dateCreated")
    private String dateCreated;

    @ColumnInfo(name = "functionCode")
    private String functionCode;

    @ColumnInfo(name = "functionId")
    private Long functionId;

    @ColumnInfo(name = "groupId")
    private Long groupId;

    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "idNumber")
    private String idNumber;

    @ColumnInfo(name = "keyPublicAlias")
    private String keyPublicAlias;

    @ColumnInfo(name = "ecKeyPublicValue")
    private String ecKeyPublicValue;

    @ColumnInfo(name = "nickname")
    private String nickname;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "personFirstName")
    private String personFirstName;

    @ColumnInfo(name = "personLastName")
    private String personLastName;

    @ColumnInfo(name = "personMiddleName")
    private String personMiddleName;

    @ColumnInfo(name = "personMobilePhone")
    private String personMobilePhone;

    @ColumnInfo(name = "terminalId")
    private String terminalId;

    @ColumnInfo(name = "terminalInfo")
    private String terminalInfo;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "auditNumber")
    private Long auditNumber;

    @ColumnInfo(name = "accountIdt")
    private Long accountIdt;

    @ColumnInfo(name = "sessionId")
    private String sessionId;

    @NonNull
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(@NonNull Long walletId) {
        this.walletId = walletId;
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
        return personFirstName;
    }

    public void setPersonFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    public String getPersonLastName() {
        return personLastName;
    }

    public void setPersonLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    public String getPersonMiddleName() {
        return personMiddleName;
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

    public Long getAuditNumber() {
        return auditNumber;
    }

    public void setAuditNumber(Long auditNumber) {
        this.auditNumber = auditNumber;
    }

    public Long getAccountIdt() {
        return accountIdt;
    }

    public void setAccountIdt(Long accountIdt) {
        this.accountIdt = accountIdt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
