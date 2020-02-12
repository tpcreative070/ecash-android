
package vn.ecpay.ewallet.model.chanel;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestUpdateChanel {

  @SerializedName("clientKp")
  private String mClientKp;
  @SerializedName("groupChannelId")
  private String mGroupChannelId;
  @SerializedName("id")
  private String mId;
  @SerializedName("name")
  private String mName;
  @SerializedName("requireAuthorization")
  private String mRequireAuthorization;
  @SerializedName("requireSession")
  private String mRequireSession;

  public String getClientKp() {
    return mClientKp;
  }

  public void setClientKp(String clientKp) {
    mClientKp = clientKp;
  }

  public String getGroupChannelId() {
    return mGroupChannelId;
  }

  public void setGroupChannelId(String groupChannelId) {
    mGroupChannelId = groupChannelId;
  }

  public String getId() {
    return mId;
  }

  public void setId(String id) {
    mId = id;
  }

  public String getName() {
    return mName;
  }

  public void setName(String name) {
    mName = name;
  }

  public String getRequireAuthorization() {
    return mRequireAuthorization;
  }

  public void setRequireAuthorization(String requireAuthorization) {
    mRequireAuthorization = requireAuthorization;
  }

  public String getRequireSession() {
    return mRequireSession;
  }

  public void setRequireSession(String requireSession) {
    mRequireSession = requireSession;
  }

}
