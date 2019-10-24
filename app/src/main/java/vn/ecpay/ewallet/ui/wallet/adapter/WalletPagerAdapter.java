package vn.ecpay.ewallet.ui.wallet.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import vn.ecpay.ewallet.ui.wallet.fragment.FragmentAccountInfo;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentMyWallet;

public class WalletPagerAdapter extends FragmentPagerAdapter {

    public WalletPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0)? "Ví của tôi" : "Thông tin tài khoản" ;
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public Fragment getItem(int position) {
        return (position == 0)? new FragmentMyWallet() : new FragmentAccountInfo() ;
    }
}