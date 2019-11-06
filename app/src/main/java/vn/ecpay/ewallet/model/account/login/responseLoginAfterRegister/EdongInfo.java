
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
    private Long accBalance;

    @ColumnInfo(name = "accLock")
    @SerializedName("accLock")
    private Long accLock;

    @ColumnInfo(name = "accountStatus")
    @SerializedName("accountStatus")
    private String accountStatus;

    @ColumnInfo(name = "accountType")
    @SerializedName("accountType")
    private Long accountType;
    @SerializedName("usableBalance")
    private Long usableBalance;


    public Long getAccBalance() {
        return accBalance;
    }

    public void setAccBalance(Long accBalance) {
        this.accBalance = accBalance;
    }

    public Long getAccLock() {
        return accLock;
    }

    public void setAccLock(Long accLock) {
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
        return usableBalance;
    }

    public void setUsableBalance(Long usableBalance) {
        this.usableBalance = usableBalance;
    }
}
