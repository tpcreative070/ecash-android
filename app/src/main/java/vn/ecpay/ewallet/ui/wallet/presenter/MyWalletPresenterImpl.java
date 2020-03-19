package vn.ecpay.ewallet.ui.wallet.presenter;

import android.content.Context;

import com.google.firebase.database.annotations.NotNull;

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
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.account.cancelAccount.RequestCancelAccount;
import vn.ecpay.ewallet.model.account.cancelAccount.response.ResponseCancelAccount;
import vn.ecpay.ewallet.model.account.logout.RequestLogOut;
import vn.ecpay.ewallet.model.account.logout.response.ResponseLogOut;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.wallet.view.MyWalletView;

public class MyWalletPresenterImpl implements MyWalletPresenter {
    private MyWalletView myWalletView;
    @Inject
    ECashApplication application;

    @Inject
    public MyWalletPresenterImpl() {
    }

    @Override
    public void setView(MyWalletView view) {
        this.myWalletView = view;
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
    public void logout(AccountInfo accountInfo) {
        myWalletView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestLogOut requestLogout = new RequestLogOut();
        requestLogout.setChannelCode(Constant.CHANNEL_CODE);
        requestLogout.setFunctionCode(Constant.FUNCTION_LOGOUT);
        requestLogout.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestLogout.setToken(CommonUtils.getToken());
        requestLogout.setUsername(accountInfo.getUsername());
        requestLogout.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestLogout));
        requestLogout.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseLogOut> call = apiService.logOut(requestLogout);
        call.enqueue(new Callback<ResponseLogOut>() {
            @Override
            public void onResponse(Call<ResponseLogOut> call, Response<ResponseLogOut> response) {
                myWalletView.dismissLoading();
                if (response.isSuccessful()) {
                    myWalletView.onLogoutSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogOut> call, Throwable t) {
                myWalletView.dismissLoading();
              //  myWalletView.showDialogError(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showStatusErrorConnection(t);
            }
        });
    }

    @Override
    public void validateCancelAccount(long balance, AccountInfo accountInfo, Context context) {
        if (balance > 0) {
            myWalletView.transferMoneyToCancel();
        } else {
            cancelAccount(accountInfo, context);
        }
    }

    @Override
    public void cancelAccount(AccountInfo accountInfo, Context context) {
        myWalletView.showLoading();
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestCancelAccount requestCancelAccount = new RequestCancelAccount();
        requestCancelAccount.setChannelCode(Constant.CHANNEL_CODE);
        requestCancelAccount.setFunctionCode(Constant.FUNCTION_CANCEL_ACCOUNT);
        requestCancelAccount.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestCancelAccount.setToken(CommonUtils.getToken());
        requestCancelAccount.setUsername(accountInfo.getUsername());
        requestCancelAccount.setWalletId(String.valueOf(accountInfo.getWalletId()));
        requestCancelAccount.setTerminalId(accountInfo.getTerminalId());
        requestCancelAccount.setTerminalInfo(accountInfo.getTerminalInfo());
        requestCancelAccount.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestCancelAccount));
        requestCancelAccount.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestCancelAccount);
        Call<ResponseCancelAccount> call = apiService.cancelAccount(requestCancelAccount);
        call.enqueue(new Callback<ResponseCancelAccount>() {
            @Override
            public void onResponse(@NotNull Call<ResponseCancelAccount> call, @NotNull Response<ResponseCancelAccount> response) {
                myWalletView.dismissLoading();
                ECashApplication.isCancelAccount = false;
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                myWalletView.onCancelAccountSuccess();
                                SharedPrefs.getInstance().put(SharedPrefs.contactMaxDate, 0L);
                            } else {
                                myWalletView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        myWalletView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    myWalletView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseCancelAccount> call, Throwable t) {
                ECashApplication.isCancelAccount = false;
                myWalletView.dismissLoading();
                //myWalletView.showDialogError(application.getString(R.string.err_upload));
                ECashApplication.getInstance().showStatusErrorConnection(t);
            }
        });
    }
}
