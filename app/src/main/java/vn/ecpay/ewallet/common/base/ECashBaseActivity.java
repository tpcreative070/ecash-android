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
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Stack;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.LanguageUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.payTo.PayToRequest;
import vn.ecpay.ewallet.ui.function.ToPayFuntion;
import vn.ecpay.ewallet.ui.interfaceListener.ToPayListener;

import static vn.ecpay.ewallet.ECashApplication.getActivity;

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
    public void checkAmountValidate(Long amount){
        Long balance = (long) (WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT));
        if(balance<amount){
            showDialogCannotPayment();
        }else{
            Toast.makeText(this, "toto check money", Toast.LENGTH_SHORT).show();
        }

    }
    private void showDialogCannotPayment(){
        DialogUtil.getInstance().showDialogCannotPayment(this);
    }

    public void showDialogPaymentSuccess(PayToRequest payToRequest){
        DialogUtil.getInstance().showDialogPaymentSuccess(this, payToRequest, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                dismissLoading();
            }
        });
    }
    public void showDialogNewPaymentRequest(PayToRequest payToRequest){
        DialogUtil.getInstance().showDialogPaymentRepuest(this,payToRequest, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                checkCashInvalidToPayment(payToRequest);
            }
        });
    }
    public void showDialogConfirmPayment(List<CashTotal> valueListCash, PayToRequest payToRequest){
        DialogUtil.getInstance().showDialogConfirmPayment(this,valueListCash,payToRequest, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                handleToPay(valueListCash,payToRequest);
            }
        });
    }

    public void checkCashInvalidToPayment(PayToRequest payToRequest){
        //showDialogConfirmPayment(valueListCashTake,"150000","1213244");
       long balanceEcash = WalletDatabase.getTotalCash(Constant.STR_CASH_IN) - WalletDatabase.getTotalCash(Constant.STR_CASH_OUT);
        long totalAmount =Long.parseLong(payToRequest.getTotalAmount());
        long balanceEdong=0;
        ArrayList<EdongInfo>  listEDongInfo = ECashApplication.getListEDongInfo();
        if (null != listEDongInfo) {
            if (listEDongInfo.size() > 0) {
                balanceEdong =CommonUtils.getMoneyEdong(listEDongInfo.get(0).getUsableBalance());
            }
        }
        //
        if(balanceEcash>=totalAmount){
            Log.e("case 1","hop le, check list money");
            if(checkListECashInvalidate(totalAmount)!=null&&checkListECashInvalidate(totalAmount).size()>0){
                showDialogConfirmPayment(checkListECashInvalidate(totalAmount),payToRequest);
            }else{
                Log.e("case 1.1","đổi ecash");
            }
        }
        else if(balanceEcash<totalAmount){
            if(balanceEdong+balanceEcash<totalAmount){
                Log.e("case 2","case 2");
                showDialogCannotPayment();
            }
          else if(balanceEdong+balanceEcash>=totalAmount){
                Log.e("case 3","nạp ecash");
                //checkListECashInvalidate(totalAmount);
            }
        }
       else if(balanceEdong+balanceEcash<totalAmount){
            Log.e("case 4","case 4");
            showDialogCannotPayment();
        }

    }
    private List<CashTotal> checkListECashInvalidate(long totalAmount){
        List<CashTotal> cashTotalList = DatabaseUtil.getAllCashTotal(getActivity());
        List<CashTotal> list = new ArrayList<>();
        Collections.reverse(cashTotalList);
        for(CashTotal cashTotal: cashTotalList){
//            Log.e("cashTotal getParValue()",cashTotal.getParValue()+"");
//            Log.e("cashTotal getTotal()",cashTotal.getTotal()+"");
//            Log.e("cashTotal getTotalDatabase()",cashTotal.getTotalDatabase()+"");
            //Log.e("div ",cashTotal.getParValue()* cashTotal.getTotalDatabase()%totalAmount+"");
            if(cashTotal.getParValue()==totalAmount){
                cashTotal.setTotal(1);
                list.add(cashTotal);
                return list;
            }else if((cashTotal.getParValue()* cashTotal.getTotalDatabase())%totalAmount==0){
                for(int j=1;j<=cashTotal.getTotalDatabase();j++){
                    if(cashTotal.getParValue()*j==totalAmount){ CashTotal cash = new CashTotal();
                        cash.setParValue(cashTotal.getParValue());
                        cash.setTotalDatabase(cashTotal.getTotalDatabase());
                        cash.setTotal(j);
                        list.add(cash);
                        return list;
                    }
                }

            }else{
                return list;
            }
            // if(cashTotal.getTotalDatabase())
        }
        return list;
    }
    private void handleToPay(List<CashTotal> listCash,PayToRequest payToRequest){
        showLoading();
        ArrayList<Contact> listContact = new ArrayList<>();
        Contact contact = new Contact();
        contact.setWalletId(Long.parseLong(payToRequest.getSender()));
        listContact.add(contact);
        ToPayFuntion toPayFuntion =new ToPayFuntion(getActivity(),listCash,payToRequest);
        toPayFuntion.handlePayToSocket(new ToPayListener() {
            @Override
            public void onToPaySuccess() {
                showDialogPaymentSuccess(payToRequest);
            }
        });
    }

}
