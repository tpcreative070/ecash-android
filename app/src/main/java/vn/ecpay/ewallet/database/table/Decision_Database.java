package vn.ecpay.ewallet.database.table;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DECISIONS")
public class Decision_Database {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "decisionNo")
    private String decisionNo;

    @NonNull
    @ColumnInfo(name = "accountPublicKeyValue")
    private String accountPublicKeyValue;

    @NonNull
    @ColumnInfo(name = "treasurePublicKeyValue")
    private String treasurePublicKeyValue;

    @NonNull
    public String getAccountPublicKeyValue() {
        return accountPublicKeyValue;
    }

    public void setAccountPublicKeyValue(@NonNull String accountPublicKeyValue) {
        this.accountPublicKeyValue = accountPublicKeyValue;
    }

    @NonNull
    public String getTreasurePublicKeyValue() {
        return treasurePublicKeyValue;
    }

    public void setTreasurePublicKeyValue(@NonNull String treasurePublicKeyValue) {
        this.treasurePublicKeyValue = treasurePublicKeyValue;
    }

    @NonNull
    public String getDecisionNo() {
        return decisionNo;
    }

    public void setDecisionNo(@NonNull String decisionNo) {
        this.decisionNo = decisionNo;
    }
}
