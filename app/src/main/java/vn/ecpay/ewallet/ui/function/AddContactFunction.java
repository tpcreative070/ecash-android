package vn.ecpay.ewallet.ui.function;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactAdd.RequestAddContact;
import vn.ecpay.ewallet.model.contactAdd.ResponseAddContact;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.callbackListener.AddContactListener;

public class AddContactFunction {
    private Context context;

    public AddContactFunction(Context context) {
        this.context = context;
    }

    public void addContact(AccountInfo accountInfo, Contact contact, AddContactListener addContactListener) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(context.getResources().getString(R.string.api_base_url));
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
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            addContactListener.addContactSuccess();
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        addContactListener.addContactFail();
                    }
                } else {
                    addContactListener.addContactFail();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddContact> call, Throwable t) {
            //    addContactListener.addContactFail();
                ECashApplication.getInstance().showStatusErrorConnection(t);
            }
        });
    }
}
