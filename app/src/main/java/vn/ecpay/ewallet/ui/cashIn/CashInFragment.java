package vn.ecpay.ewallet.ui.cashIn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

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
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cash.edongToEcash.EDongToECash;
import vn.ecpay.ewallet.ui.cashIn.module.CashInModule;
import vn.ecpay.ewallet.ui.cashIn.presenter.CashInPresenter;
import vn.ecpay.ewallet.ui.cashIn.view.CashInView;

public class CashInFragment extends ECashBaseFragment implements CashInView {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.iv_drop_down)
    ImageView ivDropDown;
    @BindView(R.id.tv_eDong_wallet)
    TextView tvEdongWallet;
    @BindView(R.id.tv_over_edong)
    TextView tvOverEdong;
    @BindView(R.id.tv_500)
    TextView tv500;
    @BindView(R.id.iv_down_500)
    ImageView ivDown500;
    @BindView(R.id.tv_total_500)
    TextView tvTotal500;
    @BindView(R.id.iv_up_500)
    ImageView ivUp500;
    @BindView(R.id.tv_200)
    TextView tv200;
    @BindView(R.id.iv_down_200)
    ImageView ivDown200;
    @BindView(R.id.tv_total_200)
    TextView tvTotal200;
    @BindView(R.id.iv_up_200)
    ImageView ivUp200;
    @BindView(R.id.tv_100)
    TextView tv100;
    @BindView(R.id.iv_down_100)
    ImageView ivDown100;
    @BindView(R.id.tv_total_100)
    TextView tvTotal100;
    @BindView(R.id.iv_up_100)
    ImageView ivUp100;
    @BindView(R.id.tv_50)
    TextView tv50;
    @BindView(R.id.iv_down_50)
    ImageView ivDown50;
    @BindView(R.id.tv_total_50)
    TextView tvTotal50;
    @BindView(R.id.iv_up_50)
    ImageView ivUp50;
    @BindView(R.id.tv_20)
    TextView tv20;
    @BindView(R.id.iv_down_20)
    ImageView ivDown20;
    @BindView(R.id.tv_total_20)
    TextView tvTotal20;
    @BindView(R.id.iv_up_20)
    ImageView ivUp20;
    @BindView(R.id.tv_10)
    TextView tv10;
    @BindView(R.id.iv_down_10)
    ImageView ivDown10;
    @BindView(R.id.tv_total_10)
    TextView tvTotal10;
    @BindView(R.id.iv_up_10)
    ImageView ivUp10;
    @BindView(R.id.tv_total_cash_in)
    TextView tvTotalCashIn;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.layout_eDong)
    RelativeLayout layoutEDong;
    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;
    private long balance;
    private int total500 = 0, total200 = 0, total100 = 0, total50 = 0, total20 = 0, total10 = 0;
    private long totalMoney;
    private EdongInfo eDongInfoCashIn;
    private List<Integer> listQuality;
    private List<Integer> listValue;

    @Inject
    CashInPresenter cashInPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_in;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new CashInModule(this)).inject(this);
        cashInPresenter.setView(this);
        cashInPresenter.onViewCreate();
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        listEDongInfo = ECashApplication.getListEDongInfo();
        setData();
        updateQualityIn();
    }

    private void setData() {
        tvAccountName.setText(CommonUtils.getFullName(accountInfo));
        tvId.setText(String.valueOf(accountInfo.getWalletId()));
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));

        if (listEDongInfo.size() > 0) {
            eDongInfoCashIn = listEDongInfo.get(0);
            tvEdongWallet.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvOverEdong.setText(CommonUtils.formatPriceVND(listEDongInfo.get(0).getAccBalance()));
        }
    }

    private void updateBalance() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvOverEcash.setText(CommonUtils.formatPriceVND(balance));
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (listEDongInfo.size() > 0) {
            for (int i = 0; i < listEDongInfo.size(); i++) {
                if (listEDongInfo.get(i).getAccountIdt().equals(eDongInfoCashIn.getAccountIdt())) {
                    tvOverEdong.setText(CommonUtils.formatPriceVND(listEDongInfo.get(i).getAccBalance()));
                    tvEdongWallet.setText(String.valueOf(listEDongInfo.get(i).getAccountIdt()));
                }
            }
        } else {
            tvEdongWallet.setText(String.valueOf(listEDongInfo.get(0).getAccountIdt()));
            tvOverEdong.setText(CommonUtils.formatPriceVND(listEDongInfo.get(0).getAccBalance()));
        }
    }

    private void updateQualityIn() {
        tvTotal500.setText(String.valueOf(total500));
        tvTotal200.setText(String.valueOf(total200));
        tvTotal100.setText(String.valueOf(total100));
        tvTotal50.setText(String.valueOf(total50));
        tvTotal20.setText(String.valueOf(total20));
        tvTotal10.setText(String.valueOf(total10));
    }

    private void updateTotalMoneySend() {
        totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
        tvTotalCashIn.setText(CommonUtils.formatPriceVND(totalMoney));
    }

    private void showDialogEDong() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn tài khoản Edong");
        String[] eDong = new String[listEDongInfo.size()];
        for (int i = 0; i < listEDongInfo.size(); i++) {
            eDong[i] = String.valueOf(listEDongInfo.get(i).getAccountIdt());
        }

        builder.setItems(eDong, (dialog, which) -> {
            tvEdongWallet.setText(String.valueOf(listEDongInfo.get(which).getAccountIdt()));
            tvOverEdong.setText(String.valueOf(listEDongInfo.get(which).getAccBalance()));
            eDongInfoCashIn = listEDongInfo.get(which);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onResume() {
        ((CashInActivity) getActivity()).updateTitle(getString(R.string.str_cash_in));
        super.onResume();
    }

    private void updateAllData() {
        updateQualityIn();
        updateTotalMoneySend();
    }

    @OnClick({R.id.layout_eDong, R.id.iv_down_500, R.id.iv_up_500, R.id.iv_down_200, R.id.iv_up_200, R.id.iv_down_100, R.id.iv_up_100, R.id.iv_down_50, R.id.iv_up_50, R.id.iv_down_20, R.id.iv_up_20, R.id.iv_down_10, R.id.iv_up_10, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_down_500:
                if (total500 > 0) {
                    total500 = total500 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_500:
                total500 = total500 + 1;
                updateAllData();
                break;
            case R.id.iv_down_200:
                if (total200 > 0) {
                    total200 = total200 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_200:
                total200 = total200 + 1;
                updateAllData();
                break;
            case R.id.iv_down_100:
                if (total100 > 0) {
                    total100 = total100 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_100:
                total100 = total100 + 1;
                updateAllData();
                break;
            case R.id.iv_down_50:
                if (total50 > 0) {
                    total50 = total50 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_50:
                total50 = total50 + 1;
                updateAllData();
                break;
            case R.id.iv_down_20:
                if (total20 > 0) {
                    total20 = total20 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_20:
                total20 = total20 + 1;
                updateAllData();
                break;
            case R.id.iv_down_10:
                if (total10 > 0) {
                    total10 = total10 - 1;
                }
                updateAllData();
                break;
            case R.id.iv_up_10:
                total10 = total10 + 1;
                updateAllData();
                break;
            case R.id.layout_eDong:
                if (listEDongInfo.size() > 1) {
                    showDialogEDong();
                }
                break;
            case R.id.btn_confirm:
                validateData();
                break;
        }
    }

    private void validateData() {
        if (totalMoney == 0) {
            ((CashInActivity) getActivity()).showDialogError("Bạn chưa chọn số tiền muốn nạp");
            return;
        }
        listQuality = new ArrayList<>();
        listValue = new ArrayList<>();
        if (total10 > 0) {
            listQuality.add(total10);
            listValue.add(10000);
        }
        if (total20 > 0) {
            listQuality.add(total20);
            listValue.add(20000);
        }
        if (total50 > 0) {
            listQuality.add(total50);
            listValue.add(50000);
        }
        if (total100 > 0) {
            listQuality.add(total100);
            listValue.add(100000);
        }
        if (total200 > 0) {
            listQuality.add(total200);
            listValue.add(200000);
        }
        if (total500 > 0) {
            listQuality.add(total500);
            listValue.add(500000);
        }
        cashInPresenter.transferMoneyEDongToECash(totalMoney, eDongInfoCashIn, listQuality, accountInfo, listValue);
    }

    private void showDialogCashInOk() {
        DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_transfer_success),
                getString(R.string.str_dialog_cash_in_success, CommonUtils.formatPriceVND(totalMoney), String.valueOf(eDongInfoCashIn.getAccountIdt())), new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        total500 = 0;
                        total200 = 0;
                        total100 = 0;
                        total50 = 0;
                        total20 = 0;
                        total10 = 0;
                        updateBalance();
                        updateQualityIn();
                        updateTotalMoneySend();
                    }

                    @Override
                    public void OnListenerCancel() {
                        Objects.requireNonNull(getActivity()).finish();
                    }
                });
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }

    @Override
    public void showDialogError(String err) {
        ((CashInActivity) Objects.requireNonNull(getActivity())).showDialogError(err);
    }

    @Override
    public void transferMoneySuccess(EDongToECash eDongToECash, String id) {
        cashInPresenter.getEDongInfo(accountInfo);
        startService(eDongToECash, id);
    }

    private void startService(EDongToECash responseData, String transactionSignature) {
        Intent intent = new Intent(getActivity(), CashInService.class);
        intent.putExtra(Constant.EDONG_TO_ECASH, responseData);
        intent.putExtra(Constant.ACCOUNT_INFO, accountInfo);
        intent.putExtra(Constant.TRANSACTION_SIGNATURE, transactionSignature);
        if (getActivity() != null) {
            getActivity().startService(intent);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.UPDATE_MONEY)) {
            dismissProgress();
            showDialogCashInOk();
            Intent intent = new Intent(getActivity(), CashInService.class);
            if (getActivity() != null) {
                getActivity().startService(intent);
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }
}
