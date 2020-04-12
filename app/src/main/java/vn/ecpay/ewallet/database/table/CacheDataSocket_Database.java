package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CACHE_SOCKET_DATA")
public class CacheDataSocket_Database {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "sender")
    private String sender;

    @NonNull
    @ColumnInfo(name = "receiver")
    private String receiver;

    @NonNull
    @ColumnInfo(name = "time")
    private String time;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "content")
    private String content;

    @NonNull
    @ColumnInfo(name = "cashEnc")
    private String cashEnc;

    @ColumnInfo(name = "refId")
    private String refId;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    public void setSender(@NonNull String sender) {
        this.sender = sender;
    }

    @NonNull
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(@NonNull String receiver) {
        this.receiver = receiver;
    }

    @NonNull
    public String getTime() {
        return time;
    }

    public void setTime(@NonNull String time) {
        this.time = time;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    public String getCashEnc() {
        return cashEnc;
    }

    public void setCashEnc(@NonNull String cashEnc) {
        this.cashEnc = cashEnc;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
