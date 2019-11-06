
package vn.ecpay.ewallet.model.QRCode;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class QRCodeSender {
    @SerializedName("content")
    private String mContent;
    @SerializedName("cycle")
    private int mCycle;
    @SerializedName("total")
    private int mTotal;

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getCycle() {
        return mCycle;
    }

    public void setCycle(int cycle) {
        mCycle = cycle;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        mTotal = total;
    }

}
