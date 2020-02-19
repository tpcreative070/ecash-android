package vn.ecpay.ewallet.model.contactAdd;

import com.google.gson.annotations.SerializedName;

public class ResponseAddContact {
    @SerializedName("responseCode")
    private String mResponseCode;
    @SerializedName("responseMessage")
    private String mResponseMessage;

    public String getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }
}
