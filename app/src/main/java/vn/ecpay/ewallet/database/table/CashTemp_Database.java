package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CASH_TEMP")
public class CashTemp_Database {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "senderAccountId")
    private String senderAccountId;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    @NonNull
    @ColumnInfo(name = "transactionSignature")
    private String transactionSignature;

    @NonNull
    @ColumnInfo(name = "receiveDate")
    private String receiveDate;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(@NonNull String senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public String getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(@NonNull String transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    @NonNull
    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(@NonNull String receiveDate) {
        this.receiveDate = receiveDate;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }
}
