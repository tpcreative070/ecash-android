package vn.ecpay.ewallet.ui.cashChange;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.database.table.Payment_DataBase;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.RequestGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.getAccountWalletInfo.ResponseGetAccountWalletInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashChange.RequestECashChange;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.cashValue.ResultOptimal;
import vn.ecpay.ewallet.model.cashValue.UtilCashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.edongToEcash.response.ResponseEdongToECash;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.RequestGetPublicKeyOrganizetion;
import vn.ecpay.ewallet.model.getPublicKeyOrganization.ResponseGetPublickeyOrganization;
import vn.ecpay.ewallet.ui.cashChange.component.CashChangeSuccess;
import vn.ecpay.ewallet.ui.cashChange.component.GetFullNameAccountRequest;
import vn.ecpay.ewallet.ui.cashChange.component.PublicKeyOrganization;
import vn.ecpay.ewallet.ui.function.SyncCashService;
import vn.ecpay.ewallet.ui.function.ToPayFuntion;
import vn.ecpay.ewallet.ui.function.UpdateMasterKeyFunction;
import vn.ecpay.ewallet.ui.callbackListener.UpdateMasterKeyListener;

import static vn.ecpay.ewallet.ECashApplication.getActivity;
import static vn.ecpay.ewallet.common.utils.CommonUtils.getEncrypData;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;

public class PaymentCashChangeHandler {
    private Payment_DataBase payment;
    private ECashBaseActivity activity;
    private ECashApplication application;
    private String publicKeyOrganization = "";
    private boolean isHandle;
    private List<Integer> listQualitySend = new ArrayList<>();
    private List<Integer> listValueSend = new ArrayList<>();

    private List<Integer> listQualityTake = new ArrayList<>();
    private List<Integer> listValueTake = new ArrayList<>();

    private List<CashTotal> valueListCashChange = new ArrayList<>();
    private List<CashTotal> valueListCashTake = new ArrayList<>();

    private List<CashTotal> listTransfer = new ArrayList<>();

    public PaymentCashChangeHandler(ECashApplication application, ECashBaseActivity activity, Payment_DataBase payment) {
        this.application = application;
        this.activity = activity;
        this.payment = payment;
    }

