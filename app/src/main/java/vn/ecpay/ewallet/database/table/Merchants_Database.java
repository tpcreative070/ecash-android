package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MERCHANTS")
public class Merchants_Database {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "merchantCode")
    private String merchantCode;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @ColumnInfo(name = "merchantName")
    private String merchantName;

    @ColumnInfo(name = "merchantAddress")
    private String merchantAddress;

    @ColumnInfo(name = "publicKeyValue")
    private String publicKeyValue;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "created")
    private String created;

    @ColumnInfo(name = "modified")
    private String modified;

    @ColumnInfo(name = "destroyed")
    private String destroyed;

    @NonNull
    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(@NonNull String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getPublicKeyValue() {
        return publicKeyValue;
    }

    public void setPublicKeyValue(String publicKeyValue) {
        this.publicKeyValue = publicKeyValue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getDestroyed() {
        return destroyed;
    }

    public void setDestroyed(String destroyed) {
        this.destroyed = destroyed;
    }
}
