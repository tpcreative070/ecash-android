package vn.ecpay.ewallet.ui.payto;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.cashIn.adapter.CashValueAdapter;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.fragment.FragmentContactTransferCash;
import vn.ecpay.ewallet.ui.lixi.MyLixiActivity;

public class PayToFragment extends ECashBaseFragment {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.iv_qr_code)
    ImageView ivQRCode;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_payment)
    TextView tvTotalPayment;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private long balance;
    private long totalMoney;
    private AccountInfo accountInfo;
    private List<CashTotal> valuesListAdapter;
    private CashValueAdapter cashValueAdapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_payto;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        setData();
    }
    private void setData(){
        setAdapter();
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
    }
    private void setAdapter() {
        valuesListAdapter = DatabaseUtil.getAllCashValues(getActivity());
        Collections.reverse(valuesListAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvCashValues.setLayoutManager(mLayoutManager);
        cashValueAdapter = new CashValueAdapter(valuesListAdapter, getActivity(), this::updateTotalMoney);
        rvCashValues.setAdapter(cashValueAdapter);
    }
    private void updateTotalMoney() {
        totalMoney = 0;
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.get(i).getParValue());
        }
        tvTotalPayment.setText(CommonUtils.formatPriceVND(totalMoney));
    }
    @Override
    public void onResume() {
        ((PayToActivity) getActivity()).updateTitle(String.format(getString(R.string.str_payment_request)," "));
        super.onResume();
    }
    @OnClick({R.id.iv_qr_code, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_qr_code:
                gotoScanQRCode();
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }
    private void gotoScanQRCode(){
        Intent intentCashIn = new Intent(getActivity(), QRCodeActivity.class);
        startActivity(intentCashIn);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    private void validateData(){// TODO
        showDialogNewPayment("150000","1213244");
    }
}
