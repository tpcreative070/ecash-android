package vn.ecpay.ewallet.ui.interfaceListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.model.contactTransfer.Contact;

public interface MultiTransferListener extends Serializable {
    void onMultiTransfer(ArrayList<Contact> contactList);
}
