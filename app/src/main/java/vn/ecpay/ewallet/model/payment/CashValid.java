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

    public List<Integer> listCash(){
        List<Integer> list = new ArrayList<>();
        list.add(1000);
        list.add(2000);
        list.add(5000);
        list.add(10000);
        list.add(20000);
        list.add(50000);
        list.add(100000);
        list.add(200000);
        list.add(500000);
        return list;
    }

    public CashConvert mapCash(CashConvert cashSend,CashTotal cashTotal,int cashRemain) {//30000
        int cash =cashTotal.getParValue() - cashRemain;
        for(int i=0;i<listCash().size();i++){
            if(cashRemain<=listCash().get(i)){

            }
        }
        return cashSend;
    }
}
