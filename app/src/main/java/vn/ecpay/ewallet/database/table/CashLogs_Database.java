package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CASH_LOGS")
public class CashLogs_Database {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "userName")
    private String userName;

    @NonNull
    @ColumnInfo(name = "countryCode")
    private String countryCode;

    @NonNull
    private String issuerCode;

    @NonNull
    @ColumnInfo(name = "decisionNo")
    private String decisionNo;

    @NonNull
    @ColumnInfo(name = "serialNo")
    private String serialNo;

    @NonNull
    @ColumnInfo(name = "parValue")
    private int parValue;

    @NonNull
    @ColumnInfo(name = "activeDate")
    private String activeDate;

    @NonNull
    @ColumnInfo(name = "expireDate")
    private String expireDate;

    @NonNull
    @ColumnInfo(name = "cycle")
    private int cycle;

    @NonNull
    @ColumnInfo(name = "treSign")
    private String treSign;

    @NonNull
    @ColumnInfo(name = "accSign")
    private String accSign;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    @NonNull
    @ColumnInfo(name = "transactionSignature")
    private String transactionSignature;

    @NonNull
    @ColumnInfo(name = "previousHash")
    private String previousHash;

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    @NonNull
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@NonNull String countryCode) {
        this.countryCode = countryCode;
    }

    @NonNull
    public String getIssuerCode() {
        return issuerCode;
    }

    public void setIssuerCode(@NonNull String issuerCode) {
        this.issuerCode = issuerCode;
    }

    @NonNull
    public String getDecisionNo() {
        return decisionNo;
    }

    public void setDecisionNo(@NonNull String decisionNo) {
        this.decisionNo = decisionNo;
    }

    @NonNull
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(@NonNull String serialNo) {
        this.serialNo = serialNo;
    }

    @NonNull
    public int getParValue() {
        return parValue;
    }

    public void setParValue(@NonNull int parValue) {
        this.parValue = parValue;
    }

    @NonNull
    public String getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(@NonNull String activeDate) {
        this.activeDate = activeDate;
    }

    @NonNull
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(@NonNull String expireDate) {
        this.expireDate = expireDate;
    }

    @NonNull
    public String getTreSign() {
        return treSign;
    }

    public void setTreSign(@NonNull String treSign) {
        this.treSign = treSign;
    }

    @NonNull
    public String getAccSign() {
        return accSign;
    }

    public void setAccSign(@NonNull String accSign) {
        this.accSign = accSign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(@NonNull String transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    @NonNull
    public int getCycle() {
        return cycle;
    }

    public void setCycle(@NonNull int cycle) {
        this.cycle = cycle;
    }

    @NonNull
    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(@NonNull String previousHash) {
        this.previousHash = previousHash;
    }
}
