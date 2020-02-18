package vn.ecpay.ewallet.model.payment;

import java.util.ArrayList;
import java.util.List;

public class CashConvert {
    private List<Integer> listQualitySend = new ArrayList<>();
    private List<Integer> listValueSend =new ArrayList<>();

    public List<Integer> getListQualitySend() {
        return listQualitySend;
    }

    public void setListQualitySend(List<Integer> listQualitySend) {
        this.listQualitySend = listQualitySend;
    }

    public List<Integer> getListValueSend() {
        return listValueSend;
    }

    public void setListValueSend(List<Integer> listValueSend) {
        this.listValueSend = listValueSend;
    }
}
