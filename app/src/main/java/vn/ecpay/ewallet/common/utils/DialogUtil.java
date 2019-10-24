package vn.ecpay.ewallet.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import vn.ecpay.ewallet.R;

public class DialogUtil {
    private static DialogUtil mInsance;
    private Dialog mDialog;
    private int solan = 1;
    View view;

    public interface OnConfirm {
        void OnListenerOk();

        void OnListenerCancel();
    }

    public interface OnCancelAccount {
        void OnListenerTransferCash();

        void OnListenerCashOut();
    }

    public interface OnResult {
        void OnListenerOk();
    }

    public interface OnConfirmOTP {
        void onSuccess(String otp);

        void onRetryOTP();

        void onCancel();
    }

    public static DialogUtil getInstance() {
        /*
         * loked mInstance
         * Avoid singlton fail from multi thread
         * */
        if (mInsance == null)
            mInsance = new DialogUtil();
        return mInsance;
    }

    public void release() {
        // TODO:
        mDialog = null;
        solan = 1;
        view = null;
    }

    private void initDialog(Context pContext) {
        mDialog = new Dialog(pContext, R.style.DialogFragment);
    }

    public void showDialogConfirm(Context pContext, int message, final OnConfirm pOnConfirm) {
        showDialogConfirm(pContext, pContext.getResources().getString(R.string.str_dialog_notifi_title), pContext.getResources().getString(message), pOnConfirm);
    }

    public void showDialogConfirm(Context pContext, String message, final OnConfirm pOnConfirm) {
        showDialogConfirm(pContext, pContext.getResources().getString(R.string.str_dialog_notifi_title), message, pOnConfirm);
    }

