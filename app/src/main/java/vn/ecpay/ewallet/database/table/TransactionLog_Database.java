package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TRANSACTIONS_LOGS")
public class TransactionLog_Database {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "transactionSignature")
    private String transactionSignature;

    @ColumnInfo(name = "senderAccountId")
    private String senderAccountId;

    @ColumnInfo(name = "receiverAccountId")
    private String receiverAccountId;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "cashEnc")
    private String cashEnc;

    @ColumnInfo(name = "refId")
    private String refId;

    @ColumnInfo(name = "previousHash")
    private String previousHash;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(String senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(String receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCashEnc() {
        return cashEnc;
    }

    public void setCashEnc(String cashEnc) {
        this.cashEnc = cashEnc;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    @NonNull
    public String getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(@NonNull String transactionSignature) {
        this.transactionSignature = transactionSignature;
    }
}
