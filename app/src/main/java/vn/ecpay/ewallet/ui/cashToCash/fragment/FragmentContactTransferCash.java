package vn.ecpay.ewallet.ui.cashToCash.fragment;

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
import vn.ecpay.ewallet.model.contactTransfer.ContactTransferModel;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.ContactTransferAdapter;
import vn.ecpay.ewallet.ui.cashToCash.adapter.RecyclerItemTouchHelper;

public class FragmentContactTransferCash extends ECashBaseFragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    private ContactTransferAdapter mAdapter;
    private List<ContactTransferModel> mSectionList;
    private ContactTransferModel transferModel;
    private ContactTransferListener contactTransferListener;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contact_transfer;
    }

    public static FragmentContactTransferCash newInstance(ContactTransferListener contactTransferListener) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.CONTACT_TRANSFER, contactTransferListener);
        FragmentContactTransferCash fragment = new FragmentContactTransferCash();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.contactTransferListener = (ContactTransferListener) bundle.getSerializable(Constant.CONTACT_TRANSFER);
        }

        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<ContactTransferModel> transferModelArrayList = WalletDatabase.getListContact();

        setAdapter(transferModelArrayList);
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
                    setAdapter(transferModelArrayList);
                }
            }
        });
    }

    private void setAdapterTextChange(String filter) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<ContactTransferModel> transferModelArrayList = WalletDatabase.getListContactFilter(CommonUtils.getParamFilter(filter));
        setAdapter(transferModelArrayList);
    }

    private void setAdapter(List<ContactTransferModel> transferModelArrayList) {
        mSectionList = new ArrayList<>();
        getHeaderListLatter(transferModelArrayList);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactTransferAdapter(mSectionList, getActivity());
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

    private void getHeaderListLatter(List<ContactTransferModel> usersList) {

        Collections.sort(usersList, new Comparator<ContactTransferModel>() {
            @Override
            public int compare(ContactTransferModel user1, ContactTransferModel user2) {
                return String.valueOf(user1.getFullName().charAt(0)).toUpperCase()
                        .compareTo(String.valueOf(user2.getFullName().charAt(0)).toUpperCase());
            }
        });

        String lastHeader = "";

        int size = usersList.size();

        for (int i = 0; i < size; i++) {

            ContactTransferModel user = usersList.get(i);
            String header = String.valueOf(user.getFullName().charAt(0)).toUpperCase();

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                mSectionList.add(new ContactTransferModel(true, header));
            }

            mSectionList.add(user);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CashToCashActivity) getActivity()).updateTitle(getString(R.string.str_account_receive));
    }

    @OnClick(R.id.iv_clear_search)
    public void onViewClicked() {
        edtSearch.setText(Constant.STR_EMPTY);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ContactTransferAdapter.ItemViewHolder) {
            transferModel = mSectionList.get(position);
            contactTransferListener.itemClick(transferModel);
            ((CashToCashActivity) getActivity()).onBackPressed();
        }
    }
}
