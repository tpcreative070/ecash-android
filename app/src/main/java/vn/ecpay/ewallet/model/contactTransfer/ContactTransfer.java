package vn.ecpay.ewallet.model.contactTransfer;

import android.graphics.Bitmap;

public class ContactTransfer extends Contact {

    private Bitmap bitmap;

    public ContactTransfer(Contact contact){
        this.setCustomerId(contact.getCustomerId());
        this.setPublicKeyValue(contact.getPublicKeyValue());
        this.setPhone(contact.getPhone());
        this.setTerminalInfo(contact.getTerminalInfo());
        this.setWalletId(contact.getWalletId());
        this.setFullName(contact.getFullName());
        this.setStatus(contact.getStatus());
        this.setSection(contact.isSection());
        this.setAddTransfer(contact.isAddTransfer());
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
