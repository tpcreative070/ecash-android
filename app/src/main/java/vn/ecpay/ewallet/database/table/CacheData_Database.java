package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CACHE_DATA")
public class CacheData_Database {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "transactionSignature")
    private String transactionSignature;

    @NonNull
    @ColumnInfo(name = "responseData")
    private String responseData;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "status")
    private String countryCode;

    @NonNull
    public String getTransactionSignature() {
        return transactionSignature;
    }

    public void setTransactionSignature(@NonNull String transactionSignature) {
        this.transactionSignature = transactionSignature;
    }

    @NonNull
    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(@NonNull String responseData) {
        this.responseData = responseData;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
