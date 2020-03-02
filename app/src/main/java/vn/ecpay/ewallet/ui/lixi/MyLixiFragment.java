package vn.ecpay.ewallet.ui.lixi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.ui.QRCode.QRCodeActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.AdapterCashLogTransactionsHistory;
import vn.ecpay.ewallet.ui.function.CashInFunction;
import vn.ecpay.ewallet.ui.interfaceListener.CashInSuccessListener;
import vn.ecpay.ewallet.ui.lixi.adapter.MyLixiAdapter;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class MyLixiFragment extends ECashBaseFragment {
    @BindView(R.id.rv_my_lixi)
    RecyclerView rvMyLixi;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;

    private List<CashTemp> listCashTemp;
    private MyLixiAdapter myLixiAdapter;
    private AccountInfo accountInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.my_lixi_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        setAdapter();
        toolbarCenterText.setText(getResources().getString(R.string.str_my_lixi));
    }

    private void setAdapter() {
        listCashTemp = DatabaseUtil.getAllCashTemp(getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvMyLixi.setLayoutManager(mLayoutManager);
        myLixiAdapter = new MyLixiAdapter(listCashTemp, getActivity(), cashTemp -> {
            showProgress();
            ResponseMessSocket responseMess = new Gson().fromJson(cashTemp.getContent(), ResponseMessSocket.class);
            if (null != responseMess) {
                if (!DatabaseUtil.isTransactionLogExit(responseMess, getActivity())) {
                    if (responseMess.getCashEnc() != null) {
                        CashInFunction cashInFunction = new CashInFunction(accountInfo, getActivity(), responseMess);
                        cashInFunction.handleCashIn(new CashInSuccessListener() {
                            @Override
                            public void onCashInSuccess() {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        if (getActivity() == null) return;
                                        getActivity().runOnUiThread(() -> {
                                            dismissProgress();
                                            DatabaseUtil.updateStatusLixi(getActivity(), Constant.OPEN, cashTemp.getId());
                                            showDialogLixiDetail(cashTemp, responseMess.getContent());
                                        });
                                    }
                                }, 1000);
                            }

                            @Override
                            public void onCashInFail() {
                                dismissProgress();
                                showDialogError(getResources().getString(R.string.err_upload));
                            }
                        });
                    }
                } else {
                    dismissProgress();
                    showDialogLixiDetail(cashTemp, responseMess.getContent());
                }
            }
        });
        rvMyLixi.setAdapter(myLixiAdapter);
    }

    private void showDialogLixiDetail(CashTemp cashTemp, String content) {
        Dialog mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.dialog_lixi_detail);

        Button btnConfirm;
        RecyclerView rv_cash_take;
        btnConfirm = mDialog.findViewById(R.id.btn_confirm);
        TextView tv_total_money_take = mDialog.findViewById(R.id.tv_total_money_take);
        TextView tvContent = mDialog.findViewById(R.id.tv_content);
        rv_cash_take = mDialog.findViewById(R.id.rv_cash_take);

        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<CashLogTransaction> listCashLogTransaction = WalletDatabase.getAllCashByTransactionLog(cashTemp.getTransactionSignature());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_cash_take.setLayoutManager(mLayoutManager);
        rv_cash_take.setAdapter(new AdapterCashLogTransactionsHistory(listCashLogTransaction, getActivity()));

        tv_total_money_take.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoneyByCashLog(listCashLogTransaction)));
        tvContent.setText(content);

        btnConfirm.setOnClickListener(v -> {
            EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_LIXI));
            mDialog.dismiss();
        });

        mDialog.show();
    }

    @OnClick({R.id.iv_back, R.id.iv_send_lixi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (getActivity() != null)
                    ((MyLixiActivity) getActivity()).onBackPressed();
                break;
            case R.id.iv_send_lixi:
                if (getActivity() != null)
                    ((MyLixiActivity) getActivity()).addFragment(new SendLixiFragment(), true);
                break;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.EVENT_UPDATE_LIXI)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> setAdapter());
                }
            }, 500);
        }
        if (event.getData().equals(Constant.EVENT_VERIFY_CASH_FAIL)) {
            dismissProgress();
            if (getActivity() != null)
                ((QRCodeActivity) getActivity()).showDialogError(getResources().getString(R.string.err_upload));
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }
}
