package vn.ecpay.ewallet.model.cashValue;

import android.util.Log;

import java.util.ArrayList;

public class CashTotal {
    private int parValue;
    private int totalDatabase;
    private int total;

    public int getParValue() {
        return parValue;
    }

    public void setParValue(int parValue) {
        this.parValue = parValue;
    }

    public int getTotalDatabase() {
        return totalDatabase;
    }

    public void setTotalDatabase(int totalDatabase) {
        this.totalDatabase = totalDatabase;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public CashTotal() {
    }

    public CashTotal(int parValue, int total, int totalDatabase) {
        this.parValue = parValue;
        this.totalDatabase = totalDatabase;
        this.total = total;
    }
    public ArrayList<CashTotal> slitCashTotal() {
        ArrayList<CashTotal> result = new ArrayList<>();
        for (int i = 0; i < totalDatabase; i++) {
            CashTotal item = new CashTotal(parValue, 0, totalDatabase);
            result.add(item);
        }
        return result;
    }
}


