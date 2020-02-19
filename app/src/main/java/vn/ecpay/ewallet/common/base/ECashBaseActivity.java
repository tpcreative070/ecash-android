package vn.ecpay.ewallet.common.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.LanguageUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CacheData_Database;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.cashValue.ResultOptimal;
import vn.ecpay.ewallet.model.cashValue.UtilCashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.ui.cashChange.CashChangeHandler;
import vn.ecpay.ewallet.ui.cashChange.component.CashChangeSuccess;
import vn.ecpay.ewallet.ui.cashChange.component.GetFullNameAccountRequest;
import vn.ecpay.ewallet.ui.cashChange.component.PublicKeyOrganization;
import vn.ecpay.ewallet.ui.function.SyncCashService;
import vn.ecpay.ewallet.ui.function.ToPayFuntion;
import vn.ecpay.ewallet.ui.interfaceListener.ToPayListener;
import vn.ecpay.ewallet.webSocket.WebSocketsService;

import static vn.ecpay.ewallet.ECashApplication.getActivity;
import static vn.ecpay.ewallet.common.utils.CommonUtils.getEncrypData;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;

public abstract class ECashBaseActivity extends AppCompatActivity implements BaseView {
    private static final String TAG = "BaseActivity";
    private Dialog progressDialog;
    Stack<Fragment> fragmentStack;
    FragmentManager fmgr;
    public static final int TIMES_OUT = 300000;

    public void initFragmentStack() {
        fragmentStack = new Stack<Fragment>();
    }

    public FragmentManager getFmgr() {
        return fmgr;
    }

    public void setFmgr(FragmentManager fmgr) {
        this.fmgr = fmgr;
    }

    public Stack<Fragment> getFragmentStack() {
        return fragmentStack;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent();
//        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getLayoutResId());
        setFmgr(getSupportFragmentManager());
        initFragmentStack();
        injectViews();

    }

