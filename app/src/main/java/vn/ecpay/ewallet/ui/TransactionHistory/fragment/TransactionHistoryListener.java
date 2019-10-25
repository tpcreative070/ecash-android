package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import java.io.Serializable;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;

public interface  TransactionHistoryListener extends Serializable {
    void itemClick(TransactionsHistoryModel transactionsHistoryModelModel);
}
