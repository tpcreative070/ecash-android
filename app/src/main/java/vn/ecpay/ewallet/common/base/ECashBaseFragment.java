package vn.ecpay.ewallet.common.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import butterknife.ButterKnife;
import vn.ecpay.ewallet.common.utils.DialogUtil;
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

    public void showDialogPaymentSuccess(String amount,String eCashID){// todo: check Object  or input: amount,ecash id
        DialogUtil.getInstance().showDialogPaymentSuccess(getActivity(),amount,eCashID, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
            }
        });
    }
    public void showDialogNewPayment(String amount,String eCashID){// todo: check Object  or input: amount,ecash id
        DialogUtil.getInstance().showDialogPaymentRepuest(getActivity(),amount,eCashID, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                checkCashInvalidToPaywment();
            }
        });
    }
    public void showDialogConfirmPayment(List<CashTotal> valueListCash,String amount, String eCashID){// todo: check Object or input: amount,ecash id
        DialogUtil.getInstance().showDialogConfirmPayment(getActivity(),valueListCash,amount,eCashID, new DialogUtil.OnResult() {
            @Override
            public void OnListenerOk() {
                // todo: check status
                showDialogPaymentSuccess("150000","1213244");// success
                // unsuccess: show dialog fail
            }
        });
    }
    public void showDialogCannotpayment(){
        DialogUtil.getInstance().showDialogCannotPayment(getActivity());
    }
    public void checkCashInvalidToPaywment(){
        // todo: Query database
        // todo: case 1:
       // showDialogCannotpayment();
        // todo: case 2:
         List<CashTotal> valueListCashTake = new ArrayList<>();
        CashTotal cashTotal = new CashTotal();
        cashTotal.setParValue(100000);
        cashTotal.setTotal(100000);
        cashTotal.setTotalDatabase(1);
        valueListCashTake.add(cashTotal);
        cashTotal.setParValue(50000);
        cashTotal.setTotal(50000);
        cashTotal.setTotalDatabase(2);
        valueListCashTake.add(cashTotal);
        showDialogConfirmPayment(valueListCashTake,"150000","1213244");
    }
    /**
     * Layout Res ID.
     *
     * @return Layout res id
     */
    protected abstract int getLayoutResId();

}
