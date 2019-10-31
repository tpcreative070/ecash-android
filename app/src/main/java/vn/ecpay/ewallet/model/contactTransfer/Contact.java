package vn.ecpay.ewallet.model.contactTransfer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contact implements Serializable {
    @SerializedName("customerId")
    private Long customerId;
    @SerializedName("publicKeyValue")
    private String publicKeyValue;
    @SerializedName("phone")
    private String phone;
    @SerializedName("terminalInfo")
    private String terminalInfo;
    @SerializedName("walletId")
    private Long walletId;
    private String fullName;
    public boolean isSection;

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

    public String getPublicKeyValue() {
        return publicKeyValue;
    }

    public void setPublicKeyValue(String publicKeyValue) {
        this.publicKeyValue = publicKeyValue;
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
}