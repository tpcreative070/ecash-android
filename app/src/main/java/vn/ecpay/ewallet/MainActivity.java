package vn.ecpay.ewallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.ecpay.ewallet.common.base.CustomFragmentTabHost;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.ui.QRCode.fragment.FragmentQRCodeTab;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.contact.fragment.FragmentContact;
import vn.ecpay.ewallet.ui.home.HomeFragment;
import vn.ecpay.ewallet.ui.TransactionHistory.fragment.FragmentTransactionHistory;
import vn.ecpay.ewallet.ui.wallet.fragment.FragmentWallet;
import vn.ecpay.ewallet.webSocket.WebSocketsService;

public class MainActivity extends ECashBaseActivity {


    @BindView(R.id.main_content)
    FrameLayout mainContent;
    @BindView(android.R.id.tabcontent)
    FrameLayout tabcontent;
    @BindView(R.id.tab_host)
    CustomFragmentTabHost tabHost;

    private ViewHolder homeTabViewHolder;
    private ViewHolder contactTabViewHolder;
    private ViewHolder historyTabViewHolder;
    private ViewHolder walletTabViewHolder;
    private ViewHolderQRCode qrCodeTabViewHolder;

    public static final String TAB_TAG_HOME = "home";
    public static final String TAB_TAG_CONTACT = "contact";
    public static final String TAB_TAG_HISTORY = "history";
    public static final String TAB_TAG_MY_WALLET = "wallet";
    public static final String TAB_TAG_QR_CODE = "code";
    private String currentTabTag = TAB_TAG_HOME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTabs();
        tabHost.setOnTabChangedListener(this::onChange);

