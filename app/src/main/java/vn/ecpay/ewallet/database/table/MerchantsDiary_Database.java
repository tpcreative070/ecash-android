package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MERCHANTS_DIARY")
public class MerchantsDiary_Database {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "merchantCode")
    private String merchantCode;

    @ColumnInfo(name = "publicKeyAlias")
    private String publicKeyAlias;

    @ColumnInfo(name = "publicKeyValue")
    private String publicKeyValue;

    @ColumnInfo(name = "systemDate")
    private String systemDate;

    @NonNull
    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(@NonNull String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }

    public void setPublicKeyAlias(String publicKeyAlias) {
        this.publicKeyAlias = publicKeyAlias;
    }

    public String getPublicKeyValue() {
        return publicKeyValue;
    }

    public void setPublicKeyValue(String publicKeyValue) {
        this.publicKeyValue = publicKeyValue;
    }

    public String getSystemDate() {
        return systemDate;
    }

    public void setSystemDate(String systemDate) {
        this.systemDate = systemDate;
    }
}
