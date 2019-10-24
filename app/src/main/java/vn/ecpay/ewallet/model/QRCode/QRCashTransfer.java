package vn.ecpay.ewallet.model.QRCode;

import android.os.Parcel;
import android.os.Parcelable;

public class QRCashTransfer implements Parcelable {
    private boolean isSuccess;
    private int parValue;
    private String name;
    private String phone;
    private String content;
    private String totalMoney;

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getParValue() {
        return parValue;
    }

    public void setParValue(int parValue) {
        this.parValue = parValue;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSuccess ? (byte) 1 : (byte) 0);
        dest.writeInt(this.parValue);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.content);
        dest.writeString(this.totalMoney);
    }

    public QRCashTransfer() {
    }

    protected QRCashTransfer(Parcel in) {
        this.isSuccess = in.readByte() != 0;
        this.parValue = in.readInt();
        this.name = in.readString();
        this.phone = in.readString();
        this.content = in.readString();
        this.totalMoney = in.readString();
    }

    public static final Creator<QRCashTransfer> CREATOR = new Creator<QRCashTransfer>() {
        @Override
        public QRCashTransfer createFromParcel(Parcel source) {
            return new QRCashTransfer(source);
        }

        @Override
        public QRCashTransfer[] newArray(int size) {
            return new QRCashTransfer[size];
        }
    };
}
