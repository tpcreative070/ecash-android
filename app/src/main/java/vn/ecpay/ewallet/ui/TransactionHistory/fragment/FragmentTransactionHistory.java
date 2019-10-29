package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
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
    private String dateFilter, typeFilter, statusFilter;

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
            String strMonth, strYear;
            TransactionsHistoryModel transactionHistory = usersList.get(i);
            String strTransactionDateTime = transactionHistory.getTransactionDate();
            strYear = strTransactionDateTime.substring(0, 4);
            strMonth = strTransactionDateTime.substring(4, 6);
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
        if (event.getData().equals(Constant.UPDATE_MONEY)) {
            List<TransactionsHistoryModel> transactionsHistoryModelList = WalletDatabase.getListTransactionHistory();
            setAdapter(transactionsHistoryModelList);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @OnClick({R.id.layout_date, R.id.layout_type, R.id.layout_status, R.id.btn_apply, R.id.iv_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
//                List<TransactionsHistoryModel> transactionsHistoryModelList =
//                        WalletDatabase.getAllTransactionsHistoryFilter(CommonUtils.getParamFilter(filter));
//                setAdapter(transactionsHistoryModelList);
                break;
            case R.id.iv_filter:
                if (layoutFilter.getVisibility() == View.VISIBLE) {
                    layoutFilter.setVisibility(View.GONE);
                } else {
                    layoutFilter.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void showDialogDate() {
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, year1, month1, dayOfMonth) -> {
            String monthOfYear = String.valueOf(month1 + 1);
            if (month1 + 1 < 10)
                monthOfYear = "0" + (month1 + 1);
            tvDate.setText(getString(R.string.str_history_date_header, monthOfYear, String.valueOf(year1)));
            dateFilter = year1+monthOfYear;
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showDialogChoseType() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle(getResources().getString(R.string.str_type_transfer));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.str_cash_in));
        arrayAdapter.add(getResources().getString(R.string.str_cash_out));
        arrayAdapter.add(getResources().getString(R.string.str_transfer));
        arrayAdapter.add(getResources().getString(R.string.str_change_cash));
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        typeFilter = arrayAdapter.getItem(which);
                tvType.setText(arrayAdapter.getItem(which));
            }
        });
        builderSingle.show();
    }

    private void showDialogChoseStatus() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle(getResources().getString(R.string.str_type_transfer));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getResources().getString(R.string.str_cash_take_success));
        arrayAdapter.add(getResources().getString(R.string.str_fail));

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> tvStatus.setText(arrayAdapter.getItem(which)));
        builderSingle.show();
    }
}
