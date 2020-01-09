package vn.ecpay.ewallet.common.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.ui.account.AccountActivity;

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

    public void showDialogPaymentSuccess(String title,String amount,String eCashID){// todo: check Object  input: amount,ecash id
        DialogUtil.getInstance().showDialogPaymentSuccess(getActivity(),title,amount,eCashID, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
            }
        });
    }
    public void showDialogNewPayment(String amount,String eCashID){// todo: check Object  input: amount,ecash id
        DialogUtil.getInstance().showDialogNewpayment(getActivity(),amount,eCashID, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                showDialogViewPaymentInfo( amount, eCashID);
            }
        });
    }
    public void showDialogViewPaymentInfo(String amount,String eCashID){// todo: check Object  input: amount,ecash id
        DialogUtil.getInstance().showDialogViewPaymentInfo(getActivity(),amount,eCashID, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                // todo: check status
                showDialogPaymentSuccess(getString(R.string.str_payment_success),"150000","1213244");
            }
        });
    }
    /**
     * Layout Res ID.
     *
     * @return Layout res id
     */
    protected abstract int getLayoutResId();

}
