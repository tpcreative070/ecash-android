package vn.ecpay.ewallet.ui.lixi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.fragment.FragmentContactTransferCash;
import vn.ecpay.ewallet.ui.function.CashOutFunction;
import vn.ecpay.ewallet.ui.interfaceListener.MultiTransferListener;
import vn.ecpay.ewallet.ui.lixi.adapter.CashTotalAdapter;

import static vn.ecpay.ewallet.common.utils.Constant.TYPE_LIXI;

public class SendLixiFragment extends ECashBaseFragment implements MultiTransferListener {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverECash;
    @BindView(R.id.tv_number_wallet)
    TextView tvNumberWallet;
    @BindView(R.id.sw_qr_code)
    Switch swQrCode;
    @BindView(R.id.layout_qr_code)
    RelativeLayout layoutQrCode;
    @BindView(R.id.rv_cash_values)
    RecyclerView rvCashValues;
    @BindView(R.id.tv_total_send)
    TextView tvTotalSend;
    @BindView(R.id.edt_content)
    EditText edtContent;
    private AccountInfo accountInfo;
    private int balance;
    private List<CashTotal> valuesListAdapter;
    private CashTotalAdapter cashValueAdapter;
    private List<Contact> multiTransferList;
    private long totalMoney;

    @Override
    protected int getLayoutResId() {
        return R.layout.send_lixi_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        setData();
    }

    private void setData() {
        setAdapter();
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverECash.setText(CommonUtils.formatPriceVND(balance));
    }

    private void setAdapter() {
        valuesListAdapter = DatabaseUtil.getAllCashTotal(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvCashValues.setLayoutManager(mLayoutManager);
        cashValueAdapter = new CashTotalAdapter(valuesListAdapter, getActivity(), this::updateTotalMoney);
        rvCashValues.setAdapter(cashValueAdapter);
    }

    private void updateTotalMoney() {
        totalMoney = 0;
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            totalMoney = totalMoney + (valuesListAdapter.get(i).getTotal() * valuesListAdapter.get(i).getParValue());
        }
        tvTotalSend.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    @OnClick({R.id.layout_chose_wallet, R.id.btn_confirm, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_chose_wallet:
                if (getActivity() != null)
                    ((MyLixiActivity) getActivity()).addFragment(FragmentContactTransferCash.newInstance(this), true);
                break;
            case R.id.btn_confirm:
                validateData();
                break;
            case R.id.iv_back:
                if (getActivity() != null)
                    ((MyLixiActivity) getActivity()).onBackPressed();
                break;
        }
    }

    private void validateData() {
        if (totalMoney == 0) {
            if (getActivity() != null)
                ((MyLixiActivity) getActivity()).showDialogError(getString(R.string.err_dit_not_money_transfer));
            return;
        }

        if (multiTransferList != null) {
            if (multiTransferList.size() == 0) {
                if (getActivity() != null)
                    ((MyLixiActivity) getActivity()).showDialogError(getString(R.string.err_not_input_number_username));
                return;
            }
        } else {
            if (getActivity() != null)
                ((MyLixiActivity) getActivity()).showDialogError(getString(R.string.err_not_input_number_username));
            return;
        }
        if (!checkTotalMoneySend()) {
            if (getActivity() != null)
                ((MyLixiActivity) getActivity()).showDialogError(getString(R.string.err_amount_not_enough_to_transfer));
            return;
        }

        if (edtContent.getText().toString().isEmpty()) {
            if (getActivity() != null)
                ((MyLixiActivity) getActivity()).showDialogError(getString(R.string.err_dit_not_content));
            return;
        }

        showProgress();
        if (swQrCode.isChecked()) {
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                if (!CommonUtils.isExternalStorageWritable()) {
                    dismissProgress();
                    if (getActivity() != null)
                        ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_store_image));
                    return;
                }
                CashOutFunction cashOutSocketFunction = new CashOutFunction(getActivity(), valuesListAdapter,
                        multiTransferList, edtContent.getText().toString(), TYPE_LIXI);
                cashOutSocketFunction.handleCashOutQRCode();
            }
        } else {
            CashOutFunction cashOutSocketFunction = new CashOutFunction(getActivity(), valuesListAdapter,
                    multiTransferList, edtContent.getText().toString(), TYPE_LIXI);
            cashOutSocketFunction.handleCashOutSocket();
        }
    }

    private boolean checkTotalMoneySend() {
        for (int i = 0; i < valuesListAdapter.size(); i++) {
            if (valuesListAdapter.get(i).getTotal() > 0) {
                int slc = valuesListAdapter.get(i).getTotal() * (multiTransferList.size());
                if (slc > valuesListAdapter.get(i).getTotal() + valuesListAdapter.get(i).getTotalDatabase()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgress();
                    CashOutFunction cashOutSocketFunction = new CashOutFunction(getActivity(), valuesListAdapter,
                            multiTransferList, edtContent.getText().toString(), TYPE_LIXI);
                    cashOutSocketFunction.handleCashOutQRCode();
                }
            }
            default:
                break;
        }
    }

    @Override
    public void onMultiTransfer(ArrayList<Contact> contactList) {
        this.multiTransferList = contactList;
        updateWalletSend();
    }

    private void updateWalletSend() {
        StringBuilder walletId = new StringBuilder();
        for (int i = 0; i < multiTransferList.size(); i++) {
            if (i == 0) {
                walletId.append(multiTransferList.get(i).getWalletId());
            } else {
                walletId.append("; ").append(multiTransferList.get(i).getWalletId());
            }
        }
        tvNumberWallet.setText(walletId.toString());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)) {
            dismissProgress();
            showDialogSendOk();
            EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_ACCOUNT_LOGIN));
        }

        if (event.getData().equals(Constant.CASH_OUT_MONEY_FAIL)) {
            dismissProgress();
            Toast.makeText(getActivity(),getResources().getString(R.string.err_upload), Toast.LENGTH_LONG).show();
        }

        if (event.getData().equals(Constant.UPDATE_MONEY) ||
                event.getData().equals(Constant.UPDATE_MONEY_SOCKET)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            setData();
                        });
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, 500);
        }

        if (event.getData().equals(Constant.EVENT_CONNECT_SOCKET_FAIL)) {
            dismissProgress();
            ((CashToCashActivity) getActivity()).showDialogError(getString(R.string.err_connect_socket_fail));
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void showDialogSendOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                "chuyển tiền thành công", new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        setData();
                        updateTotalMoney();
                    }

                    @Override
                    public void OnListenerCancel() {
                        getActivity().finish();
                    }
                });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}
