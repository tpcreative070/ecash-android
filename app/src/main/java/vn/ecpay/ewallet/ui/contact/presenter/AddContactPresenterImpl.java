package vn.ecpay.ewallet.ui.contact.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contact.RequestSyncContact;
import vn.ecpay.ewallet.model.contact.ResponseSyncContact;
import vn.ecpay.ewallet.model.contactAdd.RequestAddContact;
import vn.ecpay.ewallet.model.contactAdd.ResponseAddContact;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.getPublicKeyWallet.RequestGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseGetPublicKeyWallet;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseGetPublicKeyByPhone;
import vn.ecpay.ewallet.ui.contact.view.AddContactView;

public class AddContactPresenterImpl implements AddContactPresenter {
    AddContactView addContactView;

    @Inject
    ECashApplication application;

    @Inject
    public AddContactPresenterImpl() {
    }

    @Override
    public void setView(AddContactView view) {
        this.addContactView = view;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewResume() {

    }

    @Override
    public void onViewPause() {

    }

    @Override
    public void onViewStop() {

    }

    @Override
    public void onViewDestroy() {

    }

    @Override
    public void requestSearchByPhone(String phoneNumber, AccountInfo accountInfo) {
        addContactView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_BY_PHONE);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken());
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setPersonMobilePhone(phoneNumber);
        requestGetPublicKeyWallet.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyByPhone> call = apiService.getPublicKeyWalletByPhone(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyByPhone>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyByPhone> call, Response<ResponseGetPublicKeyByPhone> response) {
                addContactView.dismissLoading();
                if (response.isSuccessful()) {
                    if (null != response.body()) {
                        if (null != response.body().getResponseCode()) {
                            if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                                addContactView.onSearchByPhoneSuccess(response.body().getResponseData());
                            } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                                application.checkSessionByErrorCode(response.body().getResponseCode());
                            } else {
                                addContactView.getWalletFail(response.body().getResponseMessage());
                            }
                        } else {
                            addContactView.getWalletFail(application.getString(R.string.err_upload));
                        }
                    } else {
                        addContactView.getWalletFail(application.getString(R.string.err_upload));
                    }
                } else {
                    addContactView.getWalletFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyByPhone> call, Throwable t) {
                addContactView.dismissLoading();
                addContactView.getWalletFail(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void requestSearchWalletID(String walletId, AccountInfo accountInfo) {
        addContactView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetPublicKeyWallet requestGetPublicKeyWallet = new RequestGetPublicKeyWallet();
        requestGetPublicKeyWallet.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyWallet.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetPublicKeyWallet.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyWallet.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyWallet.setToken(CommonUtils.getToken());
        requestGetPublicKeyWallet.setUsername(accountInfo.getUsername());
        requestGetPublicKeyWallet.setWalletId(walletId);
        requestGetPublicKeyWallet.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyWallet));
        requestGetPublicKeyWallet.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublicKeyWallet> call = apiService.getPublicKeyWallet(requestGetPublicKeyWallet);
        call.enqueue(new Callback<ResponseGetPublicKeyWallet>() {
            @Override
            public void onResponse(Call<ResponseGetPublicKeyWallet> call, Response<ResponseGetPublicKeyWallet> response) {
                addContactView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            addContactView.onSearchByWalletSuccess(response.body().getResponseData());
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            addContactView.getWalletFail(response.body().getResponseMessage());
                        }
                    }
                } else {
                    addContactView.getWalletFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublicKeyWallet> call, Throwable t) {
                addContactView.dismissLoading();
                addContactView.getWalletFail(application.getString(R.string.err_upload));
            }
        });
    }

    @Override
    public void addContact(Context context, AccountInfo accountInfo, Contact contact) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        List<String> userList = new ArrayList<>();
        userList.add(String.valueOf(contact.getWalletId()));

        RequestAddContact requestAddContact = new RequestAddContact();
        requestAddContact.setChannelCode(Constant.CHANNEL_CODE);
        requestAddContact.setFunctionCode(Constant.FUNCTION_ADD_CONTACT);
        requestAddContact.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestAddContact.setListWallets(userList);
        requestAddContact.setUsername(accountInfo.getUsername());
        requestAddContact.setWalletId(String.valueOf(accountInfo.getWalletId()));
        requestAddContact.setToken(CommonUtils.getToken(accountInfo));
        requestAddContact.setAuditNumber(CommonUtils.getAuditNumber());
        requestAddContact.setAddNewWalletId(String.valueOf(contact.getWalletId()));

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestAddContact));
        requestAddContact.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseAddContact> call = apiService.addContacts(requestAddContact);
        call.enqueue(new Callback<ResponseAddContact>() {
            @Override
            public void onResponse(Call<ResponseAddContact> call, Response<ResponseAddContact> response) {
                addContactView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            addContactView.onSyncContactSuccess();
                        } else if (response.body().getResponseCode().equals(Constant.sesion_expid)) {
                            application.checkSessionByErrorCode(response.body().getResponseCode());
                        } else {
                            addContactView.getWalletFail(response.body().getResponseMessage());
                        }
                    }
                } else {
                    addContactView.getWalletFail(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseAddContact> call, Throwable t) {
                addContactView.dismissLoading();
                addContactView.getWalletFail(application.getString(R.string.err_upload));
            }
        });
    }
}
