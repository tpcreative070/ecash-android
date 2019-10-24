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
import android.widget.ImageView;
import android.widget.TextView;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;

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

    public interface OnResultChoseCash {
        void OnListenerOk(int sl500, int sl200, int sl100, int sl50, int sl20, int sl10);
    }

    public interface OnConfirmOTP {
        void onSuccess(String otp);

        void onRetryOTP();

        void onCancel();
    }

    public static DialogUtil getInstance() {
        return new DialogUtil();
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


    int slDatabase500, slDatabase200, slDatabase100, slDatabase50, slDatabase20, slDatabase10;
    int total500 = 0, total200 = 0, total100 = 0, total50 = 0, total20 = 0, total10 = 0;
    private long totalMoney = 0;

    public void showDialogChoseCash(boolean isChange, Context pContext, AccountInfo accountInfo, String title, final OnResultChoseCash onResultChoseCash) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_chose_cash);
            Button btnOk;
            TextView tvTotalMoney, tvTitle;
            TextView tvSl500, tvSl200, tvSl100, tvSl50, tvSl20, tvSl10;
            TextView tvTotal500, tvTotal200, tvTotal100, tvTotal50, tvTotal20, tvTotal10;
            ImageView ivDown500, ivDown200, ivDown100, ivDown50, ivDown20, ivDown10;
            ImageView ivUp500, ivUp200, ivUp100, ivUp50, ivUp20, ivUp10;

            tvTitle = mDialog.findViewById(R.id.tv_title);
            tvTotalMoney = mDialog.findViewById(R.id.tv_total_money);

            tvTotal500 = mDialog.findViewById(R.id.tv_total_500);
            tvTotal200 = mDialog.findViewById(R.id.tv_total_200);
            tvTotal100 = mDialog.findViewById(R.id.tv_total_100);
            tvTotal50 = mDialog.findViewById(R.id.tv_total_50);
            tvTotal20 = mDialog.findViewById(R.id.tv_total_20);
            tvTotal10 = mDialog.findViewById(R.id.tv_total_10);

            btnOk = mDialog.findViewById(R.id.btn_confirm);
            tvSl500 = mDialog.findViewById(R.id.tv_sl_500);
            tvSl200 = mDialog.findViewById(R.id.tv_sl_200);
            tvSl100 = mDialog.findViewById(R.id.tv_sl_100);
            tvSl50 = mDialog.findViewById(R.id.tv_sl_50);
            tvSl20 = mDialog.findViewById(R.id.tv_sl_20);
            tvSl10 = mDialog.findViewById(R.id.tv_sl_10);

            ivDown500 = mDialog.findViewById(R.id.iv_down_500);
            ivDown200 = mDialog.findViewById(R.id.iv_down_200);
            ivDown100 = mDialog.findViewById(R.id.iv_down_100);
            ivDown50 = mDialog.findViewById(R.id.iv_down_50);
            ivDown20 = mDialog.findViewById(R.id.iv_down_20);
            ivDown10 = mDialog.findViewById(R.id.iv_down_10);

            ivUp500 = mDialog.findViewById(R.id.iv_up_500);
            ivUp200 = mDialog.findViewById(R.id.iv_up_200);
            ivUp100 = mDialog.findViewById(R.id.iv_up_100);
            ivUp50 = mDialog.findViewById(R.id.iv_up_50);
            ivUp20 = mDialog.findViewById(R.id.iv_up_20);
            ivUp10 = mDialog.findViewById(R.id.iv_up_10);


            WalletDatabase.getINSTANCE(pContext, ECashApplication.masterKey);
            slDatabase500 = WalletDatabase.getTotalMoney("500000", Constant.STR_CASH_IN);
            slDatabase200 = WalletDatabase.getTotalMoney("200000", Constant.STR_CASH_IN);
            slDatabase100 = WalletDatabase.getTotalMoney("100000", Constant.STR_CASH_IN);
            slDatabase50 = WalletDatabase.getTotalMoney("50000", Constant.STR_CASH_IN);
            slDatabase20 = WalletDatabase.getTotalMoney("20000", Constant.STR_CASH_IN);
            slDatabase10 = WalletDatabase.getTotalMoney("10000", Constant.STR_CASH_IN);

            tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
            tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
            tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
            tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
            tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
            tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));

            tvTotal500.setText(String.valueOf(total500));
            tvTotal200.setText(String.valueOf(total200));
            tvTotal100.setText(String.valueOf(total100));
            tvTotal50.setText(String.valueOf(total50));
            tvTotal20.setText(String.valueOf(total20));
            tvTotal10.setText(String.valueOf(total10));

            if (isChange) {
                tvTitle.setText(pContext.getString(R.string.str_cash_change));
            } else {
                tvTitle.setText(pContext.getString(R.string.str_cash_take));
                tvTotal10.setVisibility(View.GONE);
                tvTotal20.setVisibility(View.GONE);
                tvTotal50.setVisibility(View.GONE);
                tvTotal100.setVisibility(View.GONE);
                tvTotal200.setVisibility(View.GONE);
                tvTotal500.setVisibility(View.GONE);
            }

            ivDown10.setOnClickListener(v -> {
                if (total10 > 0) {
                    total10 = total10 - 1;
                    slDatabase10 = slDatabase10 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });
            ivUp10.setOnClickListener(v -> {
                if (slDatabase10 > 0) {
                    total10 = total10 + 1;
                    slDatabase10 = slDatabase10 - 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });

            ivDown20.setOnClickListener(v -> {
                if (total20 > 0) {
                    total20 = total20 - 1;
                    slDatabase20 = slDatabase20 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });
            ivUp20.setOnClickListener(v -> {
                if (slDatabase20 > 0) {
                    total20 = total20 + 1;
                    slDatabase20 = slDatabase20 - 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });

            ivDown50.setOnClickListener(v -> {
                if (total50 > 0) {
                    total50 = total50 - 1;
                    slDatabase50 = slDatabase50 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });
            ivUp50.setOnClickListener(v -> {
                if (slDatabase50 > 0) {
                    total50 = total50 + 1;
                    slDatabase50 = slDatabase50 - 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });

            ivDown100.setOnClickListener(v -> {
                if (total100 > 0) {
                    total100 = total100 - 1;
                    slDatabase100 = slDatabase100 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });
            ivUp100.setOnClickListener(v -> {
                if (slDatabase100 > 0) {
                    total100 = total100 + 1;
                    slDatabase100 = slDatabase100 - 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });

            ivDown200.setOnClickListener(v -> {
                if (total200 > 0) {
                    total200 = total200 - 1;
                    slDatabase200 = slDatabase200 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });
            ivUp200.setOnClickListener(v -> {
                if (slDatabase200 > 0) {
                    total200 = total200 + 1;
                    slDatabase200 = slDatabase200 - 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });

            ivDown500.setOnClickListener(v -> {
                if (total500 > 0) {
                    total500 = total500 - 1;
                    slDatabase500 = slDatabase500 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });
            ivUp500.setOnClickListener(v -> {
                if (total500 > 0) {
                    total500 = total500 - 1;
                    slDatabase500 = slDatabase500 + 1;
                }
                totalMoney = total500 * 500000 + total200 * 200000 + total100 * 100000 + total50 * 50000 + total20 * 20000 + total10 * 10000;
                tvTotalMoney.setText(CommonUtils.formatPriceVND(totalMoney));

                tvTotal500.setText(String.valueOf(total500));
                tvTotal200.setText(String.valueOf(total200));
                tvTotal100.setText(String.valueOf(total100));
                tvTotal50.setText(String.valueOf(total50));
                tvTotal20.setText(String.valueOf(total20));
                tvTotal10.setText(String.valueOf(total10));

                tvSl500.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase500)));
                tvSl200.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase200)));
                tvSl100.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase100)));
                tvSl50.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase50)));
                tvSl20.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase20)));
                tvSl10.setText(pContext.getString(R.string.str_money, String.valueOf(slDatabase10)));
            });


            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            btnOk.setOnClickListener(v -> {
                dismissDialog();
                if (onResultChoseCash != null) {
                    onResultChoseCash.OnListenerOk(total500, total200, total100, total50, total20, total10);
                }
            });
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
