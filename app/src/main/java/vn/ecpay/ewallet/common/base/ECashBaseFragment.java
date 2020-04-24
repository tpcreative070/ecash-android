package vn.ecpay.ewallet.common.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.NoSuchElementException;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.common.utils.DialogUtil;

public abstract class ECashBaseFragment extends Fragment {
    private static final String TAG = "ECashBaseFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);
        injectViews(view);
        setView(view, getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void injectViews(View view) {
        ButterKnife.bind(this, view);

    }

    protected void setView(View view, Activity activity) {
        ((ECashBaseActivity) getActivity()).setupUI(view, activity);
    }

    public void showProgress() {
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).showLoading();
        }
    }

    protected void showProgress(int resId) {
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).showLoading();
        }
    }

    protected void showProgress(String msg) {
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).showLoading();
        }
    }

    public void dismissProgress() {
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).dismissLoading();
        }
    }
    protected void showDialogError(String msg) {
        if (getActivity() instanceof ECashBaseActivity) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), msg);
        }
    }
    protected void showDialogSuccess(String msg){
        if (getActivity() instanceof ECashBaseActivity) {
            DialogUtil.getInstance().showDialogSuccess(getActivity(), msg);
        }

    }
    protected Fragment getCurrentFragment() {
        try {
            return ((ECashBaseActivity)getActivity()).getCurrentFragment();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    protected String getCurrentActivity() {
        return ((ECashBaseActivity)getActivity()).getCurrentActivity();
    }
    protected AppCompatActivity getBaseActivity() {
        return ((ECashBaseActivity)getActivity()).getBaseActivity();
    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        return super.onGetLayoutInflater(savedInstanceState);
    }

    /**
     * Layout Res ID.
     *
     * @return Layout res id
     */
    protected abstract int getLayoutResId();


    public void restartSocket(){
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).restartSocket();
        }
    }
    public void stopSocket(){
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).stopSocket();
        }
    }
    public void getPaymentDataBase(){
        if(getActivity() instanceof ECashBaseActivity){
            ((ECashBaseActivity)getActivity()).getPaymentDataBase();
        }
    }
    public void showDialogPermissions(String title){
        DialogUtil.getInstance().showDialogSettingPermissions(getActivity(),title, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getBaseActivity().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}
