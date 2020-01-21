package vn.ecpay.ewallet.model.QRCode;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;

import vn.ecpay.ewallet.model.BaseObject;

public class QRCodePayment extends BaseObject implements Serializable {
    @SerializedName("sender")
    public String sender;

    @SerializedName("time")
    public String time;

    @SerializedName("type")
    public String type;

    @SerializedName("content")
    public String content;

    @SerializedName("senderPublicKey")
    public String senderPublicKey;

    @SerializedName("totalAmount")
    public String totalAmount;

    @SerializedName("channelSignature")
    public String channelSignature;

    @SerializedName("fullName")
    public String fullName;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getChannelSignature() {
        return channelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        this.channelSignature = channelSignature;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "QRCodePayment{" +
                "sender='" + sender + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", senderPublicKey='" + senderPublicKey + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", channelSignature='" + channelSignature + '\'' +
                '}';
    }

    public boolean validate(String json) {
        JSONObject object;
        try {
            object = new JSONObject(json);
            for (Field f : this.getClass().getDeclaredFields()) {
                SerializedName serializedName = f.getAnnotation(SerializedName.class);
                if (serializedName != null) {
                    if (!object.has(serializedName.value())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
