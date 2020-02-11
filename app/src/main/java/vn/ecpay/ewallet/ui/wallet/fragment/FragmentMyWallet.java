package vn.ecpay.ewallet.ui.wallet.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.account.AccountActivity;
import vn.ecpay.ewallet.ui.cashOut.CashOutActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.wallet.activity.ChangePassActivity;
import vn.ecpay.ewallet.ui.wallet.module.MyWalletModule;
import vn.ecpay.ewallet.ui.wallet.presenter.MyWalletPresenter;
import vn.ecpay.ewallet.ui.wallet.view.MyWalletView;

public class FragmentMyWallet extends ECashBaseFragment implements MyWalletView {
    @BindView(R.id.iv_account)
    CircleImageView ivAccount;
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.layout_help)
    RelativeLayout layoutHelp;
    @BindView(R.id.layout_cancel_account)
    RelativeLayout layoutCancelAccount;
    @BindView(R.id.layout_logout)
    RelativeLayout layoutLogout;
    private AccountInfo accountInfo;
    private long balance;

    @Inject
    MyWalletPresenter myWalletPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my_wallet;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new MyWalletModule(this)).inject(this);
        myWalletPresenter.setView(this);
        myWalletPresenter.onViewCreate();
        setData();
    }

    private void setData() {
        WalletDatabase.getINSTANCE(getContext(), ECashApplication.masterKey);
        accountInfo = WalletDatabase.getAccountInfoTask(ECashApplication.getAccountInfo().getUsername());
        if (accountInfo != null) {
            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
            balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
            tvPrice.setText(CommonUtils.formatPriceVND(balance));
            tvId.setText(String.valueOf(accountInfo.getWalletId()));
            tvAccountName.setText(CommonUtils.getFullName(accountInfo));
            updateAvatar();
        } else {
            tvId.setText(Constant.STR_EMPTY);
            tvAccountName.setText(Constant.STR_EMPTY);
            tvPrice.setText(Constant.STR_EMPTY);
        }
    }

    private void updateAvatar() {
        if (ECashApplication.getAccountInfo().getLarge() == null) {
            if (getActivity() != null)
                ivAccount.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_avatar));
        } else {
            CommonUtils.loadAvatar(getActivity(), ivAccount, ECashApplication.getAccountInfo().getLarge());
        }
    }

    private void updateLogin() {
        accountInfo = WalletDatabase.getAccountInfoTask(ECashApplication.getAccountInfo().getUsername());
        setData();
    }

    @OnClick({R.id.layout_help, R.id.layout_cancel_account, R.id.layout_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_help:
                break;
            case R.id.layout_cancel_account:
                DialogUtil.getInstance().showDialogConfirm(getActivity(), getString(R.string.str_dialog_notification_title),
                        getString(R.string.err_confirm_cancel_account), new DialogUtil.OnConfirm() {
                            @Override
                            public void OnListenerOk() {
                                myWalletPresenter.validateCancelAccount(balance, accountInfo);
                            }

                            @Override
                            public void OnListenerCancel() {

                            }
                        });
                break;
            case R.id.layout_logout:
                DialogUtil.getInstance().showDialogLogout(getActivity(), () -> myWalletPresenter.logout(accountInfo));
                break;
        }
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)
                || event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)|| event.getData().equals(Constant.EVENT_PAYMEMT_SUCCESS)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> updateBalance());
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, 3000);
        }

        if (event.getData().equals(Constant.EVENT_UPDATE_ACCOUNT_INFO)) {
            setData();
        }
        if (event.getData().equals(Constant.EVENT_UPDATE_AVARTAR)) {
            updateAvatar();
        }

        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            updateLogin();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void updateBalance() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        balance = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        tvPrice.setText(CommonUtils.formatPriceVND(balance));
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
        ((MainActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void onLogoutSuccess() {
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        startActivity(intent);
        if (getActivity() != null)
            getActivity().finish();
    }

    @Override
    public void transferMoneyToCancel() {
        DialogUtil.getInstance().showDialogCancelAccount(getActivity(), balance, new DialogUtil.OnCancelAccount() {
            @Override
            public void OnListenerTransferCash() {
                Intent intentTransferCash = new Intent(getActivity(), CashToCashActivity.class);
                getActivity().startActivity(intentTransferCash);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void OnListenerCashOut() {
                Intent intentCashOut = new Intent(getActivity(), CashOutActivity.class);
                getActivity().startActivity(intentCashOut);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public void onCancelAccountSuccess() {
        DialogUtil.getInstance().showDialogCancelAccountSuccess(getActivity(), Constant.STR_EMPTY, Constant.STR_EMPTY, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
                WalletDatabase.clearAllTable();
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}
