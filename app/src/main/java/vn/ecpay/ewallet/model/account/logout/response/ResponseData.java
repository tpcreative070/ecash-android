
package vn.ecpay.ewallet.model.account.logout.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

  @SerializedName("channelCode")
  private String mChannelCode;
  @SerializedName("channelId")
  private Long mChannelId;
  @SerializedName("channelSignature")
  private String mChannelSignature;
  @SerializedName("customerId")
  private Long mCustomerId;
  @SerializedName("functionCode")
  private String mFunctionCode;
  @SerializedName("functionId")
  private Long mFunctionId;
  @SerializedName("token")
  private String mToken;
  @SerializedName("username")
  private String mUsername;

  public String getChannelCode() {
    return mChannelCode;
  }

  public void setChannelCode(String channelCode) {
    mChannelCode = channelCode;
  }

  public Long getChannelId() {
    return mChannelId;
  }

  public void setChannelId(Long channelId) {
    mChannelId = channelId;
  }

  public String getChannelSignature() {
    return mChannelSignature;
  }

  public void setChannelSignature(String channelSignature) {
    mChannelSignature = channelSignature;
  }

  public Long getCustomerId() {
    return mCustomerId;
  }

  public void setCustomerId(Long customerId) {
    mCustomerId = customerId;
  }

  public String getFunctionCode() {
    return mFunctionCode;
  }

  public void setFunctionCode(String functionCode) {
    mFunctionCode = functionCode;
  }

  public Long getFunctionId() {
    return mFunctionId;
  }

  public void setFunctionId(Long functionId) {
    mFunctionId = functionId;
  }

  public String getToken() {
    return mToken;
  }

  public void setToken(String token) {
    mToken = token;
  }

  public String getUsername() {
    return mUsername;
  }

  public void setUsername(String username) {
    mUsername = username;
  }

}
