package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.ContactTransferAdapter;

public class FragmentContactTransferCash extends ECashBaseFragment implements ContactTransferAdapter.onItemTransferListener{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    private ContactTransferAdapter mAdapter;
    private List<Contact> mSectionList;
    private Contact transferModel;
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
        List<Contact> transferModelArrayList = WalletDatabase.getListContact();

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
        List<Contact> transferModelArrayList = WalletDatabase.getListContactFilter(CommonUtils.getParamFilter(filter));
        setAdapter(transferModelArrayList);
    }

    private void setAdapter(List<Contact> transferModelArrayList) {
        mSectionList = new ArrayList<>();
        getHeaderListLatter(transferModelArrayList);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactTransferAdapter(mSectionList, getActivity(), this);
        recyclerView.setAdapter(mAdapter);
    }

    private void getHeaderListLatter(List<Contact> usersList) {

        Collections.sort(usersList, (user1, user2) -> String.valueOf(user1.getFullName().charAt(0)).toUpperCase()
                .compareTo(String.valueOf(user2.getFullName().charAt(0)).toUpperCase()));

        String lastHeader = "";

        int size = usersList.size();

        for (int i = 0; i < size; i++) {

            Contact user = usersList.get(i);
            String header = String.valueOf(user.getFullName().charAt(0)).toUpperCase();

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                mSectionList.add(new Contact(true, header));
            }

            mSectionList.add(user);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CashToCashActivity) getActivity()).updateTitle(getString(R.string.str_account_receive));
    }

    @OnClick(R.id.iv_filter)
    public void onViewClicked() {
        edtSearch.setText(Constant.STR_EMPTY);
    }

    @Override
    public void onItemClick(Contact contact) {
        contactTransferListener.itemClick(transferModel);
        ((CashToCashActivity) getActivity()).onBackPressed();
    }
}