    public void showDialogConfirm(Context pContext, String title, String message, final OnConfirm pOnConfirm) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_confirm);
            Button btnOk, btnCancel;
            TextView tvTitle, tvMessage;
            btnOk = mDialog.findViewById(R.id.btnOk);
            btnCancel = mDialog.findViewById(R.id.btnCancel);
            tvTitle = mDialog.findViewById(R.id.tvTitle);
            tvMessage = mDialog.findViewById(R.id.tvContent);
            tvTitle.setText(title);
            tvMessage.setText(message);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                    if (pOnConfirm != null) {
                        pOnConfirm.OnListenerOk();
                    }

                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                    if (pOnConfirm != null) {
                        pOnConfirm.OnListenerCancel();
                    }
                }
            });
        }
    }

    public void showDialogChangePassSuccess(Context pContext, String title, String message, final OnResult pOnConfirm) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_confirm_change_pass_success);
            Button btnOk;
            TextView tvTitle, tvMessage;
            btnOk = mDialog.findViewById(R.id.btnOk);
            tvTitle = mDialog.findViewById(R.id.tvTitle);
            tvMessage = mDialog.findViewById(R.id.tvContent);
            tvTitle.setText(title);
            tvMessage.setText(message);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            btnOk.setOnClickListener(v -> {
                dismissDialog();
                if (pOnConfirm != null) {
                    pOnConfirm.OnListenerOk();
                }

            });
        }
    }

    public void showDialogCancelAccountSuccess(Context pContext, String title, String message, final OnResult pOnConfirm) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_cancel_account_success);
            Button btnOk;
            btnOk = mDialog.findViewById(R.id.btn_go_home);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            btnOk.setOnClickListener(v -> {
                dismissDialog();
                if (pOnConfirm != null) {
                    pOnConfirm.OnListenerOk();
                }

            });
        }
    }

    public void showDialogCancelAccount(Context pContext, long balance, final OnCancelAccount onCancelAccount) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_cancel_account);

            Button btnCancel, btnTransferCash, btnCashOut;
            TextView tv_balance;

            tv_balance = mDialog.findViewById(R.id.tv_balance);
            btnCancel = mDialog.findViewById(R.id.btn_cancel);
            btnTransferCash = mDialog.findViewById(R.id.btn_transfer_cash);
            btnCashOut = mDialog.findViewById(R.id.btn_cash_out);

            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            tv_balance.setText(CommonUtils.formatPriceVND(balance));
            btnTransferCash.setOnClickListener(v -> {
                dismissDialog();
                if (onCancelAccount != null) {
                    onCancelAccount.OnListenerTransferCash();
                }

            });
            btnCashOut.setOnClickListener(v -> {
                dismissDialog();
                if (onCancelAccount != null) {
                    onCancelAccount.OnListenerCashOut();
                }
            });
            btnCancel.setOnClickListener(v -> dismissDialog());
        } else {
            dismissDialog();
            showDialogCancelAccount(pContext, balance, onCancelAccount);
        }
    }

    public void showDialogFail(Context pContext, String title, String todo, String money,
                               String stk, final OnResult onResult) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_result);
            Button btnComeHome;
            TextView tvTitle, tvDo, tvMoney, tvSTK;
            btnComeHome = mDialog.findViewById(R.id.btn_come_home);
            tvTitle = mDialog.findViewById(R.id.tv_title);
            tvDo = mDialog.findViewById(R.id.tv_do);
            tvMoney = mDialog.findViewById(R.id.tv_money);
            tvSTK = mDialog.findViewById(R.id.tv_stk);

            tvTitle.setText(title);
            tvDo.setText(todo);
            tvMoney.setText(money);
            tvSTK.setText(stk);

            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();
            btnComeHome.setOnClickListener(v -> dismissDialog());
        }
    }

    public void showDialogLogout(Context pContext, final OnResult onResult) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_logout);
            Button btnOk, btnCancel;

            btnOk = mDialog.findViewById(R.id.btn_ok);
            btnCancel = mDialog.findViewById(R.id.btn_cancel);

            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();
            btnOk.setOnClickListener(v -> {
                dismissDialog();
                if (onResult != null) {
                    onResult.OnListenerOk();
                }
            });

            btnCancel.setOnClickListener(v -> dismissDialog());
        }
    }

    public void showDialogInputOTP(final Context pContext, String sdt, String error, String txtInput, final OnConfirmOTP onConfirmOTP) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_confirm_otp);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            TextView tvSDT = mDialog.findViewById(R.id.tvSDT);
            tvSDT.setText(sdt);
            final EditText edtCodeOTP = mDialog.findViewById(R.id.edtCodeOTP);
            final TextView tvError = mDialog.findViewById(R.id.txtError);
            if (!error.equals("")) {
                tvError.setVisibility(View.VISIBLE);
                edtCodeOTP.setText(txtInput);
                tvError.setText(error);
            } else tvError.setVisibility(View.GONE);
            edtCodeOTP.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (s.length() != 0) {
                        tvError.setVisibility(View.GONE);
                    }
                }
            });
            final Button btnDone = mDialog.findViewById(R.id.btnDone);
            btnDone.setOnClickListener(v -> {
                if (onConfirmOTP != null && !edtCodeOTP.getText().toString().equals("")) {
                    dismissDialog();
                    Utils.isClick(btnDone);
                    onConfirmOTP.onSuccess(edtCodeOTP.getText().toString());
                } else {
                    tvError.setText("Bạn chưa nhập mã OTP");
                    tvError.setVisibility(View.VISIBLE);
                }

            });
            final TextView txtRetryOTP = mDialog.findViewById(R.id.txtRetryOTP);
            txtRetryOTP.setOnClickListener(v -> {
                dismissDialog();
                Utils.isClick(txtRetryOTP);
                if (onConfirmOTP != null) {
                    onConfirmOTP.onRetryOTP();
                }
            });
            Button btnClose = mDialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(v -> {
                dismissDialog();
                if (onConfirmOTP != null) {
                    onConfirmOTP.onCancel();
                }
            });
            mDialog.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) pContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
            mDialog.show();
            Window window = mDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }


    public void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            release();
        }
    }

    public boolean isShowing() {
        if (mDialog == null)
            return false;
        return mDialog.isShowing();
    }
}
