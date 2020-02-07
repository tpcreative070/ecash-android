package vn.ecpay.ewallet.common.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
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
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.edongToEcash.response.CashInResponse;
import vn.ecpay.ewallet.model.payment.CashValid;
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.ui.cashChange.CashChangeHandler;
import vn.ecpay.ewallet.ui.cashChange.component.CashChangeSuccess;
import vn.ecpay.ewallet.ui.cashChange.component.PublicKeyOrganization;
import vn.ecpay.ewallet.ui.cashChange.module.CashChangeModule;
import vn.ecpay.ewallet.ui.cashChange.presenter.CashChangePresenter;
import vn.ecpay.ewallet.ui.cashChange.view.CashChangeView;
import vn.ecpay.ewallet.ui.function.CashInFunction;
import vn.ecpay.ewallet.ui.function.ToPayFuntion;
import vn.ecpay.ewallet.ui.interfaceListener.ToPayListener;

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

    private void showDialogCannotPayment() {
        DialogUtil.getInstance().showDialogCannotPayment(this);
    }

    public void showDialogPaymentSuccess(Payments payToRequest) {
        DialogUtil.getInstance().showDialogPaymentSuccess(this, payToRequest, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
            }
        });
    }

    public void showDialogNewPaymentRequest(Payments payment) {
        DialogUtil.getInstance().showDialogPaymentRepuest(this, payment, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                validatePayment(payment);
            }
        });
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
        showProgressDialog();
        long balanceEcash = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        long balanceEdong = 0;

        ArrayList<EdongInfo> listEDongInfo = ECashApplication.getListEDongInfo();
        if (null != listEDongInfo) {
            if (listEDongInfo.size() > 0) {
                balanceEdong = CommonUtils.getMoneyEdong(listEDongInfo.get(0).getUsableBalance());
            }
        }
        long totalAmount = Long.parseLong(payment.getTotalAmount());

        //-------
        if (balanceEcash >= totalAmount) {
            //Log.e("case 1","hop le, check list money");
            CashValid cashValid =checkListECashInvalidate(totalAmount);
            if(cashValid!=null){
                if(cashValid.getListCashValid().size()>0&&cashValid.getCashRemain()==0){
                    dismissLoading();
                    showDialogConfirmPayment(cashValid.getListCashValid(),payment);
                }else if(cashValid.getCashRemain()>0){
                    getPublicKeyOrganization(payment,cashValid);
                }
            }else{
                dismissLoading();
                showDialogError(getActivity().getString(R.string.str_have_warning));
            }
        } else if (balanceEcash < totalAmount) {
            dismissLoading();
            if (balanceEdong + balanceEcash < totalAmount) {
                Log.e("case 2", "case 2");
                showDialogCannotPayment();
            } else if (balanceEdong + balanceEcash >= totalAmount) {
                // Log.e("case 3","nạp ecash");
                showDialogError("nạp ecash");
                //checkListECashInvalidate(totalAmount);
            }
        } else if (balanceEdong + balanceEcash < totalAmount) {
            Log.e("case 4", "case 4");
            dismissLoading();
            showDialogCannotPayment();
        }
        //
    }

    private void getPublicKeyOrganization(Payments payments, CashValid cashValid){
        String userName = ECashApplication.getAccountInfo().getUsername();
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        CashChangeHandler cashChangeHandler = new CashChangeHandler(ECashApplication.getInstance(),this);
        cashChangeHandler.getPublicKeyOrganization(accountInfo, new PublicKeyOrganization() {
            @Override
            public void getPublicKeyOrganization(String publicKey) {
                Log.e("publicKey ",publicKey);
                if(publicKey!=null&&publicKey.length()>0){
                    getCashConvert(accountInfo,cashChangeHandler, payments,  cashValid, publicKey);
                }else{
                    dismissLoading();
                }
            }
        });
    }
    private List<Integer> listQualitySend = new ArrayList<>();
    private List<Integer> listValueSend =new ArrayList<>();

    private List<Integer> listQualityTake= new ArrayList<>();
    private List<Integer> listValueTake= new ArrayList<>();

    private List<CashTotal> valueListCashChange = new ArrayList<>();
    private List<CashTotal>  valueListCashTake = new ArrayList<>();

    private void getCashConvert(AccountInfo accountInfo,CashChangeHandler cashChangeHandler,Payments payments, CashValid cashValid,String PublicKeyOrganization){

                if(cashValid.getListCashRemain().size()>0){
                    Collections.reverse(cashValid.getListCashRemain());
                    int castRemain =cashValid.getCashRemain();
                    for(CashTotal cashTotal:cashValid.getListCashRemain()){
                        if(castRemain<cashTotal.getParValue()&&cashTotal.getTotal()<cashTotal.getTotalDatabase()){
                            if(cashTotal.getParValue()%castRemain==0){
                                int total =cashTotal.getParValue()/castRemain;

                                cashTotal.setTotal(cashTotal.getTotal()+1);

                                valueListCashChange.add(cashTotal);

                                CashTotal cash = new CashTotal();
                                cash.setParValue(castRemain);
                                cash.setTotal(total);
                                valueListCashTake.add(cash);
                               // valueListCashTake.add(cashTotal);
                                break;
                            }
                        }
                    }
                }else{
                    showDialogError(getActivity().getString(R.string.str_have_warning) +cashValid.getCashRemain()+"");
                }
                if(valueListCashChange.size()>0&&valueListCashTake.size()>0){
                    getListCashSend();
                    getListCashTake();

                    convertCash(cashChangeHandler,PublicKeyOrganization,accountInfo,payments);

                }


      //  showDialogError("đổi ecash " +cashValid.getCashRemain()+"");

    }
    private void getListCashSend() {
        listQualitySend = new ArrayList<>();
        listValueSend = new ArrayList<>();
        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
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
                listQualityTake.add(valueListCashTake.get(i).getTotal());
                listValueTake.add(valueListCashTake.get(i).getParValue());
            }
        }
    }
    private void convertCash(CashChangeHandler cashChangeHandler,String keyPublicReceiver,AccountInfo accountInfo,Payments payments){
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        for (int i = 0; i < valueListCashChange.size(); i++) {
            if (valueListCashChange.get(i).getTotal() > 0) {
                Log.e("valueListCashChange.",valueListCashChange.get(i).getParValue()+" - ");
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
        // listQualityTake =5
        // listValueTake =2000
        for(int i=0;i<listQualityTake.size();i++){
            Log.e("listQualityTake. ",listQualityTake.get(i).toString()+"");//10000
        }
        for(int i=0;i<listValueTake.size();i++){
            Log.e("listValueTake. ",listValueTake.get(i).toString()+"");//5
        }
        cashChangeHandler.requestChangeCash(encData, listQualityTake, accountInfo, listValueTake, new CashChangeSuccess() {
            @Override
            public void changeCashSuccess(CashInResponse cashInResponse) {
                DatabaseUtil.saveCashOut(cashInResponse.getId(), listCashSend, getActivity(), accountInfo.getUsername());
                Gson gson = new Gson();
                String jsonCashInResponse = gson.toJson(cashInResponse);
                CacheData_Database cacheData_database = new CacheData_Database();
                cacheData_database.setTransactionSignature(cashInResponse.getId());
                cacheData_database.setResponseData(jsonCashInResponse);
                cacheData_database.setType(TYPE_CASH_EXCHANGE);
                DatabaseUtil.saveCacheData(cacheData_database, getActivity());
                validatePayment(payments);
            }
        });

    }

    private CashValid checkListECashInvalidate(long totalAmount) {
        List<CashTotal> cashTotalList = DatabaseUtil.getAllCashTotal(getActivity());
        Collections.reverse(cashTotalList);
        //CommonUtils.handleGetCash(cashTotalList);
        return CommonUtils.getCashForPayment(cashTotalList,totalAmount);
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
