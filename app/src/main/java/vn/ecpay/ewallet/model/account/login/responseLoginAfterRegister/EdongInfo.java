
package vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import javax.annotation.Generated;

import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
@Entity(tableName = "EDONG")
public class EdongInfo {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "accountIdt")
    @SerializedName("accountIdt")
    private Long accountIdt;

    @ColumnInfo(name = "accBalance")
    @SerializedName("accBalance")
    private Float accBalance;

    @ColumnInfo(name = "accLock")
    @SerializedName("accLock")
    private Float accLock;

    @ColumnInfo(name = "accountStatus")
    @SerializedName("accountStatus")
    private String accountStatus;

    @ColumnInfo(name = "accountType")
    @SerializedName("accountType")
    private Long accountType;
    @SerializedName("usableBalance")
    private Float usableBalance;


    public Long getAccBalance() {
        return accBalance.longValue();
    }

    public Float getAccLock() {
        return accLock;
    }

    public void setAccLock(Float accLock) {
        this.accLock = accLock;
    }

    @NonNull
    public Long getAccountIdt() {
        return accountIdt;
    }

    public void setAccountIdt(@NonNull Long accountIdt) {
        this.accountIdt = accountIdt;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Long getAccountType() {
        return accountType;
    }

    public void setAccountType(Long accountType) {
        this.accountType = accountType;
    }

    public Long getUsableBalance() {
        return usableBalance.longValue();
    }
}