        tabHost.getTabWidget().getChildAt(2).setOnClickListener(v -> {
            Intent intentCashIn = new Intent(this, QRCodeActivity.class);
            startActivity(intentCashIn);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main_tab;
    }

    @Override
    protected void setupActivityComponent() {

    }

    static class ViewHolder {
        public View view;

        @BindView(R.id.tab_image)
        public ImageView imageView;

        @BindView(R.id.tab_name)
        public TextView nameView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    static class ViewHolderQRCode {
        public View view;

        @BindView(R.id.tab_image)
        public ImageView imageView;

        public ViewHolderQRCode(View view) {
            ButterKnife.bind(this, view);
            this.view = view;
        }
    }

    private void setupTabs() {
        tabHost.setup(this, getSupportFragmentManager(), R.id.main_content);
        ((TabWidget) tabHost.findViewById(android.R.id.tabs)).setDividerDrawable(null);

        LayoutInflater inflater = LayoutInflater.from(this);

        homeTabViewHolder = new ViewHolder(inflater.inflate(R.layout.tab_indicator, null, false));
        homeTabViewHolder.imageView.setImageResource(R.drawable.ic_home);
        homeTabViewHolder.nameView.setText(R.string.title_home);
        TabHost.TabSpec visitationTabSpec = tabHost.newTabSpec(TAB_TAG_HOME);
        visitationTabSpec.setIndicator(homeTabViewHolder.view);
        tabHost.addTab(visitationTabSpec, HomeFragment.class, new Bundle());

        contactTabViewHolder = new ViewHolder(inflater.inflate(R.layout.tab_indicator, null, false));
        contactTabViewHolder.imageView.setImageResource(R.drawable.ic_contact);
        contactTabViewHolder.nameView.setText(R.string.title_contact);
        TabHost.TabSpec contactTabSpec = tabHost.newTabSpec(TAB_TAG_CONTACT);
        contactTabSpec.setIndicator(contactTabViewHolder.view);
        tabHost.addTab(contactTabSpec, FragmentContact.class, new Bundle());

        qrCodeTabViewHolder = new ViewHolderQRCode(inflater.inflate(R.layout.tab_qr_code, null, false));
        qrCodeTabViewHolder.imageView.setImageResource(R.drawable.ic_scan_home);
        TabHost.TabSpec qrCode = tabHost.newTabSpec(TAB_TAG_QR_CODE);
        qrCode.setIndicator(qrCodeTabViewHolder.view);
        tabHost.addTab(qrCode, FragmentQRCodeTab.class, new Bundle());

        historyTabViewHolder = new ViewHolder(inflater.inflate(R.layout.tab_indicator, null, false));
        historyTabViewHolder.imageView.setImageResource(R.drawable.ic_history);
        historyTabViewHolder.nameView.setText(R.string.title_history);
        TabHost.TabSpec historyTabSpec = tabHost.newTabSpec(TAB_TAG_HISTORY);
        historyTabSpec.setIndicator(historyTabViewHolder.view);
        tabHost.addTab(historyTabSpec, FragmentTransactionHistory.class, new Bundle());

        walletTabViewHolder = new ViewHolder(inflater.inflate(R.layout.tab_indicator, null, false));
        walletTabViewHolder.imageView.setImageResource(R.drawable.ic_wallet);
        walletTabViewHolder.nameView.setText(R.string.title_wallet);
        TabHost.TabSpec walletTabSpec = tabHost.newTabSpec(TAB_TAG_MY_WALLET);
        walletTabSpec.setIndicator(walletTabViewHolder.view);
        tabHost.addTab(walletTabSpec, FragmentWallet.class, new Bundle());

        tabHost.setCurrentTabByTag(currentTabTag != null ? currentTabTag : TAB_TAG_HOME);
        onChange(currentTabTag);
    }

    public void onChange(String tabId) {
        switch (tabId) {
            case TAB_TAG_HOME:
                homeTabViewHolder.imageView.setImageResource(R.drawable.ic_home_active);
                contactTabViewHolder.imageView.setImageResource(R.drawable.ic_contact);
                historyTabViewHolder.imageView.setImageResource(R.drawable.ic_history);
                walletTabViewHolder.imageView.setImageResource(R.drawable.ic_wallet);
                qrCodeTabViewHolder.imageView.setImageResource(R.drawable.ic_scan_home);
                //text color
                homeTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                contactTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                historyTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                walletTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                break;
            case TAB_TAG_CONTACT:
                homeTabViewHolder.imageView.setImageResource(R.drawable.ic_home);
                contactTabViewHolder.imageView.setImageResource(R.drawable.ic_contact_active);
                historyTabViewHolder.imageView.setImageResource(R.drawable.ic_history);
                walletTabViewHolder.imageView.setImageResource(R.drawable.ic_wallet);
                qrCodeTabViewHolder.imageView.setImageResource(R.drawable.ic_scan_home);
                //text color
                homeTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                contactTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                historyTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                walletTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                break;
            case TAB_TAG_QR_CODE:
                homeTabViewHolder.imageView.setImageResource(R.drawable.ic_home);
                contactTabViewHolder.imageView.setImageResource(R.drawable.ic_contact);
                historyTabViewHolder.imageView.setImageResource(R.drawable.ic_history);
                walletTabViewHolder.imageView.setImageResource(R.drawable.ic_wallet);
                qrCodeTabViewHolder.imageView.setImageResource(R.drawable.ic_scan_home);
                //text color
                homeTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                contactTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                historyTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                walletTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                break;
            case TAB_TAG_HISTORY:
                homeTabViewHolder.imageView.setImageResource(R.drawable.ic_home);
                contactTabViewHolder.imageView.setImageResource(R.drawable.ic_contact);
                historyTabViewHolder.imageView.setImageResource(R.drawable.ic_history_active);
                walletTabViewHolder.imageView.setImageResource(R.drawable.ic_wallet);
                qrCodeTabViewHolder.imageView.setImageResource(R.drawable.ic_scan_home);
                //text color
                homeTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                contactTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                historyTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                walletTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                break;
            case TAB_TAG_MY_WALLET:
                homeTabViewHolder.imageView.setImageResource(R.drawable.ic_home);
                contactTabViewHolder.imageView.setImageResource(R.drawable.ic_contact);
                historyTabViewHolder.imageView.setImageResource(R.drawable.ic_history);
                walletTabViewHolder.imageView.setImageResource(R.drawable.ic_wallet_active);
                qrCodeTabViewHolder.imageView.setImageResource(R.drawable.ic_scan_home);
                //text color
                homeTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                contactTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                historyTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.gray));
                walletTabViewHolder.nameView.setTextColor(ContextCompat.getColor(this, R.color.blue));
                break;
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.main_content);
    }

    @Override
    public void onBackPressed() {
        if (tabHost.getCurrentTab() == 0) {
            DialogUtil.getInstance().showDialogConfirm(this, R.string.str_confirm_out_app, new DialogUtil.OnConfirm() {
                @Override
                public void OnListenerOk() {
                    finish();
                }

                @Override
                public void OnListenerCancel() {

                }
            });
        } else onSelectedChangeTab(TAB_TAG_HOME);
    }

    public void onSelectedChangeTab(String tabSelected) {
        tabHost.setCurrentTabByTag(tabSelected);
    }


    @Override
    protected void onDestroy() {
        Intent webSocketService = new Intent(MainActivity.this, WebSocketsService.class);
        stopService(webSocketService);
        super.onDestroy();
    }
}
