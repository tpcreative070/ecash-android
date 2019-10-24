package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CONTACTS")
public class Contact {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "walletId")
    private Long walletId;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "publicKeyValue")
    private String publicKeyValue;

    @ColumnInfo(name = "fullName")
    private String fullName;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "mobileInfo")
    private String mobileInfo;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "created")
    private String created;

    @ColumnInfo(name = "modified")
    private String modified;

    @ColumnInfo(name = "destroyed")
    private String destroyed;

    @ColumnInfo(name = "isSection")
    private boolean isSection;

    @NonNull
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(@NonNull Long walletId) {
        this.walletId = walletId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPublicKeyValue() {
        return publicKeyValue;
    }

    public void setPublicKeyValue(String publicKeyValue) {
        this.publicKeyValue = publicKeyValue;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileInfo() {
        return mobileInfo;
    }

    public void setMobileInfo(String mobileInfo) {
        this.mobileInfo = mobileInfo;
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
