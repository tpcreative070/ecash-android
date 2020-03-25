package vn.ecpay.ewallet.ui.wallet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.base.PagerSlidingTabStrip;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;
import vn.ecpay.ewallet.ui.wallet.adapter.WalletPagerAdapter;

public class FragmentWallet extends ECashBaseFragment {
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_wallet;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert getFragmentManager() != null;
        pager.setAdapter(new WalletPagerAdapter(getFragmentManager(), getActivity()));
        tabs.setViewPager(pager);
    }

    @OnClick(R.id.iv_qr_code)
    public void onViewClicked() {
        if (null != getActivity()) {
            Intent intent = new Intent(getActivity(), QRCodeActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
