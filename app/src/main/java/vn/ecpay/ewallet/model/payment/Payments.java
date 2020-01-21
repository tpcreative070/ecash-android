package vn.ecpay.ewallet.model.payment;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;

import vn.ecpay.ewallet.model.QRCode.QRCodePayment;

public class Payments extends QRCodePayment implements Serializable {

    @SerializedName("receiver")
    private String receiver;

    @SerializedName("requireConfirm")
    private String requireConfirm;

    @SerializedName("refId")
    private String refId;

    public String getSender() {
        return sender;
    }

    public Payments() {
    }

    public Payments(QRCodePayment qrCodePayment) {
        this.sender =qrCodePayment.sender;
        this.time =qrCodePayment.time;
        this.type =qrCodePayment.type;
        this.content =qrCodePayment.content;
        this.senderPublicKey =qrCodePayment.senderPublicKey;
        this.totalAmount =qrCodePayment.totalAmount;
        this.channelSignature =qrCodePayment.channelSignature;
        this.fullName =qrCodePayment.fullName;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public String getRequireConfirm() {
        return requireConfirm;
    }

    public void setRequireConfirm(String requireConfirm) {
        this.requireConfirm = requireConfirm;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public boolean validate(String json) {
        JSONObject object;
        try {
            object = new JSONObject(json);
           // Log.e("object ",object.toString());
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

