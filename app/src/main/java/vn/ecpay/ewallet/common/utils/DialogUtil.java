package vn.ecpay.ewallet.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.language.LanguageObject;

public class DialogUtil {
    private static DialogUtil mInsance;
    private Dialog mDialog;
    private int solan = 1;
    View view;

    public interface OnConfirm {
        void OnListenerOk();

        void OnListenerCancel();
    }

    public interface OnChangeLanguage {
        void OnListenerVn();

        void OnListenerEn();
    }

    public interface OnContactUpdate {
        void OnListenerOk(String name);
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
        showDialogConfirm(pContext, pContext.getResources().getString(R.string.str_dialog_notification_title), pContext.getResources().getString(message), pOnConfirm);
    }

    public void showDialogConfirm(Context pContext, String message, final OnConfirm pOnConfirm) {
        showDialogConfirm(pContext, pContext.getResources().getString(R.string.str_dialog_notification_title), message, pOnConfirm);
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
        } else {
            dismissDialog();
            showDialogLogout(pContext, onResult);
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
        } else {
            dismissDialog();
            showDialogInputOTP(pContext, sdt, error, txtInput, onConfirmOTP);
        }
    }


    int slDatabase500, slDatabase200, slDatabase100, slDatabase50, slDatabase20, slDatabase10;
    int total500 = 0, total200 = 0, total100 = 0, total50 = 0, total20 = 0, total10 = 0;
    private long totalMoney = 0;

    public void showDialogChangeCash(boolean isChange, Context pContext, AccountInfo accountInfo, String title, final OnResultChoseCash onResultChoseCash) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_chose_cash);
            Button btnOk;
            TextView tvTotalMoney, tvTitle;
            RelativeLayout layout500;
            RelativeLayout layout200;
            RelativeLayout layout100;
            RelativeLayout layout50;
            RelativeLayout layout20;
            RelativeLayout layout10;
            TextView tvSl500, tvSl200, tvSl100, tvSl50, tvSl20, tvSl10;
            TextView tvTotal500, tvTotal200, tvTotal100, tvTotal50, tvTotal20, tvTotal10;
            ImageView ivDown500, ivDown200, ivDown100, ivDown50, ivDown20, ivDown10;
            ImageView ivUp500, ivUp200, ivUp100, ivUp50, ivUp20, ivUp10;

            tvTitle = mDialog.findViewById(R.id.tv_title);
            tvTotalMoney = mDialog.findViewById(R.id.tv_total_money);

            layout500 = mDialog.findViewById(R.id.layout_500);
            layout200 = mDialog.findViewById(R.id.layout_200);
            layout100 = mDialog.findViewById(R.id.layout_100);
            layout50 = mDialog.findViewById(R.id.layout_50);
            layout20 = mDialog.findViewById(R.id.layout_20);
            layout10 = mDialog.findViewById(R.id.layout_10);

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
                if (slDatabase500 > 0) {
                    layout500.setVisibility(View.VISIBLE);
                } else {
                    layout500.setVisibility(View.GONE);
                }

                if (slDatabase200 > 0) {
                    layout200.setVisibility(View.VISIBLE);
                } else {
                    layout200.setVisibility(View.GONE);
                }

                if (slDatabase100 > 0) {
                    layout100.setVisibility(View.VISIBLE);
                } else {
                    layout100.setVisibility(View.GONE);
                }

                if (slDatabase50 > 0) {
                    layout50.setVisibility(View.VISIBLE);
                } else {
                    layout50.setVisibility(View.GONE);
                }

                if (slDatabase20 > 0) {
                    layout20.setVisibility(View.VISIBLE);
                } else {
                    layout20.setVisibility(View.GONE);
                }

                if (slDatabase10 > 0) {
                    layout10.setVisibility(View.VISIBLE);
                } else {
                    layout10.setVisibility(View.GONE);
                }
            } else {
                tvTitle.setText(pContext.getString(R.string.str_cash_take));
                tvSl10.setVisibility(View.GONE);
                tvSl20.setVisibility(View.GONE);
                tvSl50.setVisibility(View.GONE);
                tvSl100.setVisibility(View.GONE);
                tvSl200.setVisibility(View.GONE);
                tvSl500.setVisibility(View.GONE);

                layout500.setVisibility(View.VISIBLE);
                layout200.setVisibility(View.VISIBLE);
                layout100.setVisibility(View.VISIBLE);
                layout50.setVisibility(View.VISIBLE);
                layout20.setVisibility(View.VISIBLE);
                layout10.setVisibility(View.VISIBLE);
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
                if (isChange) {
                    if (slDatabase10 > 0) {
                        total10 = total10 + 1;
                        slDatabase10 = slDatabase10 - 1;
                    }
                } else {
                    total10 = total10 + 1;
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
                if (isChange) {
                    if (slDatabase20 > 0) {
                        total20 = total20 + 1;
                        slDatabase20 = slDatabase20 - 1;
                    }
                } else {
                    total20 = total20 + 1;
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
                if (isChange) {
                    if (slDatabase50 > 0) {
                        total50 = total50 + 1;
                        slDatabase50 = slDatabase50 - 1;
                    }
                } else {
                    total50 = total50 + 1;
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
                if (isChange) {
                    if (slDatabase100 > 0) {
                        total100 = total100 + 1;
                        slDatabase100 = slDatabase100 - 1;
                    }
                } else {
                    total100 = total100 + 1;
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
                if (isChange) {
                    if (slDatabase200 > 0) {
                        total200 = total200 + 1;
                        slDatabase200 = slDatabase200 - 1;
                    }
                } else {
                    total200 = total200 + 1;
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
                if (isChange) {
                    if (total500 > 0) {
                        total500 = total500 + 1;
                        slDatabase500 = slDatabase500 - 1;
                    }
                } else {
                    total500 = total500 + 1;
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

    public void showDialogConfirmChangeCash(int slSend500, int slSend200, int slSend100, int slSend50, int slSend20, int slSend10,
                                            int slTake500, int slTake200, int slTake100, int slTake50, int slTake20, int slTake10, Context pContext, OnResult onResult) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_confirm_change_cash);
            RelativeLayout layout_send_500, layout_send_200, layout_send_100, layout_send_50, layout_send_20, layout_send_10;
            RelativeLayout layout_take_500, layout_take_200, layout_take_100, layout_take_50, layout_take_20, layout_take_10;

            TextView tv_number_send_500, tv_number_send_200, tv_number_send_100, tv_number_send_50, tv_number_send_20, tv_number_send_10;
            TextView tv_number_take_500, tv_number_take_200, tv_number_take_100, tv_number_take_50, tv_number_take_20, tv_number_take_10;

            TextView tv_total_money_send, tv_total_money_take;

            Button btnConfirm;
            layout_send_500 = mDialog.findViewById(R.id.layout_send_500);
            layout_send_200 = mDialog.findViewById(R.id.layout_send_200);
            layout_send_100 = mDialog.findViewById(R.id.layout_send_100);
            layout_send_50 = mDialog.findViewById(R.id.layout_send_50);
            layout_send_20 = mDialog.findViewById(R.id.layout_send_20);
            layout_send_10 = mDialog.findViewById(R.id.layout_send_10);

            layout_take_500 = mDialog.findViewById(R.id.layout_500);
            layout_take_200 = mDialog.findViewById(R.id.layout_200);
            layout_take_100 = mDialog.findViewById(R.id.layout_100);
            layout_take_50 = mDialog.findViewById(R.id.layout_50);
            layout_take_20 = mDialog.findViewById(R.id.layout_20);
            layout_take_10 = mDialog.findViewById(R.id.layout_10);

            tv_number_send_500 = mDialog.findViewById(R.id.tv_number_send_500);
            tv_number_send_200 = mDialog.findViewById(R.id.tv_number_send_200);
            tv_number_send_100 = mDialog.findViewById(R.id.tv_number_send_100);
            tv_number_send_50 = mDialog.findViewById(R.id.tv_number_send_50);
            tv_number_send_20 = mDialog.findViewById(R.id.tv_number_send_20);
            tv_number_send_10 = mDialog.findViewById(R.id.tv_number_send_10);

            tv_number_take_500 = mDialog.findViewById(R.id.tv_number_take_500);
            tv_number_take_200 = mDialog.findViewById(R.id.tv_number_take_200);
            tv_number_take_100 = mDialog.findViewById(R.id.tv_number_take_100);
            tv_number_take_50 = mDialog.findViewById(R.id.tv_number_take_50);
            tv_number_take_20 = mDialog.findViewById(R.id.tv_number_take_20);
            tv_number_take_10 = mDialog.findViewById(R.id.tv_number_take_10);

            tv_total_money_send = mDialog.findViewById(R.id.tv_total_money_send);
            tv_total_money_take = mDialog.findViewById(R.id.tv_total_money_take);

            btnConfirm = mDialog.findViewById(R.id.btn_confirm);

            if (slSend500 > 0) {
                layout_send_500.setVisibility(View.VISIBLE);
            } else {
                layout_send_500.setVisibility(View.GONE);
            }

            if (slSend200 > 0) {
                layout_send_200.setVisibility(View.VISIBLE);
            } else {
                layout_send_200.setVisibility(View.GONE);
            }

            if (slSend100 > 0) {
                layout_send_100.setVisibility(View.VISIBLE);
            } else {
                layout_send_100.setVisibility(View.GONE);
            }

            if (slSend50 > 0) {
                layout_send_50.setVisibility(View.VISIBLE);
            } else {
                layout_send_50.setVisibility(View.GONE);
            }

            if (slSend20 > 0) {
                layout_send_20.setVisibility(View.VISIBLE);
            } else {
                layout_send_20.setVisibility(View.GONE);
            }

            if (slSend10 > 0) {
                layout_send_10.setVisibility(View.VISIBLE);
            } else {
                layout_send_10.setVisibility(View.GONE);
            }


            if (slTake500 > 0) {
                layout_take_500.setVisibility(View.VISIBLE);
            } else {
                layout_take_500.setVisibility(View.GONE);
            }

            if (slTake200 > 0) {
                layout_take_200.setVisibility(View.VISIBLE);
            } else {
                layout_take_200.setVisibility(View.GONE);
            }

            if (slTake100 > 0) {
                layout_take_100.setVisibility(View.VISIBLE);
            } else {
                layout_take_100.setVisibility(View.GONE);
            }

            if (slTake50 > 0) {
                layout_take_50.setVisibility(View.VISIBLE);
            } else {
                layout_take_50.setVisibility(View.GONE);
            }

            if (slTake20 > 0) {
                layout_take_20.setVisibility(View.VISIBLE);
            } else {
                layout_take_20.setVisibility(View.GONE);
            }

            if (slTake10 > 0) {
                layout_take_10.setVisibility(View.VISIBLE);
            } else {
                layout_take_10.setVisibility(View.GONE);
            }

            tv_number_send_500.setText(String.valueOf(slSend500));
            tv_number_send_200.setText(String.valueOf(slSend200));
            tv_number_send_100.setText(String.valueOf(slSend100));
            tv_number_send_50.setText(String.valueOf(slSend50));
            tv_number_send_20.setText(String.valueOf(slSend20));
            tv_number_send_10.setText(String.valueOf(slSend10));

            tv_number_take_500.setText(String.valueOf(slTake500));
            tv_number_take_200.setText(String.valueOf(slTake200));
            tv_number_take_100.setText(String.valueOf(slTake100));
            tv_number_take_50.setText(String.valueOf(slTake50));
            tv_number_take_20.setText(String.valueOf(slTake20));
            tv_number_take_10.setText(String.valueOf(slTake10));

            long totalMoneyChange = slSend500 * 500000 + slSend200 * 200000 + slSend100 * 100000 + slSend50 * 50000 + slSend20 * 20000 + slSend10 * 10000;
            tv_total_money_send.setText(CommonUtils.formatPriceVND(totalMoneyChange));

            long totalMoneyTake = slTake500 * 500000 + slTake200 * 200000 + slTake100 * 100000 + slTake50 * 50000 + slTake20 * 20000 + slTake10 * 10000;
            tv_total_money_take.setText(CommonUtils.formatPriceVND(totalMoneyTake));

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                    if (onResult != null) {
                        onResult.OnListenerOk();
                    }
                }
            });

            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
        } else {
            dismissDialog();
            showDialogConfirmChangeCash(slSend500, slSend200, slSend100, slSend50, slSend20, slSend10,
                    slTake500, slTake200, slTake100, slTake50, slTake20, slTake10, pContext, onResult);
        }
    }

    public void showDialogEditContact(Context pContext, Contact contactTransferModel, OnContactUpdate onContactUpdate) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_edit_contact);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            Button btnUpdate = mDialog.findViewById(R.id.btn_update);
            TextView walletId = mDialog.findViewById(R.id.tv_wallet_id);
            EditText edtName = mDialog.findViewById(R.id.edt_name);
            TextView tvPhone = mDialog.findViewById(R.id.tv_phone);
            TextView tvMobileInfo = mDialog.findViewById(R.id.tv_mobile_info);

            walletId.setText(String.valueOf(contactTransferModel.getWalletId()));
            edtName.setText(contactTransferModel.getFullName());
            tvPhone.setText(contactTransferModel.getPhone());

            btnUpdate.setOnClickListener(v -> {
                String name = edtName.getText().toString();
                if (!name.isEmpty()) {
                    dismissDialog();
                    if (onContactUpdate != null) {
                        onContactUpdate.OnListenerOk(name);
                    }
                }
            });
            mDialog.show();
        } else {
            dismissDialog();
            showDialogEditContact(pContext, contactTransferModel, onContactUpdate);
        }
    }

    public void showDialogViewQRCode(Context pContext, Bitmap ivBitmap) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_view_image);
            ImageView ivQr;
            Button btn_close;
            btn_close = mDialog.findViewById(R.id.btn_close);
            ivQr = mDialog.findViewById(R.id.iv_qr_code);
            ivQr.setImageBitmap(ivBitmap);
            btn_close.setOnClickListener(v -> {
                dismissDialog();
            });

            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    public void showDialogChangeLanguage(Context pContext, final OnChangeLanguage onChangeLanguage) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_change_language);
            Button btnVn, btnEn;
            btnVn = mDialog.findViewById(R.id.btn_vietnam);
            btnEn = mDialog.findViewById(R.id.btn_english);

            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();

            if (LanguageUtils.getCurrentLanguage().getCode().equals(pContext.getResources().getString(R.string.language_vietnamese_code))) {
                btnVn.setBackground(pContext.getResources().getDrawable(R.drawable.bg_border_gray));
                btnEn.setTextColor(pContext.getResources().getColor(R.color.gray));

                btnEn.setBackground(pContext.getResources().getDrawable(R.drawable.bg_border_rectangle_blue));
                btnEn.setTextColor(pContext.getResources().getColor(R.color.white));
            } else {
                btnEn.setBackground(pContext.getResources().getDrawable(R.drawable.bg_border_gray));
                btnEn.setTextColor(pContext.getResources().getColor(R.color.gray));

                btnVn.setBackground(pContext.getResources().getDrawable(R.drawable.bg_border_rectangle_blue));
                btnVn.setTextColor(pContext.getResources().getColor(R.color.white));
            }

            btnVn.setOnClickListener(v -> {
                if (LanguageUtils.getCurrentLanguage().getCode().equals(pContext.getResources().getString(R.string.language_vietnamese_code))) {
                    Toast.makeText(pContext, pContext.getResources().getString(R.string.err_current_language_conflict), Toast.LENGTH_LONG).show();
                    return;
                }

                dismissDialog();
                LanguageObject currentLanguage = new LanguageObject(Constant.DEFAULT_LANGUAGE_ID,
                        pContext.getResources().getString(R.string.language_vietnamese),
                        pContext.getResources().getString(R.string.language_vietnamese_code));
                LanguageUtils.changeLanguage(currentLanguage);
                if (onChangeLanguage != null) {
                    onChangeLanguage.OnListenerVn();
                }
            });
            btnEn.setOnClickListener(v -> {
                if (LanguageUtils.getCurrentLanguage().getCode().equals(pContext.getResources().getString(R.string.language_english_code))) {
                    Toast.makeText(pContext, pContext.getResources().getString(R.string.err_current_language_conflict), Toast.LENGTH_LONG).show();
                    return;
                }
                dismissDialog();
                LanguageObject currentLanguage = new LanguageObject(1,
                        pContext.getResources().getString(R.string.language_english),
                        pContext.getResources().getString(R.string.language_english_code));
                LanguageUtils.changeLanguage(currentLanguage);
                if (onChangeLanguage != null) {
                    onChangeLanguage.OnListenerVn();
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
