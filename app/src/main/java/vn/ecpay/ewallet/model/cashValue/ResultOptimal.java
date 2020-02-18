package vn.ecpay.ewallet.model.cashValue;

import java.util.ArrayList;
import java.util.List;

public class ResultOptimal {
    public List<CashTotal> listWallet;
    public List<CashTotal> listPartial;
    public long remain;

    ResultOptimal(List<CashTotal> listWallet, List<CashTotal> listPartial, long remain) {
        this.listWallet = listWallet;
        this.listPartial = listPartial;
        this.remain = remain;
    }
}