    public String getPublicKeyOrganization(AccountInfo accountInfo, PublicKeyOrganization publicKey) {
        publicKeyOrganization = "";
        Retrofit retrofit = RetroClientApi.getRetrofitClient(activity.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);
        RequestGetPublicKeyOrganizetion requestGetPublicKeyOrganizetion = new RequestGetPublicKeyOrganizetion();
        requestGetPublicKeyOrganizetion.setChannelCode(Constant.CHANNEL_CODE);
        requestGetPublicKeyOrganizetion.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_ORGANIZATION);
        requestGetPublicKeyOrganizetion.setIssuerCode(Constant.ISSUER_CODE);
        requestGetPublicKeyOrganizetion.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestGetPublicKeyOrganizetion.setTerminalId(accountInfo.getTerminalId());
        requestGetPublicKeyOrganizetion.setToken(CommonUtils.getToken());
        requestGetPublicKeyOrganizetion.setUsername(accountInfo.getUsername());
        requestGetPublicKeyOrganizetion.setChannelSignature(Constant.STR_EMPTY);
        requestGetPublicKeyOrganizetion.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetPublicKeyOrganizetion));
        requestGetPublicKeyOrganizetion.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseGetPublickeyOrganization> call = apiService.getPublicKeyOrganization(requestGetPublicKeyOrganizetion);
        call.enqueue(new Callback<ResponseGetPublickeyOrganization>() {
            @Override
            public void onResponse(Call<ResponseGetPublickeyOrganization> call, Response<ResponseGetPublickeyOrganization> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            publicKeyOrganization = response.body().getResponseData().getIssuerKpValue();
                            publicKey.getPublicKeyOrganization(publicKeyOrganization);
                        } else {
                            CheckErrCodeUtil.errorMessage(activity, response.body().getResponseCode());
                        }
                    }
                } else {
                    activity.dismissLoading();
                    activity.showDialogError(application.getString(R.string.err_upload));
                    activity.restartSocket();
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPublickeyOrganization> call, Throwable t) {
                activity.dismissLoading();
                activity.showDialogError(application.getString(R.string.err_upload));
            }
        });
        return publicKeyOrganization;
    }

    public void requestChangeCash(String cashEnc, List<Integer> listQuality, AccountInfo accountInfo, List<Integer> listValue, CashChangeSuccess cashChangeSuccess) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestECashChange requestECashChange = new RequestECashChange();
        requestECashChange.setChannelCode(Constant.CHANNEL_CODE);
        requestECashChange.setFunctionCode(Constant.FUNCTION_CHANGE_CASH);
        requestECashChange.setCashEnc(cashEnc);
        requestECashChange.setQuantities(listQuality);
        requestECashChange.setReceiver(accountInfo.getWalletId());
        requestECashChange.setSender(Constant.ISSUER_CODE);
        requestECashChange.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        requestECashChange.setToken(CommonUtils.getToken());
        requestECashChange.setUsername(accountInfo.getUsername());
        requestECashChange.setValues(listValue);
        requestECashChange.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestECashChange));
        requestECashChange.setChannelSignature(CommonUtils.generateSignature(dataSign));

        Call<ResponseEdongToECash> call = apiService.changeCash(requestECashChange);
        call.enqueue(new Callback<ResponseEdongToECash>() {
            @Override
            public void onResponse(Call<ResponseEdongToECash> call, Response<ResponseEdongToECash> response) {
                if (response.isSuccessful()) {
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                CashInResponse responseData = response.body().getResponseData();
                                cashChangeSuccess.changeCashSuccess(responseData);
                            } else {
                                activity.dismissLoading();
                                activity.showDialogError(response.body().getResponseMessage());
                            }
                        } else {
                            activity.dismissLoading();
                            CheckErrCodeUtil.errorMessage(activity, response.body().getResponseCode());
                            activity.restartSocket();
                        }
                    } else {
                        activity.dismissLoading();
                        activity.showDialogError(response.body().getResponseMessage());
                        activity.restartSocket();
                    }
                } else {
                    activity.dismissLoading();
                    activity.showDialogError(application.getString(R.string.err_upload));
                    activity.restartSocket();
                }
            }

            @Override
            public void onFailure(Call<ResponseEdongToECash> call, Throwable t) {
                activity.dismissLoading();
                activity.showDialogError(application.getString(R.string.err_upload));
                activity.restartSocket();
            }
        });
    }

    public void getWalletAccountInfo(AccountInfo accountInfo, long walletID, GetFullNameAccountRequest listener) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestGetAccountWalletInfo requestGetAccountWalletInfo = new RequestGetAccountWalletInfo();
        requestGetAccountWalletInfo.setChannelCode(Constant.CHANNEL_CODE);
        requestGetAccountWalletInfo.setFunctionCode(Constant.FUNCTION_GET_PUBLIC_KEY_WALLET);
        requestGetAccountWalletInfo.setSessionId(accountInfo.getSessionId());
        requestGetAccountWalletInfo.setTerminalId(CommonUtils.getIMEI(activity));
        requestGetAccountWalletInfo.setToken(CommonUtils.getToken());
        requestGetAccountWalletInfo.setUsername(accountInfo.getUsername());//auditNumber
        requestGetAccountWalletInfo.setWalletId(walletID);
        requestGetAccountWalletInfo.setAuditNumber(CommonUtils.getAuditNumber());

        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestGetAccountWalletInfo));
        requestGetAccountWalletInfo.setChannelSignature(CommonUtils.generateSignature(dataSign));
        Gson gson = new Gson();
        String json = gson.toJson(requestGetAccountWalletInfo);
        Log.e("json", json);

        Call<ResponseGetAccountWalletInfo> call = apiService.getWalletAccountInfo(requestGetAccountWalletInfo);
        call.enqueue(new Callback<ResponseGetAccountWalletInfo>() {
            @Override
            public void onResponse(Call<ResponseGetAccountWalletInfo> call, Response<ResponseGetAccountWalletInfo> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getResponseCode() != null) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            AccountInfo mAccountInfo = response.body().getResponseData();
                            if (mAccountInfo != null) {
                                listener.getFullName(mAccountInfo.getPersonFirstName() + " " + mAccountInfo.getPersonMiddleName() + " " + mAccountInfo.getPersonLastName());
                            } else {
                                listener.getFullName("");
                            }
                        } else {
                            listener.getFullName("");
                        }
                    }
                } else {
                    listener.getFullName("");
                }

            }

            @Override
            public void onFailure(Call<ResponseGetAccountWalletInfo> call, Throwable t) {
                listener.getFullName("");
                activity.dismissLoading();
                activity.restartSocket();
            }
        });

    }

    public void showDialogConfirmPayment(List<CashTotal> valueListCash) {
        DialogUtil.getInstance().showDialogConfirmPayment(activity, valueListCash, payment, new DialogUtil.OnConfirm() {
            @Override
            public void OnListenerOk() {
                handleToPay(valueListCash, payment);
            }

            @Override
            public void OnListenerCancel() {
                activity.dismissLoading();
                activity.restartSocket();
            }
        });
    }

    public void showDialogNewPaymentRequest(boolean toPay) {
        isHandle = true;
        activity.showLoading();
        payment.setFullName("");
        DatabaseUtil.deletePayment(activity, payment.getId());
        AccountInfo accountInfo = ECashApplication.getAccountInfo();
        if (accountInfo != null) {
            getWalletAccountInfo(accountInfo, Long.parseLong(payment.getSender()), new GetFullNameAccountRequest() {
                @Override
                public void getFullName(String fullname) {
                    payment.setFullName(fullname);
                    if (toPay) {
                        DialogUtil.getInstance().showDialogPaymentRequest(activity, payment, new DialogUtil.OnConfirm() {
                            @Override
                            public void OnListenerOk() {
                                validatePayment();
                            }

                            @Override
                            public void OnListenerCancel() {
                                activity.dismissLoading();
                                activity.getPaymentDataBase();
                            }
                        });

                    } else {
                        validatePayment();
                    }
                }
            });
        } else {
            if (toPay) {
                DialogUtil.getInstance().showDialogPaymentRequest(activity, payment, new DialogUtil.OnConfirm() {
                    @Override
                    public void OnListenerOk() {
                        validatePayment();
                    }

                    @Override
                    public void OnListenerCancel() {
                        activity.dismissLoading();
                        activity.getPaymentDataBase();
                    }
                });

            } else {
                validatePayment();
            }
        }

    }

    public void validatePayment() {
        // this.payment = payment;
        if (payment == null)
            return;
        activity.showLoading();
        long balanceEcash = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);

        long totalAmount = Long.parseLong(payment.getTotalAmount());

        //-------
        if (balanceEcash >= totalAmount) {
            valueListCashChange = new ArrayList<>();
            valueListCashTake = new ArrayList<>();
            List<CashTotal> listDataBase = DatabaseUtil.getAllCashTotal(getActivity());
            // Collections.reverse(listDataBase);// todo

            List<CashTotal> walletList = new ArrayList<>();
            for (CashTotal cash : listDataBase) {
                walletList.addAll(cash.slitCashTotal());
            }
//            for (CashTotal cashTotal : walletList) {
//                Log.e(" change new .", cashTotal.getParValue() + "");
//            }
            List<CashTotal> partialList = new ArrayList<>();
            UtilCashTotal util = new UtilCashTotal();
            ResultOptimal resultOptimal = util.recursiveFindeCashs(walletList, partialList, totalAmount);
            if (resultOptimal.remain == 0) {
                //List<CashTotal> list = resultOptimal.listPartial;
                //  handlePaymentWithCashValid(list);
                listTransfer = resultOptimal.listPartial;
                handlePaymentWithCashValid();

            } else {
                /** Lay ra nhung to tien can doi **/
                int amountCompare = 0;
                ArrayList<CashTotal> arrayUseForExchange = new ArrayList<CashTotal>();
                for (CashTotal item : resultOptimal.listWallet) {
                    if (amountCompare < resultOptimal.remain) {
                        amountCompare += item.getParValue();
                        arrayUseForExchange.add(item);
                    }
                }
                //Collections.reverse(arrayUseForExchange);
                ResultOptimal expectedExchange = util.recursiveGetArrayNeedExchange(resultOptimal.remain, new ArrayList<CashTotal>());
                long otherAmountNeedExchange = amountCompare - resultOptimal.remain;
                ResultOptimal resultOtherExchange = util.recursiveGetArrayNeedExchange(otherAmountNeedExchange, new ArrayList<CashTotal>());
                expectedExchange.listPartial.addAll(resultOtherExchange.listPartial);
                String stExpect = "";
                for (CashTotal item : expectedExchange.listPartial) {
                    stExpect += item.getParValue() + ",";

                    if (valueListCashTake.size() > 0) {
                        boolean check = false;
                        for (CashTotal cash : valueListCashTake) {
                            if (cash.getParValue() == item.getParValue()) {
                                cash.setTotalDatabase(cash.getTotal() + 1);
                                cash.setTotal(cash.getTotal() + 1);
                                check = true;
                            }
                        }
                        if (!check) {
                            item.setTotalDatabase(1);
                            item.setTotal(1);
                            valueListCashTake.add(item);
                        }
                    } else {
                        item.setTotalDatabase(1);
                        item.setTotal(1);
                        valueListCashTake.add(item);
                    }
                }
                if (stExpect.length() > 0) {
                    stExpect = "Array Expect Exchange = [" + stExpect.substring(0, stExpect.length() - 1) + "]";
                }

                String stEchange = "";
                for (CashTotal item : arrayUseForExchange) {
                    stEchange += item.getParValue() + ",";
                    item.setTotal(item.getTotal() + 1);
                    //  Log.e("item change ",item.getParValue()+"");
                    valueListCashChange.add(item);
                }
                if (stEchange.length() > 0) {
                    stEchange = "Array Ecash Use For Exchange = [" + stEchange.substring(0, stEchange.length() - 1) + "]";
                }

                resultOptimal.listPartial.addAll(expectedExchange.listPartial);
                ResultOptimal rs = util.recursiveFindeCashs(resultOptimal.listPartial, new ArrayList<CashTotal>(), totalAmount);
                String stTranfer = "";
                for (CashTotal item : rs.listPartial) {
                    stTranfer += item.getParValue() + ",";
                    listTransfer.add(item);
                }
                if (stTranfer.length() > 0) {
                    stTranfer = "Array Transfer = [" + stTranfer.substring(0, stTranfer.length() - 1) + "]";
                }

                getPublicKeyOrganization();
            }

        } else {//balanceEcash<totalAmount
            activity.dismissLoading();
            showDialogCannotPayment();
            isHandle = false;
        }

        //
    }

    private void getPublicKeyOrganization() {
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(activity);
        getPublicKeyOrganization(accountInfo, new PublicKeyOrganization() {
            @Override
            public void getPublicKeyOrganization(String publicKey) {
                //   Log.e("publicKey ",publicKey);
                if (publicKey != null && publicKey.length() > 0) {
                    // getCashConvert(accountInfo,cashChangeHandler, payments, publicKey);
                    getListCashSend();
                    getListCashTake();
                    convertCash(publicKey, accountInfo);
                }
            }
        });
    }

    private void getListCashSend() {
        listQualitySend = new ArrayList<>();
        listValueSend = new ArrayList<>();
        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
                Log.e("valueListCashChange . ", valueListCashChange.get(i).getParValue() + "");
                //Log.e("valueListCashChange . ",valueListCashChange.get(i).getParValue()+"");
                listQualitySend.add(valueListCashChange.get(i).getTotal());
                listValueSend.add(valueListCashChange.get(i).getParValue());
            }
        }
    }

    private void getListCashTake() {
        listQualityTake = new ArrayList<>();
        listValueTake = new ArrayList<>();
        for (int i = 0; i < valueListCashTake.size(); i++) {
            if (valueListCashTake.get(i).getTotal() > 0) {
                Log.e("valueListCashTake . ", valueListCashTake.get(i).getParValue() + " * " + valueListCashTake.get(i).getTotal() + "");
                //  Log.e("valueListCashTake . ",valueListCashTake.get(i).getParValue()+" * "+valueListCashTake.get(i).getTotal()+"");
                listQualityTake.add(valueListCashTake.get(i).getTotal());
                listValueTake.add(valueListCashTake.get(i).getParValue());
            }
        }
    }


    private void convertCash(String keyPublicReceiver, AccountInfo accountInfo) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
                //   Log.e("valueListCashChange.",valueListCashChange.get(i).getParValue()+" - ");
                //  Log.e("valueListCashChange.", valueListCashChange.get(i).getParValue() + " - ");
                List<CashLogs_Database> cashList = WalletDatabase.getListCashForMoney(String.valueOf(valueListCashChange.get(i).getParValue()), Constant.STR_CASH_IN);
                for (int j = 0; j < valueListCashChange.get(i).getTotal(); j++) {
                    listCashSend.add(cashList.get(j));
                }
            }
        }

        String[][] cashSendArray = new String[listCashSend.size()][3];

        for (int i = 0; i < listCashSend.size(); i++) {
            CashLogs_Database cash = listCashSend.get(i);
            String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
            cashSendArray[i] = moneyItem;
        }
        String encData = getEncrypData(cashSendArray, keyPublicReceiver);
        if (encData.isEmpty()) {
            activity.dismissLoading();
            if (activity != null)
                activity.showDialogError("không lấy được endCrypt data và ID");
            isHandle = false;
            return;
        }

        requestChangeCash(encData, listQualityTake, accountInfo, listValueTake, new CashChangeSuccess() {
            @Override
            public void changeCashSuccess(CashInResponse cashInResponse) {
                if (null != activity) {
                    activity.startService(new Intent(activity, SyncCashService.class));
                }
                DatabaseUtil.saveCashOut(cashInResponse.getId(), listCashSend, activity, accountInfo.getUsername());
                Gson gson = new Gson();
                String jsonCashInResponse = gson.toJson(cashInResponse);
                CacheData_Database cacheData_database = new CacheData_Database();
                cacheData_database.setTransactionSignature(cashInResponse.getId());
                cacheData_database.setResponseData(jsonCashInResponse);
                cacheData_database.setType(TYPE_CASH_EXCHANGE);
                DatabaseUtil.saveCacheData(cacheData_database, activity);
                // Log.e("A","A");
                // EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_CHANGE));
                activity.setEvenBus(Constant.EVENT_CASH_IN_CHANGE);
                //validatePayment(payments);
                // dismissLoading();
            }
        });
        //  dismissLoading();
    }

    public void handlePaymentWithCashValid() {
        if (listTransfer == null || listTransfer.size() == 0) {
            // activity.showDialogError("có lỗi xẩy ra!!!");
            Log.e("Error listTransfer", "handlePaymentWithCashValid");
            activity.dismissLoading();
            isHandle = false;
            return;
        }
        valueListCashChange = new ArrayList<>();
        // String st = "";
        for (CashTotal item : listTransfer) {
            //   st += item.getParValue() + ",";
            if (valueListCashChange.size() > 0) {
                boolean check = false;
                for (CashTotal cash : valueListCashChange) {
                    if (cash.getParValue() == item.getParValue()) {
                        cash.setTotalDatabase(cash.getTotal() + 1);
                        cash.setTotal(cash.getTotal() + 1);
                        check = true;
                    }
                }
                if (!check) {
                    item.setTotal(1);
                    item.setTotalDatabase(1);
                    valueListCashChange.add(item);
                }
            } else {
                item.setTotal(1);
                item.setTotalDatabase(1);
                valueListCashChange.add(item);
            }
        }
        //                if (st.length() > 0) {
//                    st = "Array Transfer = [" + st.substring(0, st.length() - 1) + "]";
//                }
        showDialogConfirmPayment(valueListCashChange);
    }

    private void showDialogCannotPayment() {
        DialogUtil.getInstance().showDialogCannotPayment(activity);
        activity.restartSocket();
    }

    private void handleToPay(List<CashTotal> listCash, Payment_DataBase payToRequest) {
        activity.showLoading();
        ArrayList<Contact> listContact = new ArrayList<>();
        Contact contact = new Contact();
        contact.setWalletId(Long.parseLong(payToRequest.getSender()));
        listContact.add(contact);
        UpdateMasterKeyFunction updateMasterKeyFunction = new UpdateMasterKeyFunction(activity);
        ToPayFuntion toPayFuntion = new ToPayFuntion(activity, listCash, contact, payToRequest);
        updateMasterKeyFunction.updateLastTimeAndMasterKey(new UpdateMasterKeyListener() {
            @Override
            public void onUpdateMasterSuccess() {
                toPayFuntion.handlePayToSocket(() -> {
                    activity.dismissLoading();
                    showDialogPaymentSuccess(payToRequest);
                });
            }

            @Override
            public void onUpdateMasterFail(String code) {
                activity.dismissLoading();
                CheckErrCodeUtil.errorMessage(getActivity(), code);
                activity.restartSocket();
            }

            @Override
            public void onRequestTimeout() {

            }
        });
    }

    public void showDialogPaymentSuccess(Payment_DataBase payment_dataBase) {
        // DatabaseUtil.deletePayment(activity,payment_dataBase.getId());
        this.payment = null;
        isHandle = false;

        activity.restartSocket();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (activity == null) return;
                activity.setEvenBus(Constant.EVENT_PAYMENT_SUCCESS);
                // EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_PAYMENT_SUCCESS));
            }
        }, 500);

        DialogUtil.getInstance().showDialogPaymentSuccess(activity, payment_dataBase, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                activity.getPaymentDataBase();
            }
        });

        listQualitySend = new ArrayList<>();
        listValueSend = new ArrayList<>();
        listQualityTake = new ArrayList<>();
        listValueTake = new ArrayList<>();
        valueListCashChange = new ArrayList<>();
        valueListCashTake = new ArrayList<>();
        listTransfer = new ArrayList<>();
    }

    public boolean isHandle() {
        return isHandle;
    }

    public void setHandle(boolean handle) {
        isHandle = handle;
    }
}
