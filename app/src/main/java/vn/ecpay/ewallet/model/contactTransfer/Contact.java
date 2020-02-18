package vn.ecpay.ewallet.model.contactTransfer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contact implements Serializable, Parcelable {
    @SerializedName("customerId")
    private Long customerId;
    @SerializedName("ecPublicKey")
    private String publicKeyValue;
    @SerializedName("personMobilePhone")
    private String phone;
    @SerializedName("terminalInfo")
    private String terminalInfo;
    @SerializedName("walletId")
    private Long walletId;
    private String fullName;
    private int status;
    public boolean isSection;
    public boolean isAddTransfer;
    public Contact() {
    }

    public Contact(boolean isSection, String fullName) {
        this.isSection = isSection;
        this.fullName = fullName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
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

    public boolean isAddTransfer() {
        return isAddTransfer;
    }

    public void setAddTransfer(boolean addTransfer) {
        isAddTransfer = addTransfer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.customerId);
        dest.writeString(this.publicKeyValue);
        dest.writeString(this.phone);
        dest.writeString(this.terminalInfo);
        dest.writeValue(this.walletId);
        dest.writeString(this.fullName);
        dest.writeInt(this.status);
        dest.writeByte(this.isSection ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAddTransfer ? (byte) 1 : (byte) 0);
    }

    protected Contact(Parcel in) {
        this.customerId = (Long) in.readValue(Long.class.getClassLoader());
        this.publicKeyValue = in.readString();
        this.phone = in.readString();
        this.terminalInfo = in.readString();
        this.walletId = (Long) in.readValue(Long.class.getClassLoader());
        this.fullName = in.readString();
        this.status = in.readInt();
        this.isSection = in.readByte() != 0;
        this.isAddTransfer = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}