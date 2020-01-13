
package vn.ecpay.ewallet.webSocket.object;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import vn.ecpay.ewallet.model.BaseObject;
import vn.ecpay.ewallet.model.contactTransfer.Contact;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseMessSocket extends BaseObject implements Serializable {

    @SerializedName("sender")
    private String mSender;
    @SerializedName("receiver")
    private String mReceiver;
    @SerializedName("time")
    private String mTime;
    @SerializedName("type")
    private String mType;
    @SerializedName("content")
    private String mContent;
    @SerializedName("id")
    private String mId;
    @SerializedName("refId")
    private String mRefId;
    @SerializedName("requireConfirm")
    private String mRequireConfirm;
    @SerializedName("cashEnc")
    private String mCashEnc;
    @SerializedName("contacts")
    private ArrayList<Contact> contactSyncSockets;

    public String getCashEnc() {
        return mCashEnc;
    }

    public void setCashEnc(String cashEnc) {
        mCashEnc = cashEnc;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

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

    public String getRequireConfirm() {
        return mRequireConfirm;
    }

    public void setRequireConfirm(String requireConfirm) {
        mRequireConfirm = requireConfirm;
    }

    public String getSender() {
        return mSender;
    }

    public void setSender(String sender) {
        mSender = sender;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public ArrayList<Contact> getContacts() {
        return contactSyncSockets;
    }

    public void setContacts(ArrayList<Contact> contactSyncSockets) {
        this.contactSyncSockets = contactSyncSockets;
    }
}
