
package vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseDataGetWalletByPhone {

    @SerializedName("customerId")
    private String mCustomerId;
    @SerializedName("customerStatus")
    private String mCustomerStatus;
    @SerializedName("idNumber")
    private String mIdNumber;
    @SerializedName("personFirstName")
    private String mPersonFirstName;
    @SerializedName("personLastName")
    private String mPersonLastName;
    @SerializedName("personMiddleName")
    private String mPersonMiddleName;
    @SerializedName("personMobilePhone")
    private String mPersonMobilePhone;
    @SerializedName("wallets")
    private ArrayList<Wallet> mWallets;

    public String getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(String customerId) {
        mCustomerId = customerId;
    }

    public String getCustomerStatus() {
        return mCustomerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        mCustomerStatus = customerStatus;
    }

    public String getIdNumber() {
        return mIdNumber;
    }

    public void setIdNumber(String idNumber) {
        mIdNumber = idNumber;
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

    public ArrayList<Wallet> getWallets() {
        return mWallets;
    }

    public void setWallets(ArrayList<Wallet> wallets) {
        mWallets = wallets;
    }

}
