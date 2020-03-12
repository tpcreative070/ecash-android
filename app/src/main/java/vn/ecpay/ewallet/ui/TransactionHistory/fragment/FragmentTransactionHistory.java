package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
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
import vn.ecpay.ewallet.common.utils.bottomSheet.TransactionHistoryBottomSheet;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.TransactionHistory.TransactionsHistoryDetailActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.TransactionHistoryAdapter;

public class FragmentTransactionHistory extends ECashBaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    private TransactionHistoryAdapter mAdapter;
    private List<TransactionsHistoryModel> mSectionList;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_transactions;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<TransactionsHistoryModel> transactionsHistoryModelList = WalletDatabase.getListTransactionHistory();
        setAdapter(transactionsHistoryModelList);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapterTextChange(s.toString());
                } else {
                    setAdapter(transactionsHistoryModelList);
                }
            }
        });
    }

    private void setAdapterTextChange(String filter) {
        List<TransactionsHistoryModel> transactionsHistoryModelList = WalletDatabase.getListTransactionHistoryFilter(CommonUtils.getParamFilter(filter));
        setAdapter(transactionsHistoryModelList);
    }

    private void setAdapter(List<TransactionsHistoryModel> transactionsHistoryModelList) {
        mSectionList = new ArrayList<>();
        getHeaderListLatter(transactionsHistoryModelList);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TransactionHistoryAdapter(mSectionList, getActivity(), transactionsHistoryModel -> {
            Intent intent = new Intent(getActivity(), TransactionsHistoryDetailActivity.class);
            intent.putExtra(Constant.TRANSACTIONS_HISTORY_MODEL, transactionsHistoryModel);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void getHeaderListLatter(List<TransactionsHistoryModel> usersList) {
        Collections.sort(usersList, (user1, user2) -> String.valueOf(user1.getTransactionDate().charAt(0)).toUpperCase()
                .compareTo(String.valueOf(user2.getTransactionDate().charAt(0)).toUpperCase()));

        String lastHeader = "";

        int size = usersList.size();

        for (int i = 0; i < size; i++) {
            String strMonth = Constant.STR_EMPTY, strYear = Constant.STR_EMPTY;
            TransactionsHistoryModel transactionHistory = usersList.get(i);
            String strTransactionDateTime = transactionHistory.getTransactionDate();
            try {
                strYear = strTransactionDateTime.substring(0, 4);
                strMonth = strTransactionDateTime.substring(4, 6);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            String header = getString(R.string.str_history_date_header, strMonth, strYear);
            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                mSectionList.add(new TransactionsHistoryModel(true, header));
            }
            mSectionList.add(transactionHistory);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.EVENT_CASH_IN_SUCCESS)
                || event.getData().equals(Constant.EVENT_UPDATE_BALANCE)
                || event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)) {
            reloadData();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void reloadData() {
        long numberCash = WalletDatabase.getAllCash().size();
        if (WalletDatabase.numberRequest == 0 && numberCash > 0) {
            List<TransactionsHistoryModel> transactionsHistoryModelList = WalletDatabase.getListTransactionHistory();
            setAdapter(transactionsHistoryModelList);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> reloadData());
                }
            }, 1000);
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @OnClick({R.id.iv_filter})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_filter) {
            TransactionHistoryBottomSheet cashChange = new TransactionHistoryBottomSheet((date, type, status) -> {
                List<TransactionsHistoryModel> transactionsHistoryModelList =
                        WalletDatabase.getAllTransactionsHistoryFilter(date, type, status);
                setAdapter(transactionsHistoryModelList);
            });
            cashChange.show(getChildFragmentManager(), "historyFilter");
        }
    }


}
