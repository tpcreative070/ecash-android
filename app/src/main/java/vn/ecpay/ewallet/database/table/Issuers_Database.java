package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ISSUERS")
public class Issuers_Database {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "issuerCode")
    private String issuerCode;

    @ColumnInfo(name = "issuerName")
    private String issuerName;

    @ColumnInfo(name = "publicKeyValue")
    private String publicKeyValue;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "created")
    private String created;

    @ColumnInfo(name = "modified")
    private String modified;

    @ColumnInfo(name = "destroyed")
    private String destroyed;

    @NonNull
    public String getIssuerCode() {
        return issuerCode;
    }

    public void setIssuerCode(@NonNull String issuerCode) {
        this.issuerCode = issuerCode;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getPublicKeyValue() {
        return publicKeyValue;
    }

    public void setPublicKeyValue(String publicKeyValue) {
        this.publicKeyValue = publicKeyValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getDestroyed() {
        return destroyed;
    }

    public void setDestroyed(String destroyed) {
        this.destroyed = destroyed;
    }
}
