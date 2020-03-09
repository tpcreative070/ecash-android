package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.billingQRCode.BillingQRCodeFragment;
import vn.ecpay.ewallet.ui.cashIn.CashInActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashSuccessWithQRCodeActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.SlideQRCodeAdapter;

public class CashToCashSuccessWithQRCodeFragment extends ECashBaseFragment {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.view_pager)
    ViewPager view_pager;

    private List<CashTotal> valuesListAdapter;
    private List<Contact> multiTransferList;
    private String content;
    private String type;
    private SlideQRCodeAdapter adapter;

    public static CashToCashSuccessWithQRCodeFragment newInstance(List<CashTotal> valuesListAdapter, List<Contact> multiTransferList, String content, String type) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.CASH_TOTAL_TRANSFER, (Serializable) valuesListAdapter);
        args.putSerializable(Constant.CONTACT_MULTI_TRANSFER, (Serializable) multiTransferList);
        args.putSerializable(Constant.CONTENT_TRANSFER, content);
        args.putSerializable(Constant.TYPE_TRANSFER, type);
        CashToCashSuccessWithQRCodeFragment fragment = new CashToCashSuccessWithQRCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_to_cash_success_qr_code;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            valuesListAdapter = (List<CashTotal>) bundle.getSerializable(Constant.CASH_TOTAL_TRANSFER);
            multiTransferList = (List<Contact>) bundle.getSerializable(Constant.CONTACT_MULTI_TRANSFER);
            content = bundle.getString(Constant.CONTENT_TRANSFER);
            type = bundle.getString(Constant.TYPE_TRANSFER);
            setData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CashToCashSuccessWithQRCodeActivity) getActivity()).updateTitle(getString(R.string.str_transfer));
    }

    private void setData() {
        if (valuesListAdapter != null && multiTransferList != null) {
            tv_title.setText(String.format(getString(R.string.str_you_have_successfully_qr_press_arrow_to_view_other_qr), multiTransferList.size() + ""));
            adapter = new SlideQRCodeAdapter(getActivity(), valuesListAdapter, multiTransferList, content, type);
            view_pager.setAdapter(adapter);
        }
    }
}
