package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs;
import vn.ecpay.ewallet.database.table.TransactionLog;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.TransactionHistoryAdapter;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.RecyclerItemTouchHelper;

public class FragmentTransactionHistory extends ECashBaseFragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    private TransactionHistoryAdapter mAdapter;
    private List<TransactionsHistoryModel> mSectionList;
    private TransactionsHistoryModel transactionsHistoryModel;
    private TransactionHistoryListener transactionHistoryListener;

    @Override
    protected int getLayoutResId() { return R.layout.fragment_transactions;}

    public static FragmentTransactionHistory newInstance(TransactionHistoryListener transactionHistoryListener) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.TRANSACTIONS_HISTORY, transactionHistoryListener);
        FragmentTransactionHistory fragment = new FragmentTransactionHistory();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.transactionHistoryListener = (TransactionHistoryListener) bundle.getSerializable(Constant.TRANSACTIONS_HISTORY);
        }
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);

        List<CashLogs> listCash = WalletDatabase.getAllCash();
        List<TransactionLog>  list = WalletDatabase.getAllTransactionLog();
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
        mAdapter = new TransactionHistoryAdapter(mSectionList, getActivity());
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);
    }


    private void getHeaderListLatter(List<TransactionsHistoryModel> usersList) {

        Collections.sort(usersList, new Comparator<TransactionsHistoryModel>() {
            @Override
            public int compare(TransactionsHistoryModel user1, TransactionsHistoryModel user2) {
                return String.valueOf(user1.getTransactionDate().charAt(0)).toUpperCase()
                        .compareTo(String.valueOf(user2.getTransactionDate().charAt(0)).toUpperCase());
            }
        });

        String lastHeader = "";

        int size = usersList.size();

        for (int i = 0; i < size; i++) {
            String strMonth="", strYear="";
            TransactionsHistoryModel transactionHistory = usersList.get(i);
            String strTransactionDateTime = transactionHistory.getTransactionDate();
            strYear = strTransactionDateTime.substring(0, 4);
            strMonth = strTransactionDateTime.substring(4, 6);

            //String header = String.valueOf(transactionHistory.getTransactionDate().charAt(0)).toUpperCase();
            String header = "Tháng " + strMonth + "/" + strYear;

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
        //((CashToCashActivity) getActivity()).updateTitle(getString(R.string.str_account_receive));
    }

    @OnClick(R.id.iv_clear_search)
    public void onViewClicked() {
        edtSearch.setText(Constant.STR_EMPTY);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof TransactionHistoryAdapter.ItemViewHolder) {
            transactionsHistoryModel = mSectionList.get(position);
            transactionHistoryListener.itemClick(transactionsHistoryModel);
            ((CashToCashActivity) getActivity()).onBackPressed();
        }
    }
}
