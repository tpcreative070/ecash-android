
package vn.ecpay.ewallet.model.account.updateAvartar.response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

    @SerializedName("dateModified")
    private String mDateModified;
    @SerializedName("groupUserId")
    private Long mGroupUserId;
    @SerializedName("nickname")
    private String mNickname;
    @SerializedName("userId")
    private Long mUserId;

    public String getDateModified() {
        return mDateModified;
    }

    public void setDateModified(String dateModified) {
        mDateModified = dateModified;
    }

    public Long getGroupUserId() {
        return mGroupUserId;
    }

    public void setGroupUserId(Long groupUserId) {
        mGroupUserId = groupUserId;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

}
