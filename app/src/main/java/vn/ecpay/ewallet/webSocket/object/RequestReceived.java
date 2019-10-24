
package vn.ecpay.ewallet.webSocket.object;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class RequestReceived {

    @SerializedName("id")
    private String mId;
    @SerializedName("receiver")
    private String mReceiver;
    @SerializedName("refId")
    private String mRefId;
    @SerializedName("type")
    private String mType;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getReceiver() {
        return mReceiver;
    }

    public void setReceiver(String receiver) {
        mReceiver = receiver;
    }

    public String getRefId() {
        return mRefId;
    }

    public void setRefId(String refId) {
        mRefId = refId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
