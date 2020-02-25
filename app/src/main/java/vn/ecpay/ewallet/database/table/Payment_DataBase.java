package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import vn.ecpay.ewallet.model.payment.Payments;
@Entity(tableName = "PAYMENTS")
public class Payment_DataBase extends Payments {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "toPay")
    private boolean toPay;

//    public Payment_DataBase(Payments payments,boolean toPay){
//        sender =payments.getSender();
//        time =payments.getTime();
//        type =payments.getType();
//        content =payments.getContent();
//        senderPublicKey =payments.getSenderPublicKey();
//        totalAmount =payments.getTotalAmount();
//        channelSignature =payments.getChannelSignature();
//        fullName =payments.getFullName();
//        this.toPay =toPay;
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isToPay() {
        return toPay;
    }

    public void setToPay(boolean toPay) {
        this.toPay = toPay;
    }
}
