
package vn.ecpay.ewallet.model.account.register.register_response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class NotificationList {

    @SerializedName("customerId")
    private Long mCustomerId;
    @SerializedName("customerRegnotiId")
    private Long mCustomerRegnotiId;
    @SerializedName("customerRegnotiStatus")
    private String mCustomerRegnotiStatus;
    @SerializedName("dateJoin")
    private String mDateJoin;
    @SerializedName("dateLeft")
    private Object mDateLeft;
    @SerializedName("notificationMethodId")
    private Long mNotificationMethodId;
    @SerializedName("notificationTypeId")
    private Long mNotificationTypeId;

    public Long getCustomerId() {
        return mCustomerId;
    }

    public void setCustomerId(Long customerId) {
        mCustomerId = customerId;
    }

    public Long getCustomerRegnotiId() {
        return mCustomerRegnotiId;
    }

    public void setCustomerRegnotiId(Long customerRegnotiId) {
        mCustomerRegnotiId = customerRegnotiId;
    }

    public String getCustomerRegnotiStatus() {
        return mCustomerRegnotiStatus;
    }

    public void setCustomerRegnotiStatus(String customerRegnotiStatus) {
        mCustomerRegnotiStatus = customerRegnotiStatus;
    }

    public String getDateJoin() {
        return mDateJoin;
    }

    public void setDateJoin(String dateJoin) {
        mDateJoin = dateJoin;
    }

    public Object getDateLeft() {
        return mDateLeft;
    }

    public void setDateLeft(Object dateLeft) {
        mDateLeft = dateLeft;
    }

    public Long getNotificationMethodId() {
        return mNotificationMethodId;
    }

    public void setNotificationMethodId(Long notificationMethodId) {
        mNotificationMethodId = notificationMethodId;
    }

    public Long getNotificationTypeId() {
        return mNotificationTypeId;
    }

    public void setNotificationTypeId(Long notificationTypeId) {
        mNotificationTypeId = notificationTypeId;
    }

}
