
package vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseLoginAfterRegister {

  @SerializedName("responseCode")
  private String mResponseCode;
  @SerializedName("responseData")
  private AccountInfo accountInfo;
  @SerializedName("responseMessage")
  private String mResponseMessage;

  public String getResponseCode() {
    return mResponseCode;
  }

  public AccountInfo getAccountInfo() {
    return accountInfo;
  }

  public void setAccountInfo(AccountInfo accountInfo) {
    this.accountInfo = accountInfo;
  }

  public void setResponseData(AccountInfo responseData) {
    accountInfo = responseData;
  }

  public String getResponseMessage() {
    return mResponseMessage;
  }

  public void setResponseMessage(String responseMessage) {
    mResponseMessage = responseMessage;
  }

}
