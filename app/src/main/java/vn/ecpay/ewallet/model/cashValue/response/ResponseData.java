
package vn.ecpay.ewallet.model.cashValue.response;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class ResponseData {

    @SerializedName("channelCode")
    private String mChannelCode;
    @SerializedName("channelSignature")
    private String mChannelSignature;
    @SerializedName("listDenomination")
    private List<Denomination> mListDenomination;

    public String getChannelCode() {
        return mChannelCode;
    }

    public void setChannelCode(String channelCode) {
        mChannelCode = channelCode;
    }

    public String getChannelSignature() {
        return mChannelSignature;
    }

    public void setChannelSignature(String channelSignature) {
        mChannelSignature = channelSignature;
    }

    public List<Denomination> getListDenomination() {
        return mListDenomination;
    }

    public void setListDenomination(List<Denomination> listDenomination) {
        mListDenomination = listDenomination;
    }

}
