package vn.ecpay.ewallet.common.eventBus;

import android.app.Activity;
import android.content.Intent;

import java.util.List;

import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;

public class EventDataChange {
    private String data;

    //update avatar
    private int requestCode;
    private Activity activity;
    private int resultCode;
    private Intent dataImage;

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

    public String getData() {
        return data;
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
