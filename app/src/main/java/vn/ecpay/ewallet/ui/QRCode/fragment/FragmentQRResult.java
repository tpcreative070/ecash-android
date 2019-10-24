package vn.ecpay.ewallet.ui.QRCode.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.QRCode.adapter.AdapterMoneyTransfer;

public class FragmentQRResult extends ECashBaseFragment {
    @BindView(R.id.tv_total_money_transfer)
    TextView tvTotalMoneyTransfer;
    @BindView(R.id.tv_human_transfer)
    TextView tvHumanTransfer;
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.rv_list_cash_transfer)
    RecyclerView rvListCashTransfer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ArrayList<QRCashTransfer> qrCashTransfers;
    private AdapterMoneyTransfer adapterMoneyTransfer;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_scan_qr_result;
    }

    public static FragmentQRResult newInstance(ArrayList<QRCashTransfer> qrCashTransfers) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constant.LIST_CASH_RESULT, qrCashTransfers);
        FragmentQRResult fragment = new FragmentQRResult();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.qrCashTransfers = bundle.getParcelableArrayList(Constant.LIST_CASH_RESULT);
        }

        if (qrCashTransfers != null) {
            long money = Long.valueOf(qrCashTransfers.get(0).getTotalMoney());
            tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_total_money_transfer, CommonUtils.formatPriceVND(money)));
            tvHumanTransfer.setText(qrCashTransfers.get(0).getName());
            tvPhoneNumber.setText(qrCashTransfers.get(0).getPhone());
            tvContent.setText(qrCashTransfers.get(0).getContent());

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rvListCashTransfer.setLayoutManager(mLayoutManager);
            adapterMoneyTransfer = new AdapterMoneyTransfer(qrCashTransfers, getActivity());
            rvListCashTransfer.setAdapter(adapterMoneyTransfer);
        }
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        ((QRCodeActivity)getActivity()).onBackPressed();
    }
}
