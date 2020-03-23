package vn.ecpay.ewallet.ui.callbackListener;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import vn.ecpay.ewallet.model.contactTransfer.Contact;

public interface MultiTransferListener extends Serializable {
    void onMultiTransfer(ArrayList<Contact> contactList);
}
