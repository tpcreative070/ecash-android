package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Index;

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
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.TransactionHistory.MonthYearPickerDialog;
import vn.ecpay.ewallet.ui.TransactionHistory.TransactionsHistoryDetailActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.TransactionHistoryAdapter;

public class FragmentTransactionHistory extends ECashBaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.layout_filter)
    RelativeLayout layoutFilter;
    private TransactionHistoryAdapter mAdapter;
    private List<TransactionsHistoryModel> mSectionList;
    private String dateFilter = null,
            typeFilter = null,
            statusFilter = null;

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
        if (event.getData().equals(Constant.UPDATE_MONEY)
                || event.getData().equals(Constant.CASH_OUT_MONEY_SUCCESS)
                || event.getData().equals(Constant.UPDATE_MONEY_SOCKET)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            List<TransactionsHistoryModel> transactionsHistoryModelList = WalletDatabase.getListTransactionHistory();
                            setAdapter(transactionsHistoryModelList);
                        });
                    } catch (NullPointerException e) {
                        return;
                    }
                }
            }, 500);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @OnClick({R.id.layout_date, R.id.layout_type, R.id.layout_status, R.id.btn_apply, R.id.iv_filter, R.id.layout_transparent, R.id.btn_clear, R.id.layout_content_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_transparent:
                if (layoutFilter.getVisibility() == View.VISIBLE) {
                    layoutFilter.setVisibility(View.GONE);
                }
                break;
            case R.id.layout_date:
                showDialogDate();
                break;
            case R.id.layout_type:
                showDialogChoseType();
                break;
            case R.id.layout_status:
                showDialogChoseStatus();
                break;
            case R.id.btn_apply:
                List<TransactionsHistoryModel> transactionsHistoryModelList =
                        WalletDatabase.getAllTransactionsHistoryFilter(dateFilter, typeFilter, statusFilter);
                setAdapter(transactionsHistoryModelList);
                layoutFilter.setVisibility(View.GONE);
                break;
            case R.id.btn_clear:
                dateFilter = null;
                typeFilter = null;
                statusFilter = null;

                tvDate.setText(Constant.STR_EMPTY);
                tvType.setText(Constant.STR_EMPTY);
                tvStatus.setText(Constant.STR_EMPTY);
                break;
            case R.id.iv_filter:
                if (layoutFilter.getVisibility() == View.VISIBLE) {
                    layoutFilter.setVisibility(View.GONE);
                } else {
                    layoutFilter.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.layout_content_filter:
                break;
        }
    }

    private void showDialogDate() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener((view, year, month, dayOfMonth) -> {
            tvDate.setText(getString(R.string.str_history_date_header, String.valueOf(month), String.valueOf(year)));
            dateFilter = String.valueOf(year) + month;
        });
        pd.show(getChildFragmentManager(), "MonthYearPickerDialog");
    }

    private void showDialogChoseType() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.str_cash_in));
        arrayAdapter.add(getResources().getString(R.string.str_cash_out));
        arrayAdapter.add(getResources().getString(R.string.str_transfer));
        arrayAdapter.add(getResources().getString(R.string.str_change_cash));
        arrayAdapter.add(getResources().getString(R.string.str_lixi));
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            typeFilter = getTypeFilter(arrayAdapter.getItem(which));
            tvType.setText(arrayAdapter.getItem(which));
        });
        builderSingle.show();
    }

    private String getTypeFilter(String type) {
        if (type.equals(getResources().getString(R.string.str_cash_in))) {
            return Constant.TYPE_SEND_EDONG_TO_ECASH;
        } else if (type.equals(getResources().getString(R.string.str_cash_out))) {
            return Constant.TYPE_SEND_ECASH_TO_EDONG;
        } else if (type.equals(getResources().getString(R.string.str_transfer))) {
            return Constant.TYPE_ECASH_TO_ECASH;
        } else if (type.equals(getResources().getString(R.string.str_change_cash))) {
            return Constant.TYPE_CASH_EXCHANGE;
        } else if (type.equals(getResources().getString(R.string.str_lixi))) {
            return Constant.TYPE_LIXI;
        } else return Constant.STR_EMPTY;
    }

    private void showDialogChoseStatus() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.str_cash_take_success));
        arrayAdapter.add(getResources().getString(R.string.str_fail));

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            statusFilter = getStatusFilter(arrayAdapter.getItem(which));
            tvStatus.setText(arrayAdapter.getItem(which));
        });
        builderSingle.show();
    }

    private String getStatusFilter(String status) {
        if (status.equals(getResources().getString(R.string.str_cash_take_success))) {
            return "0";
        } else if (status.equals(getResources().getString(R.string.str_fail))) {
            return "1";
        } else return Constant.STR_EMPTY;
    }
}
