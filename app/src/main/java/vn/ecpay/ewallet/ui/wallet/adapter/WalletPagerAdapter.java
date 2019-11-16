package vn.ecpay.ewallet.ui.wallet.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentAccountInfo;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentMyWallet;

public class WalletPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public WalletPagerAdapter(@NonNull FragmentManager fm, Context mContext) {
        super(fm);
        this.context = mContext;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0) ? context.getResources().getString(R.string.str_my_wallet) : context.getResources().getString(R.string.str_account_info);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        return (position == 0) ? new FragmentMyWallet() : new FragmentAccountInfo();
    }
}