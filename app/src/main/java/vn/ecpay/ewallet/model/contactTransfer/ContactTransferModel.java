package vn.ecpay.ewallet.model.contactTransfer;

public class ContactTransferModel {
    public boolean isSection;
    private Long walletId;
    private String phone;
    private String fullName;
    private String publicKeyValue;

    public ContactTransferModel(boolean isSection, String fullName) {
        this.isSection = isSection;
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getWalletId() {
        return walletId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPublicKeyValue() {
        return publicKeyValue;
    }

    public void setPublicKeyValue(String publicKeyValue) {
        this.publicKeyValue = publicKeyValue;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public boolean isSection() {
        return isSection;
    }

    public void setSection(boolean section) {
        isSection = section;
    }
}