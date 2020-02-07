package vn.ecpay.ewallet.model.payment;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.model.cashValue.CashTotal;

public class CashValid {
    private List<CashTotal> listCashValid = new ArrayList<>();
    private List<CashTotal> listCashRemain = new ArrayList<>();
    private int cashRemain;

    public CashValid() {
    }

    public CashValid(List<CashTotal> listCashValid, int cashRemain) {
        this.listCashValid = listCashValid;
        this.cashRemain = cashRemain;
    }

    public List<CashTotal> getListCashValid() {
        return listCashValid;
    }

    public void setListCashValid(List<CashTotal> listCashValid) {
        this.listCashValid = listCashValid;
    }

    public List<CashTotal> getListCashRemain() {
        return listCashRemain;
    }

    public void setListCashRemain(List<CashTotal> listCashRemain) {
        this.listCashRemain = listCashRemain;
    }

    public int getCashRemain() {
        return cashRemain;
    }

    public void setCashRemain(int cashRemain) {
        this.cashRemain = cashRemain;
    }
}
