package vn.ecpay.ewallet.common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.cashValue.CashTotal;

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

    protected void showProgress() {
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

    protected void dismissProgress() {
        if (getActivity() instanceof ECashBaseActivity) {
            ((ECashBaseActivity) getActivity()).dismissLoading();
        }
    }
    protected void showDialogError(String msg) {
        if (getActivity() instanceof ECashBaseActivity) {
            DialogUtil.getInstance().showDialogWarning(getActivity(), msg);
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

    @SuppressLint("StaticFieldLeak")
    public void saveImageQRCode(Bitmap bitmap, String name){
        if (PermissionUtils.checkPermissionWriteStore(this, null)) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File mFolder = new File(root + "/qr_my_account");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    String imageName = name + ".jpg";
                    File file = new File(mFolder, imageName);
                    if (file.exists())
                        file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    dismissProgress();
                    Toast.makeText(getActivity(), getResources().getString(R.string.str_save_to_device_success), Toast.LENGTH_LONG).show();
                }
            }.execute();
        }

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
}
