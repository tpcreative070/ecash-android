package vn.ecpay.ewallet.model.toPay;

import com.google.gson.annotations.SerializedName;

import vn.ecpay.ewallet.model.BaseObject;

public class RequestToPay extends BaseObject {
    @SerializedName("content")
    private String content ;

    @SerializedName("receiver ")
    private String receiver ;

    @SerializedName("sender")
    private String sender;

    @SerializedName("senderPublicKey")
    private String senderPublicKey;

    @SerializedName("time")
    private String time;

    @SerializedName("totalAmount")
    private String totalAmount;

    @SerializedName("type")
    private String type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
