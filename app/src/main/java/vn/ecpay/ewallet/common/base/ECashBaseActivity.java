package vn.ecpay.ewallet.common.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Stack;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.keystore.KSDeCrypt;
import vn.ecpay.ewallet.common.keystore.KSEnCrypt;
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.LanguageUtils;
import vn.ecpay.ewallet.model.language.LanguageObject;

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
            //show dialog
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

//        TextView textView = (TextView) progressDialog.findViewById(R.id.progress_message);
//        textView.setText(msg);

        if (!progressDialog.isShowing()) {
            runOnUiThread(() -> progressDialog.show());
        }
    }

    public void dismissLoading() {
        if (!isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    progressDialog = null;
                });
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

    public void clearStack() {
        int count = getFragmentStack().size();
        getFragmentStack().clear();
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

    public boolean isCloseKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    public void showDialogError(String messenger) {
        Snackbar snackbar = Snackbar
                .make(getWindow().getDecorView().findViewById(android.R.id.content), messenger, Snackbar.LENGTH_LONG);
        final ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            ((CoordinatorLayout.LayoutParams) params).gravity = Gravity.TOP;
        } else {
            ((FrameLayout.LayoutParams) params).gravity = Gravity.TOP;
        }
        snackbar.getView().setLayoutParams(params);

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));

        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.setDuration(3000);
        textView.setMaxLines(3);
        snackbar.show();
        dismissLoading();
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

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
