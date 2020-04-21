package vn.ecpay.ewallet.ui.contact.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactDelete.RequestDeleteContact;
import vn.ecpay.ewallet.model.contactDelete.ResponseDeleteContact;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.contact.AddContactActivity;
import vn.ecpay.ewallet.ui.contact.adapter.ContactAdapter;

public class FragmentContact extends ECashBaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.edt_search)
    EditText edtSearch;
    @BindView(R.id.tv_done)
    TextView tvDone;
    private ContactAdapter mAdapter;
    private List<Contact> mSectionList;
    private AccountInfo accountInfo;
    private ArrayList<Contact> listContactTransfer;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contact;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accountInfo = DatabaseUtil.getAccountInfo(getActivity());
        initAdapter();
    }

    private void initAdapter() {
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
        mSectionList = new ArrayList<>();
        if (listContact.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        getHeaderListLatter(listContact);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactAdapter(mSectionList, getActivity(), pos -> {
            showProgress();
            deleteContact(mSectionList.get(pos), getActivity());
        }, this::checkListContactTransfer);
        recyclerView.setAdapter(mAdapter);
    }

    private void checkListContactTransfer() {
        for (int i = 0; i < mSectionList.size(); i++) {
            if (mSectionList.get(i).isAddTransfer) {
                tvDone.setVisibility(View.VISIBLE);
                return;
            }
        }
        tvDone.setVisibility(View.GONE);
    }

    private void getHeaderListLatter(List<Contact> usersList) {
        Collections.sort(usersList, (user1, user2) -> CommonUtils.getNameHeader(user1).toUpperCase()
                .compareTo(CommonUtils.getNameHeader(user2).toUpperCase()));
        String lastHeader = "";
        int size = usersList.size();
        for (int i = 0; i < size; i++) {
            Contact user = usersList.get(i);
            String header;
            try {
                header = CommonUtils.getNameHeader(user).toUpperCase();
            } catch (IndexOutOfBoundsException e) {
                header = String.valueOf(user.getPhone()).toUpperCase();
            }

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

    @OnClick({R.id.iv_add_contact, R.id.iv_clear, R.id.tv_done, R.id.iv_back})
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
            case R.id.iv_back:
                ((MainActivity) getActivity()).onBackPressed();
                break;
            case R.id.tv_done:
                if (CommonUtils.getListTransfer(mSectionList).size() > 0) {
                    listContactTransfer = CommonUtils.getListTransfer(mSectionList);
                    Intent intentTransferCash = new Intent(getActivity(), CashToCashActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelableArrayList(Constant.CONTACT_TRANSFER_MODEL, listContactTransfer);
                    intentTransferCash.putExtras(mBundle);
                    if (getActivity() != null) {
                        ((MainActivity) getActivity()).startActivity(intentTransferCash);
                        ((MainActivity) getActivity()).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    initAdapter();
                } else {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getResources().getString(R.string.err_un_chose_wallet_send));
                }
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
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
                List<Contact> transferModelArrayList = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
                setAdapter(transferModelArrayList);
            }, 1000);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void deleteContact(Contact contact, Context context) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(getResources().getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestDeleteContact requestDeleteContact = new RequestDeleteContact();
        requestDeleteContact.setChannelCode(Constant.CHANNEL_CODE);
        requestDeleteContact.setFunctionCode(Constant.FUNCTION_DELETE_CONTACT);
        requestDeleteContact.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestDeleteContact.setUsername(accountInfo.getUsername());
        requestDeleteContact.setWalletId(String.valueOf(contact.getWalletId()));
        requestDeleteContact.setToken(CommonUtils.getToken(getActivity()));
        requestDeleteContact.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestDeleteContact));
        requestDeleteContact.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseDeleteContact> call = apiService.deleteContacts(requestDeleteContact);
        call.enqueue(new Callback<ResponseDeleteContact>() {
            @Override
            public void onResponse(Call<ResponseDeleteContact> call, Response<ResponseDeleteContact> response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            DatabaseUtil.deleteContact(getActivity(), contact.getWalletId());
                            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
                            List<Contact> listContactAfterDelete = WalletDatabase.getListContact(String.valueOf(accountInfo.getWalletId()));
                            setAdapter(listContactAfterDelete);
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).showDialogError(context.getResources().getString(R.string.err_upload));
                    }
                } else {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).showDialogError(context.getResources().getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseDeleteContact> call, Throwable t) {
                dismissProgress();
                if (getActivity() != null)
                    ((MainActivity) getActivity()).showDialogError(getResources().getString(R.string.err_upload));
            }
        });
    }
}
