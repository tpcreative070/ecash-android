package vn.ecpay.ewallet.model.cashValue;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UtilCashTotal {
    // Đệ qui để lấy số tờ tiền tối ưu hiện có trong ví => sẽ trả về ds tờ tiền và số tiền còn dư nếu có
    public ResultOptimal recursiveFindeCashs(List<CashTotal> walletList, List<CashTotal> partialList, long amount) {
        long remainAmount = amount;
        List<CashTotal> nextWalletList = walletList;
        List<CashTotal> nextListPartial = partialList;
        if (walletList.size() > 0) {
            boolean isBreaked = false;
            int ind = 0;
            while (!isBreaked && ind < walletList.size()) {
                CashTotal item = walletList.get(ind);
                int value = item.getParValue();
                if (value <= remainAmount) {
                    isBreaked = true;
                    remainAmount -= value;
                    nextListPartial.add(item);

                    ArrayList<CashTotal> tmp = new ArrayList<CashTotal>();
                    for (int i = 0; i < ind; i++) {
                        tmp.add(nextWalletList.get(i));
                    }

                    for (int i = ind + 1; i < nextWalletList.size(); i++) {
                        tmp.add(nextWalletList.get(i));
                    }

                    nextWalletList = tmp;
                    if (remainAmount <= 0) {
                        // Completed get eCash page from Wallet for amount
                        Log.e("Debugger", "------------Completed---------------");
                        return new ResultOptimal(nextWalletList, nextListPartial, remainAmount);
                    } else {
                        // Continue get eCash page
                        Log.e("Debugger", "------------Continue---------------");
                        return recursiveFindeCashs(nextWalletList, nextListPartial, remainAmount);
                    }
                } else if (ind == walletList.size() - 1) {
                    // Have reach end of Wallet
                    Log.e("Debugger", "------------Reach End---------------");
                    isBreaked = true;
                    return new ResultOptimal(nextWalletList, nextListPartial, remainAmount);
                }
                ind += 1;
            }

        } else {
            // Wallet have no money
            return recursiveFindeCashs(nextWalletList, nextListPartial, remainAmount);
        }
        return recursiveFindeCashs(nextWalletList, nextListPartial, remainAmount);
    }

    public ResultOptimal recursiveGetArrayNeedExchange(long remainAmount, List<CashTotal> partialArray) {
        int denominations = getOptilmalDenominations(remainAmount);
        int mutile = (int) (remainAmount / denominations);
        long wholeAmout = denominations * mutile;
        long oddAmount = remainAmount - wholeAmout;
        List<CashTotal> nextArray = partialArray;
        for (int i = 1; i <= mutile; i++) {
            CashTotal item = new CashTotal(denominations, mutile, 0);
            nextArray.add(item);
        }
        if (oddAmount <= 200) {
            return new ResultOptimal(new ArrayList<CashTotal>(), nextArray, oddAmount);
        } else {
            return recursiveGetArrayNeedExchange(oddAmount, nextArray);
        }
    }


    public int getOptilmalDenominations(long money) {
        ArrayList<Integer> arrayDenominations = new ArrayList<Integer>();
        arrayDenominations.add(200);
        arrayDenominations.add(500);
        arrayDenominations.add(1000);
        arrayDenominations.add(2000);
        arrayDenominations.add(5000);
        arrayDenominations.add(10000);
        arrayDenominations.add(20000);
        arrayDenominations.add(50000);
        arrayDenominations.add(100000);
        arrayDenominations.add(200000);
        arrayDenominations.add(500000);
        int max = Integer.valueOf(0);
        for (Integer value : arrayDenominations) {
            if (value <= money) {
                max = value;
            }
        }
        return max;
    }
}