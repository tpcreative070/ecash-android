
package vn.ecpay.ewallet.model.cash.getPublicKeyWallet;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseDataGetPublicKeyWallet {

    @SerializedName("customerId")
    private String mCustomerId;
    @SerializedName("ecKpAlias")
    private String mEcKpAlias;
    @SerializedName("ecKpValue")
    private String mEcKpValue;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("idType")
    private String mIdType;
    @SerializedName("personFirstName")
    private String mPersonFirstName;
    @SerializedName("personLastName")
    private String mPersonLastName;
    @SerializedName("personMiddleName")
    private String mPersonMiddleName;
    @SerializedName("personMobilePhone")
    private String mPersonMobilePhone;

    private Long mWalletId;

    public Long getmWalletId() {
        return mWalletId;
    }

    public void setmWalletId(Long mWalletId) {
        this.mWalletId = mWalletId;
    }

    public String getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(String customerId) {
        mCustomerId = customerId;
    }

    public String getEcKpAlias() {
        return mEcKpAlias;
    }

    public void setEcKpAlias(String ecKpAlias) {
        mEcKpAlias = ecKpAlias;
    }

    public String getEcKpValue() {
        return mEcKpValue;
    }

    public void setEcKpValue(String ecKpValue) {
        mEcKpValue = ecKpValue;
    }

    public String getIdNumber() {
        return mIdNumber;
    }

    public void setIdNumber(String idNumber) {
        mIdNumber = idNumber;
    }

    public String getIdType() {
        return mIdType;
    }

    public void setIdType(String idType) {
        mIdType = idType;
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

}
