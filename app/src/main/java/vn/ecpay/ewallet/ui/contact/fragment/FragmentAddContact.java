package vn.ecpay.ewallet.ui.contact.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseDataGetWalletByPhone;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;
import vn.ecpay.ewallet.ui.contact.adapter.AddContactAdapter;
import vn.ecpay.ewallet.ui.contact.module.AddContactModule;
import vn.ecpay.ewallet.ui.contact.presenter.AddContactPresenter;
import vn.ecpay.ewallet.ui.contact.view.AddContactView;

public class FragmentAddContact extends ECashBaseFragment implements AddContactView {
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Inject
    AddContactPresenter addContactPresenter;

    private String edtTextSearch = Constant.STR_EMPTY;
    private AccountInfo accountInfo;
    private AddContactAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add_contact;
    }

    @OnClick({R.id.iv_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_clear:
                edtSearch.setText(Constant.STR_EMPTY);
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new AddContactModule(this)).inject(this);
        addContactPresenter.setView(this);
        addContactPresenter.onViewCreate();
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtTextSearch = v.getText().toString();
                validateData(edtTextSearch);
                return true;
            }
            return false;
        });
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
    }

    private void validateData(String edtTextSearch) {
        if (edtTextSearch.isEmpty()) {
            ((AddContactActivity) getActivity()).showDialogError(getResources().getString(R.string.str_edt_search_empty));
            return;
        }
        if (!CommonUtils.isValidateNumber(edtTextSearch)) {
            ((AddContactActivity) getActivity()).showDialogError(getResources().getString(R.string.err_filter_add_contact));
            return;
        }
        if (CommonUtils.isValidatePhoneNumber(edtTextSearch)) {
            addContactPresenter.requestSearchByPhone(edtTextSearch, accountInfo);
        } else {
            addContactPresenter.requestSearchWalletID(edtTextSearch, accountInfo);
        }
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
    public void getWalletFail(String err) {
        ((AddContactActivity) getActivity()).showDialogError(err);
    }

    @Override
    public void onSearchByWalletSuccess(ResponseDataGetPublicKeyWallet responseData) {
        Contact contact = new Contact();
        contact.setFullName(CommonUtils.getFullName(responseData));
        contact.setPhone(responseData.getPersonMobilePhone());
        contact.setPublicKeyValue(responseData.getEcKpValue());
        contact.setWalletId(Long.valueOf(edtTextSearch));
        List<Contact> listContact = new ArrayList<>();
        listContact.add(contact);
        setAdapter(listContact);
    }

    @Override
    public void onSearchByPhoneSuccess(ResponseDataGetWalletByPhone responseData) {
        List<Contact> listContact = new ArrayList<>();
        if (responseData.getWallets().size() > 0) {
            for (int i = 0; i < responseData.getWallets().size(); i++) {
                Contact contactTransferModel = new Contact();
                contactTransferModel.setFullName(CommonUtils.getFullName(responseData));
                contactTransferModel.setPhone(responseData.getPersonMobilePhone());
                contactTransferModel.setPublicKeyValue(responseData.getWallets().get(i).getEcPublicKey());
                contactTransferModel.setWalletId(responseData.getWallets().get(i).getWalletId());
                listContact.add(contactTransferModel);
            }
        }
        setAdapter(listContact);
    }

    private void setAdapter(List<Contact> transferModelArrayList) {
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddContactAdapter(transferModelArrayList, getActivity());
        recyclerView.setAdapter(mAdapter);
    }
}