    protected void showAnimationStartActivity() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    protected abstract int getLayoutResId();

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ECashApplication.get(this).setActivity(this);
        setupUI(getWindow().getDecorView().findViewById(android.R.id.content), this);
    }

    public void setupUI(View view, final Activity activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(activity);
                if (handler == null) {
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler = null;
                        }
                    }, 5000);
                }
                return false;
            }
        });
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }

    }

    static Handler handler;

    public void hideSoftKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void updateTitle() {
    }

    @Override
    public void onBackPressed() {
        if (getFragmentStack().size() >= 2) {
            FragmentTransaction ft = getFmgr().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            getFragmentStack().lastElement().onPause();
            ft.remove(getFragmentStack().pop());
            getFragmentStack().lastElement().onResume();
            ft.show(getFragmentStack().lastElement());
            ft.commit();
        } else {
            super.onBackPressed();
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        updateTitle();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void showLoading() {
        if (!isFinishing()) {
            showProgressDialog();
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setContentView(R.layout.dialog_progress);
            progressDialog.setCancelable(false);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissLoading() {
        if (!isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation, int resIdContainer) {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.getClass().getName().equals(pFragment.getClass().getName())) // kiểm tra fragment muốn add có trên top stack k,nếu đã tồn tại thì k add nữa
            return;
        try {
            final FragmentTransaction ft = getFmgr().beginTransaction();
            if (isAnimation)
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.add(resIdContainer, pFragment);
            if (getFragmentStack().size() > 0) {
                getFragmentStack().lastElement().onPause();
                ft.hide(getFragmentStack().lastElement());
            }
            getFragmentStack().push(pFragment);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //ft.commit();
                    ft.commitAllowingStateLoss();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment pFragment, boolean isAnimation, int resContainer) {
        try {
            final FragmentTransaction ft = getFmgr().beginTransaction();
            if (isAnimation)
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            ft.replace(resContainer, pFragment);
            if (getFragmentStack().size() > 0) {
                getFragmentStack().lastElement().onPause();
                ft.hide(getFragmentStack().lastElement());

            }
            getFragmentStack().push(pFragment);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //ft.commit();
                    ft.commitAllowingStateLoss();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    protected void injectViews() {
        ButterKnife.bind(this);
    }

    protected abstract void setupActivityComponent();


    protected Fragment getCurrentFragment() {
        try {
            return fragmentStack.lastElement();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    protected String getCurrentActivity() {
        return this.getClass().getName();
    }

    public void showDialogError(String messenger) {
        DialogUtil.getInstance().showDialogWarning(this, messenger);
    }

    public void showDialogError(int resMessage) {
        showDialogError(getString(resMessage));
    }

    public SharedPreferences getSharedPreference() {
        return getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    private Context updateBaseContextLocale(Context context) {
        Locale locale = new Locale(LanguageUtils.getCurrentLanguage().getCode());
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
    //------------------

    private Payments payment;

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        // Log.e("D","D "+event.getData());
        if (event.getData().equals(Constant.EVENT_CASH_IN_PAYTO)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            if (payment != null) {
                                validatePayment(payment);
                            }
                        });
                    } catch (NullPointerException ignored) {
                    }
                }
            }, 500);


        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void showDialogCannotPayment() {
        DialogUtil.getInstance().showDialogCannotPayment(this);
    }

    public void showDialogPaymentSuccess(Payments payToRequest) {
        this.payment = null;
        restartSocket();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) return;
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_PAYMENT_SUCCESS));
            }
        }, 500);

        DialogUtil.getInstance().showDialogPaymentSuccess(this, payToRequest, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
            }
        });
    }

    public void restartSocket() {
        if (getActivity() != null) {
            //  Log.e("start ","start ");
            getActivity().startService(new Intent(getActivity(), WebSocketsService.class));
        }
    }
    public void stopSocket(){
        if (getActivity() != null) {
            //  Log.e("start ","start ");
            if(CommonUtils.isMyServiceRunning(WebSocketsService.class)){
                Log.e("stop service","stop service");
                getActivity().stopService(new Intent(getActivity(), WebSocketsService.class));
            }else{
                Log.e("cannot stop service","cannot stop service");
            }

        }
    }

    public void showDialogNewPaymentRequest(Payments mPayment, boolean toPay) {
        showLoading();
        this.payment = mPayment;
        payment.setFullName("");
        AccountInfo accountInfo = ECashApplication.getAccountInfo();
        if (accountInfo != null) {
            CashChangeHandler cashChangeHandler = new CashChangeHandler(ECashApplication.getInstance(), this);
            cashChangeHandler.getWalletAccountInfo(accountInfo, Long.parseLong(payment.getSender()), new GetFullNameAccountRequest() {
                @Override
                public void getFullName(String fullname) {
                    payment.setFullName(fullname);
                    if (toPay) {
                        DialogUtil.getInstance().showDialogPaymentRepuest(getActivity(), payment, () -> validatePayment(payment));

                    } else {
                        validatePayment(payment);
                    }
                }
            });
        } else {
            if (toPay) {
                DialogUtil.getInstance().showDialogPaymentRepuest(getActivity(), payment, () -> validatePayment(payment));

            } else {
                validatePayment(payment);
            }
        }

    }

    public void showDialogConfirmPayment(List<CashTotal> valueListCash, Payments payToRequest) {

        DialogUtil.getInstance().showDialogConfirmPayment(this, valueListCash, payToRequest, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                handleToPay(valueListCash, payToRequest);
            }
        });
    }

    public void validatePayment(Payments payment) {
        this.payment = payment;
        showProgressDialog();
        long balanceEcash = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        long balanceEdong = 0;

        ArrayList<EdongInfo> listEDongInfo = ECashApplication.getListEDongInfo();
        if (null != listEDongInfo) {
            if (listEDongInfo.size() > 0) {
                balanceEdong = CommonUtils.getMoneyEDong(listEDongInfo.get(0));
            }
        }
        long totalAmount = Long.parseLong(payment.getTotalAmount());

        //-------
        if (balanceEcash >= totalAmount) {
            valueListCashChange = new ArrayList<>();
            valueListCashTake = new ArrayList<>();
            List<CashTotal> listDataBase = DatabaseUtil.getAllCashTotal(getActivity());
            Collections.reverse(listDataBase);

            List<CashTotal> walletList = new ArrayList<>();
            for (CashTotal cash : listDataBase) {
                walletList.addAll(cash.slitCashTotal());
            }

            List<CashTotal> partialList = new ArrayList<CashTotal>();
            UtilCashTotal util = new UtilCashTotal();
            ResultOptimal resultOptimal = util.recursiveFindeCashs(walletList, partialList, totalAmount);

            if (resultOptimal.remain == 0) {
                String st = "";
                for (CashTotal item : resultOptimal.listPartial) {
                    st += item.getParValue() + ",";

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
                    //valueListCashTake.add(item);
                }
                if (st.length() > 0) {
                    st = "Array Transfer = [" + st.substring(0, st.length() - 1) + "]";
                }
                //textView.setText(st);
                String userName = ECashApplication.getAccountInfo().getUsername();
                AccountInfo accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
                CashChangeHandler cashChangeHandler = new CashChangeHandler(ECashApplication.getInstance(), this);
                showDialogConfirmPayment(valueListCashChange, payment);
                // getPublicKeyOrganization(payment);
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
                }
                if (stTranfer.length() > 0) {
                    stTranfer = "Array Transfer = [" + stTranfer.substring(0, stTranfer.length() - 1) + "]";
                }
                // textView.setText(stExpect + "\n\n" + stEchange + "\n\n" + stTranfer);
                //  convertCash()
               // textView.setText(stExpect + "\n\n" + stEchange + "\n\n" + stTranfer);
              //  convertCash()
                getPublicKeyOrganization(this.payment);
            }

        } else {//balanceEcash<totalAmount
            Log.e("case 1", "case 1");
            dismissLoading();
            showDialogCannotPayment();
        }

        //
    }

    private void getPublicKeyOrganization(Payments payments) {
        String userName = ECashApplication.getAccountInfo().getUsername();
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        CashChangeHandler cashChangeHandler = new CashChangeHandler(ECashApplication.getInstance(), this);
        cashChangeHandler.getPublicKeyOrganization(accountInfo, new PublicKeyOrganization() {
            @Override
            public void getPublicKeyOrganization(String publicKey) {
                //   Log.e("publicKey ",publicKey);
                if (publicKey != null && publicKey.length() > 0) {
                    // getCashConvert(accountInfo,cashChangeHandler, payments, publicKey);
                    getListCashSend();
                    getListCashTake();
                    convertCash(cashChangeHandler, publicKey, accountInfo, payments);
                }
            }
        });
    }

    private List<Integer> listQualitySend = new ArrayList<>();
    private List<Integer> listValueSend = new ArrayList<>();

    private List<Integer> listQualityTake = new ArrayList<>();
    private List<Integer> listValueTake = new ArrayList<>();

    private List<CashTotal> valueListCashChange = new ArrayList<>();
    private List<CashTotal> valueListCashTake = new ArrayList<>();

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


    private void convertCash(CashChangeHandler cashChangeHandler, String keyPublicReceiver, AccountInfo accountInfo, Payments payments) {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
              //   Log.e("valueListCashChange.",valueListCashChange.get(i).getParValue()+" - ");
                Log.e("valueListCashChange.", valueListCashChange.get(i).getParValue() + " - ");
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
            dismissLoading();
            if (getActivity() != null)
                showDialogError("không lấy được endCrypt data và ID");
            return;
        }

        cashChangeHandler.requestChangeCash(encData, listQualityTake, accountInfo, listValueTake, new CashChangeSuccess() {
            @Override
            public void changeCashSuccess(CashInResponse cashInResponse) {
                if (null != getActivity()) {
                    getActivity().startService(new Intent(getActivity(), SyncCashService.class));
                }
                DatabaseUtil.saveCashOut(cashInResponse.getId(), listCashSend, getActivity(), accountInfo.getUsername());
                Gson gson = new Gson();
                String jsonCashInResponse = gson.toJson(cashInResponse);
                CacheData_Database cacheData_database = new CacheData_Database();
                cacheData_database.setTransactionSignature(cashInResponse.getId());
                cacheData_database.setResponseData(jsonCashInResponse);
                cacheData_database.setType(TYPE_CASH_EXCHANGE);
                DatabaseUtil.saveCacheData(cacheData_database, getActivity());
                Log.e("A","A");
                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_CASH_IN_CHANGE));
                //validatePayment(payments);
                dismissLoading();
            }
        });
        //  dismissLoading();
    }

    private void handleToPay(List<CashTotal> listCash, Payments payToRequest) {
        showLoading();
        ArrayList<Contact> listContact = new ArrayList<>();
        Contact contact = new Contact();
        contact.setWalletId(Long.parseLong(payToRequest.getSender()));
        listContact.add(contact);
        ToPayFuntion toPayFuntion = new ToPayFuntion(getActivity(), listCash, contact, payToRequest);
        toPayFuntion.handlePayToSocket(new ToPayListener() {
            @Override
            public void onToPaySuccess() {
                dismissLoading();

                showDialogPaymentSuccess(payToRequest);
            }
        });
    }

}
