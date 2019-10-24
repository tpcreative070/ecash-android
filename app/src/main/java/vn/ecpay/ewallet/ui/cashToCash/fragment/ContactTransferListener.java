package vn.ecpay.ewallet.ui.cashToCash.fragment;

import java.io.Serializable;

import vn.ecpay.ewallet.model.contactTransfer.ContactTransferModel;

public interface ContactTransferListener extends Serializable {
    void itemClick(ContactTransferModel transferModel);
}
