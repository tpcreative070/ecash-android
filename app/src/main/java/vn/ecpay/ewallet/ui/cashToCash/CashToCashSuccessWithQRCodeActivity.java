package vn.ecpay.ewallet.ui.cashToCash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.billingQRCode.BillingQRCodeFragment;
import vn.ecpay.ewallet.ui.cashToCash.fragment.CashToCashSuccessWithQRCodeFragment;

public class CashToCashSuccessWithQRCodeActivity extends ECashBaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_layout)
    FrameLayout fragmentLayout;
    private List<CashTotal> valuesListAdapter;
    private List<Contact> multiTransferList;
    private ArrayList<Bitmap> listBitmap;
    private String content;
    private String type;
    @Override
    protected int getLayoutResId() {
        return R.layout.cash_base_activity;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.getMenu().clear();
            ivBack.setOnClickListener(v -> onBackPressed());
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        getIntentData();

    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.fragment_layout);
    }

    public void updateTitle(String title) {
        toolbarCenterText.setText(title);
    }
    private void getIntentData(){
        Intent intent = getIntent();
        if(intent!=null){
            valuesListAdapter = (List<CashTotal>) intent.getSerializableExtra(Constant.CASH_TOTAL_TRANSFER);
            multiTransferList = (List<Contact>) intent.getSerializableExtra(Constant.CONTACT_MULTI_TRANSFER);
            content = intent.getStringExtra(Constant.CONTENT_TRANSFER);
            type = intent.getStringExtra(Constant.TYPE_TRANSFER);
          // listBitmap = (ArrayList<Bitmap>) intent.getSerializableExtra(Constant.URI_TRANSFER);
            if(valuesListAdapter!=null&&multiTransferList!=null){
                addFragment(CashToCashSuccessWithQRCodeFragment.newInstance(valuesListAdapter,multiTransferList,content,type), false);
            }
        }
    }
}

