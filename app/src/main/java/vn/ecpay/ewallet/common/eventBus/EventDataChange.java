package vn.ecpay.ewallet.common.eventBus;

import java.util.ArrayList;

import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;

public class EventDataChange {
    String data;
    ArrayList<QRCashTransfer> qrCashTransfers;

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
}
