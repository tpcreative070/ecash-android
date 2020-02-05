
package vn.ecpay.ewallet.model.account.active;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseActiveAccount {

  @SerializedName("responseCode")
  private String mResponseCode;
  @SerializedName("responseData")
  private AccountActive mResponseData;
  @SerializedName("responseMessage")
  private String mResponseMessage;

  public String getResponseCode() {
    return mResponseCode;
  }

  public void setResponseCode(String responseCode) {
    mResponseCode = responseCode;
  }

  public AccountActive getResponseData() {
    return mResponseData;
  }

  public void setResponseData(AccountActive responseData) {
    mResponseData = responseData;
  }

  public String getResponseMessage() {
    return mResponseMessage;
  }

  public void setResponseMessage(String responseMessage) {
    mResponseMessage = responseMessage;
  }

}
