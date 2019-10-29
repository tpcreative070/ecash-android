package vn.ecpay.ewallet.model.transactionsHistory;

public class CashLogTransaction {
    int parValue;
    int validCount;
    int status;

    public CashLogTransaction(int parValue, int validCount, int status) {
        this.parValue = parValue;
        this.validCount = validCount;
        this.status = status;
    }

    public int getParValue() {
        return parValue;
    }

    public void setParValue(int parValue) {
        this.parValue = parValue;
    }

    public int getValidCount() {
        return validCount;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
