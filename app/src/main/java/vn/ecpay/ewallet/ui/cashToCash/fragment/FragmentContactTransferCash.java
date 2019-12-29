package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.ContactTransferAdapter;
import vn.ecpay.ewallet.ui.interfaceListener.MultiTransferListener;
import vn.ecpay.ewallet.ui.lixi.MyLixiActivity;

public class FragmentContactTransferCash extends ECashBaseFragment implements MultiTransferListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    private ContactTransferAdapter mAdapter;
    private List<Contact> mSectionList;
    private MultiTransferListener multiTransferListener;
    private AccountInfo accountInfo;
    private ArrayList<Contact> multiTransferList;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contact_transfer;
    }

    public static FragmentContactTransferCash newInstance(MultiTransferListener multiTransferListener) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.CONTACT_MULTI_TRANSFER, multiTransferListener);
        FragmentContactTransferCash fragment = new FragmentContactTransferCash();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbarCenterText.setText(getResources().getString(R.string.str_account_receive));
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.multiTransferListener = (MultiTransferListener) bundle.getSerializable(Constant.CONTACT_MULTI_TRANSFER);
        }

        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        List<Contact> transferModelArrayList = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));

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
        List<Contact> transferModelArrayList = WalletDatabase.getListContactFilter(CommonUtils.getParamFilter(filter), accountInfo.getWalletId());
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

    @OnClick({R.id.iv_back, R.id.tv_done, R.id.iv_filter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                try {
                    if (getActivity() != null)
                        ((CashToCashActivity) getActivity()).onBackPressed();
                } catch (ClassCastException e) {
                    if (getActivity() != null)
                        ((MyLixiActivity) getActivity()).onBackPressed();
                }
                break;
            case R.id.tv_done:
                if (multiTransferList != null) {
                    if (multiTransferList.size() > 0) {
                        multiTransferListener.onMultiTransfer(multiTransferList);
                        try {
                            if (getActivity() != null)
                                ((CashToCashActivity) getActivity()).onBackPressed();
                        } catch (ClassCastException e) {
                            if (getActivity() != null)
                                ((MyLixiActivity) getActivity()).onBackPressed();
                        }
                    } else {
                        DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_un_chose_wallet_send));
                    }
                } else {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_un_chose_wallet_send));
                }
                break;
            case R.id.iv_filter:
                edtSearch.setText(Constant.STR_EMPTY);
                break;
        }
    }

    @Override
    public void onMultiTransfer(ArrayList<Contact> contactList) {
        multiTransferList = contactList;
    }
}
