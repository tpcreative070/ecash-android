package vn.ecpay.ewallet.model.payment;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.model.cashValue.CashTotal;

public class CashValid {
    private List<CashTotal> listCashValid = new ArrayList<>();
    private int cashLeft;

    public CashValid() {
    }

    public CashValid(List<CashTotal> listCashValid, int cashLeft) {
        this.listCashValid = listCashValid;
        this.cashLeft = cashLeft;
    }

    public List<CashTotal> getListCashValid() {
        return listCashValid;
    }

    public void setListCashValid(List<CashTotal> listCashValid) {
        this.listCashValid = listCashValid;
    }

    public int getCashLeft() {
        return cashLeft;
    }

    public void setCashLeft(int cashLeft) {
        this.cashLeft = cashLeft;
    }
}
