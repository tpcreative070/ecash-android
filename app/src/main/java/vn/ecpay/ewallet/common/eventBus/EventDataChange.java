package vn.ecpay.ewallet.common.eventBus;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class EventDataChange {
    private String data;

    //update avatar
    private int requestCode;
    private Activity activity;
    private int resultCode;
    private Intent dataImage;

    //cash out
    private ResponseMessSocket responseMess;
    private ArrayList<CashLogs_Database> listCashSend;

    public EventDataChange(String data, int requestCode, Activity activity, int resultCode, Intent dataImage) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.resultCode = resultCode;
        this.dataImage = dataImage;
        this.data = data;
    }

    public EventDataChange(String data, ResponseMessSocket responseMess, ArrayList<CashLogs_Database> listCashSend) {
        this.data = data;
        this.responseMess = responseMess;
        this.listCashSend = listCashSend;
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

    public ResponseMessSocket getResponseMess() {
        return responseMess;
    }

    public void setResponseMess(ResponseMessSocket responseMess) {
        this.responseMess = responseMess;
    }

    public ArrayList<CashLogs_Database> getListCashSend() {
        return listCashSend;
    }

    public void setListCashSend(ArrayList<CashLogs_Database> listCashSend) {
        this.listCashSend = listCashSend;
    }
}
