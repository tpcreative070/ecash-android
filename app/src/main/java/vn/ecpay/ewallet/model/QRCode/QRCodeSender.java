
package vn.ecpay.ewallet.model.QRCode;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

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
    @SerializedName("type")
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "QRCodeSender{" +
                "mContent='" + mContent + '\'' +
                ", mCycle=" + mCycle +
                ", mTotal=" + mTotal +
                ", type='" + type + '\'' +
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
