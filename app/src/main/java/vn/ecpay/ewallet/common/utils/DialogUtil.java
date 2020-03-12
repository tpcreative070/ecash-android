package vn.ecpay.ewallet.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.database.table.Payment_DataBase;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.language.LanguageObject;
import vn.ecpay.ewallet.model.payment.Payments;
import vn.ecpay.ewallet.ui.adapter.CashTotalChangeAdapter;
import vn.ecpay.ewallet.ui.adapter.CashTotalConfirmAdapter;
import vn.ecpay.ewallet.ui.cashIn.adapter.CashValueAdapter;
import vn.ecpay.ewallet.ui.cashOut.adapter.CashOutAdapter;
import vn.ecpay.ewallet.ui.callbackListener.UpDownMoneyListener;

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

    public interface OnChangeAvatar {
        void OnListenerFromStore();

        void OnListenerTakePhoto();
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
        void OnListenerOk(List<CashTotal> valuesListCashChange);
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
            btnOk.setOnClickListener(v -> {
                dismissDialog();
                if (pOnConfirm != null) {
                    pOnConfirm.OnListenerOk();
                }
            });
            btnCancel.setOnClickListener(v -> {
                dismissDialog();
                if (pOnConfirm != null) {
                    pOnConfirm.OnListenerCancel();
                }
            });
            ((ECashBaseActivity) pContext).dismissLoading();
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
                    try {
                        imm.hideSoftInputFromWindow(mDialog.getWindow().getCurrentFocus().getWindowToken(), 0);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            mDialog.show();
        } else {
            dismissDialog();
            showDialogInputOTP(pContext, sdt, error, txtInput, onConfirmOTP);
        }
    }

    public void showDialogCashChange(Context pContext, final OnResultChoseCash onResultChoseCash) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_chose_cash_change);

            Button btnOk;
            TextView tvTotalMoney, tvTitle;
            RecyclerView rv_cash_change;
            List<CashTotal> valuesList;

            tvTitle = mDialog.findViewById(R.id.tv_title);
            tvTotalMoney = mDialog.findViewById(R.id.tv_total_money);
            btnOk = mDialog.findViewById(R.id.btn_confirm);
            rv_cash_change = mDialog.findViewById(R.id.rv_cash_change);

            tvTitle.setText(pContext.getString(R.string.str_cash_change));
            valuesList = DatabaseUtil.getAllCashTotal(pContext);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(pContext);
            rv_cash_change.setLayoutManager(mLayoutManager);
            rv_cash_change.setAdapter(new CashOutAdapter(valuesList, pContext, new UpDownMoneyListener() {
                @Override
                public void onUpDownMoneyListener() {
                    tvTotalMoney.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valuesList)));
                }
            }));
            btnOk.setOnClickListener(v -> {
                if (onResultChoseCash != null) {
                    if (CommonUtils.getTotalMoney(valuesList) > 0) {
                        dismissDialog();
                        onResultChoseCash.OnListenerOk(valuesList);
                    } else {
                        Toast.makeText(pContext, pContext.getResources().getString(R.string.err_chose_money_transfer), Toast.LENGTH_LONG).show();
                    }
                }
            });
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    public void showDialogCashTake(Context pContext, final OnResultChoseCash onResultChoseCash) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_chose_cash_change);

            Button btnOk;
            TextView tvTotalMoney, tvTitle;
            RecyclerView rv_cash_change;
            List<CashTotal> valuesList;

            tvTitle = mDialog.findViewById(R.id.tv_title);
            tvTotalMoney = mDialog.findViewById(R.id.tv_total_money);
            btnOk = mDialog.findViewById(R.id.btn_confirm);
            rv_cash_change = mDialog.findViewById(R.id.rv_cash_change);

            tvTitle.setText(pContext.getString(R.string.str_cash_take));
            valuesList = DatabaseUtil.getAllCashValues(pContext);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(pContext);
            rv_cash_change.setLayoutManager(mLayoutManager);
            rv_cash_change.setAdapter(new CashValueAdapter(valuesList, pContext, new UpDownMoneyListener() {
                @Override
                public void onUpDownMoneyListener() {
                    tvTotalMoney.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valuesList)));
                }
            }));
            btnOk.setOnClickListener(v -> {
                if (onResultChoseCash != null) {
                    if (CommonUtils.getTotalMoney(valuesList) > 0) {
                        dismissDialog();
                        onResultChoseCash.OnListenerOk(valuesList);
                    } else {
                        Toast.makeText(pContext, pContext.getResources().getString(R.string.err_chose_money_transfer), Toast.LENGTH_LONG).show();
                    }
                }
            });
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    public void showDialogConfirmChangeCash(List<CashTotal> valuesListCashChange, List<CashTotal> valuesListCashTake, Context pContext, OnResult onResult) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_confirm_change_cash);

            Button btnConfirm;
            RecyclerView rv_cash_take, rv_cash_change;
            btnConfirm = mDialog.findViewById(R.id.btn_confirm);
            TextView tv_total_money_send = mDialog.findViewById(R.id.tv_total_money_send);
            TextView tv_total_money_take = mDialog.findViewById(R.id.tv_total_money_take);
            rv_cash_take = mDialog.findViewById(R.id.rv_cash_take);
            rv_cash_change = mDialog.findViewById(R.id.rv_cash_change);

            tv_total_money_send.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valuesListCashChange)));
            tv_total_money_take.setText(CommonUtils.formatPriceVND(CommonUtils.getTotalMoney(valuesListCashTake)));

            rv_cash_take.setLayoutManager(new LinearLayoutManager(pContext));
            rv_cash_change.setLayoutManager(new LinearLayoutManager(pContext));
            rv_cash_change.setAdapter(new CashTotalConfirmAdapter(CommonUtils.getListCashConfirm(valuesListCashChange), pContext));
            rv_cash_take.setAdapter(new CashTotalConfirmAdapter(CommonUtils.getListCashConfirm(valuesListCashTake), pContext));


            btnConfirm.setOnClickListener(v -> {
                dismissDialog();
                if (onResult != null) {
                    onResult.OnListenerOk();
                }
            });

            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();
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
                } else {
                    Toast.makeText(pContext, pContext.getResources().getString(R.string.err_contact_empty), Toast.LENGTH_SHORT).show();
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

            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
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

    public void showDialogWarning(Context pContext, String mess) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_warning);
            Button btnClose;
            TextView tv_mess;
            btnClose = mDialog.findViewById(R.id.btn_close);
            tv_mess = mDialog.findViewById(R.id.tv_mess);
            tv_mess.setText(mess);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();
            btnClose.setOnClickListener(v -> dismissDialog());
        }
    }

    public void showDialogChangeAvatar(Context pContext, final OnChangeAvatar onChangeAvatar) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_change_avatar);
            Button btnChoseFromStore, btnTakePhoto, btnClose;

            btnChoseFromStore = mDialog.findViewById(R.id.btn_chose_store);
            btnTakePhoto = mDialog.findViewById(R.id.btn_take_photo);
            btnClose = mDialog.findViewById(R.id.btn_cancel);

            mDialog.setCanceledOnTouchOutside(true);
            mDialog.setCancelable(true);
            mDialog.show();

            btnChoseFromStore.setOnClickListener(v -> {
                dismissDialog();
                if (onChangeAvatar != null) {
                    onChangeAvatar.OnListenerFromStore();
                }
            });
            btnTakePhoto.setOnClickListener(v -> {
                dismissDialog();
                if (onChangeAvatar != null) {
                    onChangeAvatar.OnListenerTakePhoto();
                }
            });
            btnClose.setOnClickListener(v -> dismissDialog());
        }
    }

    public void showDialogUpdateAccountInfo(Context pContext, String title, String message, final OnConfirm pOnConfirm) {
        if (!isShowing() && pContext != null) {
            initDialog(pContext);
            mDialog.setContentView(R.layout.dialog_update_account_info);
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
            btnOk.setOnClickListener(v -> {
                dismissDialog();
                if (pOnConfirm != null) {
                    pOnConfirm.OnListenerOk();
                }

            });
            btnCancel.setOnClickListener(v -> {
                dismissDialog();
                if (pOnConfirm != null) {
                    pOnConfirm.OnListenerCancel();
                }
            });
        }
    }

    public void showDialogPaymentSuccess(Context context, Payments payToRequest, final OnResult pOnConfirm) {
        if (!isShowing() && context != null) {
            initDialog(context);
            mDialog.setContentView(R.layout.dialog_payment_success);
            Button btnOk;
            TextView tvTitle = mDialog.findViewById(R.id.tvTitle);
            TextView tvAmount = mDialog.findViewById(R.id.tv_amount);
            TextView tvECashID = mDialog.findViewById(R.id.tv_ecash_id);
            tvAmount.setText(CommonUtils.formatPriceVND(Long.parseLong(payToRequest.getTotalAmount())));
            tvECashID.setText(String.format("%s - %s", payToRequest.getFullName(), payToRequest.getSender()));
            btnOk = mDialog.findViewById(R.id.btn_main_screen);
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

    public void showDialogPaymentRequest(Context context, Payment_DataBase payToRequest, final OnResult pOnConfirm) {
        if (!isShowing() && context != null) {
            initDialog(context);
            mDialog.setContentView(R.layout.dialog_payment_request);
            Button btnOk;
            TextView tvTitle = mDialog.findViewById(R.id.tvTitle);
            tvTitle.setText(String.format(context.getString(R.string.str_payment_request), " "));
            TextView tvContent = mDialog.findViewById(R.id.tv_content);
            tvContent.setText(Html.fromHtml(String.format(context.getString(R.string.str_content_payment_request), payToRequest.getFullName(), payToRequest.getSender(), CommonUtils.formatPriceVND(Long.parseLong(payToRequest.getTotalAmount())), payToRequest.getContent())));

            btnOk = mDialog.findViewById(R.id.btn_payment);
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

    public void showDialogConfirmPayment(Context context, List<CashTotal> valueListCash, Payment_DataBase payToRequest, final OnResult pOnConfirm) {
        if (!isShowing() && context != null) {
            initDialog(context);
            mDialog.setContentView(R.layout.dialog_confirm_payment);
            Button btnOk;

            TextView tv_info = mDialog.findViewById(R.id.tv_info);
            tv_info.setText(context.getString(R.string.str_payment_info));
            TextView tv_title = mDialog.findViewById(R.id.tv_title);
            RecyclerView rvCashValues = mDialog.findViewById(R.id.rv_cash_values);

            CashTotalChangeAdapter cashValueAdapter = new CashTotalChangeAdapter(valueListCash,false, context);
            //rvCashValues.addItemDecoration(new GridSpacingItemDecoration(2, 5, false));
            rvCashValues.setAdapter(cashValueAdapter);
            TextView tvTotalAmount = mDialog.findViewById(R.id.tv_total_payment);
            TextView tvContent = mDialog.findViewById(R.id.tv_content_payment);
            tv_title.setText(Html.fromHtml(String.format(context.getString(R.string.str_review_payment_content), CommonUtils.formatPriceVND(Long.parseLong(payToRequest.getTotalAmount())), payToRequest.getFullName(), payToRequest.getSender())));
            tvTotalAmount.setText(CommonUtils.formatPriceVND(Long.parseLong(payToRequest.getTotalAmount())));
            tvContent.setText(payToRequest.getContent());
            btnOk = mDialog.findViewById(R.id.btn_confirm);
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

    public void showDialogCannotPayment(Context context) {
        if (!isShowing() && context != null) {
            initDialog(context);
            mDialog.setContentView(R.layout.dialog_cannot_payment);
            Button btnOk;
            TextView tv_content = mDialog.findViewById(R.id.tv_content);
            tv_content.setText(Html.fromHtml(context.getString(R.string.str_cannot_payment_content)));
            btnOk = mDialog.findViewById(R.id.btn_close);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            btnOk.setOnClickListener(v -> {
                dismissDialog();

            });
        }
    }

    public void showDialogContinueAndExit(Context context, String title, String message, final OnConfirm listener) {
        if (!isShowing() && context != null) {
            initDialog(context);
            mDialog.setContentView(R.layout.dialog_continue_exit);
            TextView tvTitle, tvMessage,tvExit,tvContinue;
            tvTitle = mDialog.findViewById(R.id.tv_title);
            tvMessage = mDialog.findViewById(R.id.tv_message);
            tvExit = mDialog.findViewById(R.id.tv_exit);
            tvContinue = mDialog.findViewById(R.id.tv_continue);
            tvTitle.setText(title);
            tvMessage.setText(message);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
            tvContinue.setOnClickListener(v -> {
                dismissDialog();
                if (listener != null) {
                    listener.OnListenerOk();
                }
            });
            tvExit.setOnClickListener(v -> {
                dismissDialog();
                if (listener != null) {
                    listener.OnListenerCancel();
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
