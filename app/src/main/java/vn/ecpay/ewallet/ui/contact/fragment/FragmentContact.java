package vn.ecpay.ewallet.ui.contact.fragment;

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
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.adapter.ContactTransferAdapter;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;
import vn.ecpay.ewallet.ui.contact.adapter.ContactAdapter;

public class FragmentContact extends ECashBaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    private ContactAdapter mAdapter;
    private List<Contact> mSectionList;
    private AccountInfo accountInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contact;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                    List<Contact> listAllContact = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
                    setAdapter(listAllContact);
                }
            }
        });
    }

    private void setAdapterTextChange(String filter) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<Contact> transferModelArrayList = WalletDatabase.getListContactFilter(CommonUtils.getParamFilter(filter), accountInfo.getWalletId());
        setAdapter(transferModelArrayList);
    }

    private void setAdapter(List<Contact> listContact) {
        if (listContact.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        mSectionList = new ArrayList<>();
        getHeaderListLatter(listContact);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactAdapter(mSectionList, getActivity(), pos -> {
            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
            List<Contact> listContactAfterDelete = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
            setAdapter(listContactAfterDelete);
        });
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
    }

    @OnClick({R.id.iv_add_contact, R.id.iv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add_contact:
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.iv_clear:
                edtSearch.setText(Constant.STR_EMPTY);
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
        if (event.getData().equals(Constant.EVENT_UPDATE_CONTACT)) {
            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
            List<Contact> transferModelArrayList = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
            setAdapter(transferModelArrayList);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }
}
