package vn.ecpay.ewallet.common.eventBus;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;

public class EventDataChange {
    String data;
    ArrayList<QRCashTransfer> qrCashTransfers;

    int requestCode;
    Activity activity;
    int resultCode;
    Intent dataImage;

    public EventDataChange(String data, int requestCode, Activity activity, int resultCode, Intent dataImage) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.resultCode = resultCode;
        this.dataImage = dataImage;
        this.data = data;
    }

    public EventDataChange(String data) {
        this.data = data;
    }

    public EventDataChange(String data, ArrayList<QRCashTransfer> qrCashTransfers) {
        this.data = data;
        this.qrCashTransfers = qrCashTransfers;
    }

    public String getData() {
        return data;
    }

    public ArrayList<QRCashTransfer> getQrCashTransfers() {
        return qrCashTransfers;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getDataImage() {
        return dataImage;
    }

    public void setDataImage(Intent dataImage) {
        this.dataImage = dataImage;
    }
}
