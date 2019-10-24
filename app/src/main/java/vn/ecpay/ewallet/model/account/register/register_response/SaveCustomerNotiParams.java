
package vn.ecpay.ewallet.model.account.register.register_response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class SaveCustomerNotiParams {

    @SerializedName("customerId")
    private Long mCustomerId;
    @SerializedName("customerParamId")
    private Long mCustomerParamId;
    @SerializedName("customerParamsDateCreated")
    private String mCustomerParamsDateCreated;
    @SerializedName("customerParamsDateModified")
    private Object mCustomerParamsDateModified;
    @SerializedName("customerParamsStatus")
    private String mCustomerParamsStatus;
    @SerializedName("personEmail")
    private String mPersonEmail;
    @SerializedName("personMobilePhone")
    private String mPersonMobilePhone;

    public Long getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(Long customerId) {
        mCustomerId = customerId;
    }

    public Long getCustomerParamId() {
        return mCustomerParamId;
    }

    public void setCustomerParamId(Long customerParamId) {
        mCustomerParamId = customerParamId;
    }

    public String getCustomerParamsDateCreated() {
        return mCustomerParamsDateCreated;
    }

    public void setCustomerParamsDateCreated(String customerParamsDateCreated) {
        mCustomerParamsDateCreated = customerParamsDateCreated;
    }

    public Object getCustomerParamsDateModified() {
        return mCustomerParamsDateModified;
    }

    public void setCustomerParamsDateModified(Object customerParamsDateModified) {
        mCustomerParamsDateModified = customerParamsDateModified;
    }

    public String getCustomerParamsStatus() {
        return mCustomerParamsStatus;
    }

    public void setCustomerParamsStatus(String customerParamsStatus) {
        mCustomerParamsStatus = customerParamsStatus;
    }

    public String getPersonEmail() {
        return mPersonEmail;
    }

    public void setPersonEmail(String personEmail) {
        mPersonEmail = personEmail;
    }

    public String getPersonMobilePhone() {
        return mPersonMobilePhone;
    }

    public void setPersonMobilePhone(String personMobilePhone) {
        mPersonMobilePhone = personMobilePhone;
    }

}
