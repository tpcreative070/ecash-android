
package vn.ecpay.ewallet.model.chanel.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

    @SerializedName("groupChannelId")
    private Long mGroupChannelId;
    @SerializedName("id")
    private Long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("requireAuthorization")
    private String mRequireAuthorization;
    @SerializedName("requireSession")
    private String mRequireSession;

    public Long getGroupChannelId() {
        return mGroupChannelId;
    }

    public void setGroupChannelId(Long groupChannelId) {
        mGroupChannelId = groupChannelId;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
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
