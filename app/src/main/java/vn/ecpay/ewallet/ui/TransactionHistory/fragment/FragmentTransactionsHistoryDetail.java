package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.BuildConfig;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.model.transactionsHistory.TransactionsHistoryModel;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.AdapterCashLogTransactionsHistory;

import static vn.ecpay.ewallet.common.utils.Constant.STR_CASH_IN;
import static vn.ecpay.ewallet.common.utils.Constant.STR_CASH_OUT;
import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_FAIL;
import static vn.ecpay.ewallet.common.utils.Constant.TRANSACTION_SUCCESS;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_CASH_EXCHANGE;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_ECASH_TO_ECASH;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_LIXI;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_PAYTO;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_ECASH_TO_EDONG;
import static vn.ecpay.ewallet.common.utils.Constant.TYPE_SEND_EDONG_TO_ECASH;

public class FragmentTransactionsHistoryDetail extends ECashBaseFragment {
    @BindView(R.id.tv_total_money_transfer)
    TextView tvTotalMoneyTransfer;
    @BindView(R.id.tv_human_code)
    TextView tvHumanCode;
    @BindView(R.id.tv_history_name)
    TextView tvHistoryName;
    @BindView(R.id.tv_history_phone)
    TextView tvHistoryPhone;
    @BindView(R.id.tv_history_type)
    TextView tvHistoryType;
    @BindView(R.id.tv_history_total)
    TextView tvHistoryTotal;
    @BindView(R.id.tv_history_content)
    TextView tvHistoryContent;
    @BindView(R.id.tv_history_date)
    TextView tvHistoryDate;
    @BindView(R.id.rv_list_cash_transfer)
    RecyclerView rvListCashTransfer;
    @BindView(R.id.layout_share)
    RelativeLayout layoutShare;
    @BindView(R.id.layout_download)
    RelativeLayout layoutDownload;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_transactions_status)
    TextView tvTransactionsStatus;
    @BindView(R.id.layout_qr_code)
    LinearLayout layoutQrCode;
    @BindView(R.id.layout_content)
    RelativeLayout layoutContent;
    @BindView(R.id.tv_sender_receiver)
    TextView tvSenderReceiver;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.tv_cash_take)
    TextView tvCashTake;
    @BindView(R.id.rv_list_cash_take)
    RecyclerView rvListCashTake;
    @BindView(R.id.cv_cash_take)
    CardView cvCashTake;
    @BindView(R.id.tv_cash_change)
    TextView tvCashChange;
    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.bt_next)
    ImageView btNext;
    @BindView(R.id.toolbar_center_text)
    TextView toolbarCenterText;
    private TransactionsHistoryModel transactionsHistoryModel;
    private AdapterCashLogTransactionsHistory adapterCashLogTransactionsHistoryIn;
    private AdapterCashLogTransactionsHistory adapterCashLogTransactionsHistoryOut;
    private String currentTime;
    private boolean isSaveQR = false;
    private List<QRCodeSender> listQRCodeSender;

    public static FragmentTransactionsHistoryDetail newInstance(TransactionsHistoryModel transactionsHistoryModel) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.TRANSACTIONS_HISTORY_MODEL, transactionsHistoryModel);
        FragmentTransactionsHistoryDetail fragment = new FragmentTransactionsHistoryDetail();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.transactions_history_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.transactionsHistoryModel = (TransactionsHistoryModel) bundle.getSerializable(Constant.TRANSACTIONS_HISTORY_MODEL);
            if (null == transactionsHistoryModel) {
                showDialogError(getResources().getString(R.string.err_upload));
                return;
            }

            updateView();
            setAdapterListCash();
            checkIsHaveQRCode();
            currentTime = CommonUtils.getCurrentTime();
        }
        dismissProgress();
    }

    private void updateView() {
        tvHistoryPhone.setText(transactionsHistoryModel.getReceiverPhone());
        tvHistoryTotal.setText(CommonUtils.formatPriceVND(Long.parseLong(transactionsHistoryModel.getTransactionAmount())));
        tvHistoryContent.setText(transactionsHistoryModel.getTransactionContent());
        tvHistoryDate.setText(CommonUtils.getDateTransfer(getActivity(), transactionsHistoryModel.getTransactionDate()));

        switch (transactionsHistoryModel.getTransactionType()) {
            case TYPE_SEND_ECASH_TO_EDONG:
                toolbarCenterText.setText(getResources().getString(R.string.str_cash_out));
                tvType.setText(getResources().getString(R.string.str_cash_out));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_out));
                tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                        CommonUtils.formatPriceVND(Long.parseLong(transactionsHistoryModel.getTransactionAmount()))));
                layoutContent.setVisibility(View.GONE);
                tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                tvHumanCode.setText(transactionsHistoryModel.getSenderAccountId());
                tvHistoryName.setText(transactionsHistoryModel.getSenderName());
                break;
            case TYPE_SEND_EDONG_TO_ECASH:
                tvType.setText(getResources().getString(R.string.str_cash_in));
                toolbarCenterText.setText(getResources().getString(R.string.str_cash_in));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_in));
                tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                tvSenderReceiver.setText(getResources().getString(R.string.sender));
                tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                layoutContent.setVisibility(View.GONE);
                break;
            case TYPE_ECASH_TO_ECASH:
                if (transactionsHistoryModel.getCashLogType().equals(STR_CASH_IN)) {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvSenderReceiver.setText(getResources().getString(R.string.sender));
                    tvHumanCode.setText(transactionsHistoryModel.getSenderAccountId());
                    tvHistoryName.setText(transactionsHistoryModel.getSenderName());
                    tvHistoryPhone.setText(transactionsHistoryModel.getSenderPhone());
                } else {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                    tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                    tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                }
                tvType.setText(getResources().getString(R.string.str_transfer));
                toolbarCenterText.setText(getResources().getString(R.string.str_transfer));
                tvHistoryType.setText(getResources().getString(R.string.str_transfer));
                break;
            case TYPE_LIXI:
                if (transactionsHistoryModel.getCashLogType().equals(STR_CASH_IN)) {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvSenderReceiver.setText(getResources().getString(R.string.sender));
                } else {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                }
                tvType.setText(getResources().getString(R.string.str_lixi));
                tvHistoryType.setText(getResources().getString(R.string.str_lixi));
                break;
            case TYPE_CASH_EXCHANGE:
                tvType.setText(getResources().getString(R.string.str_cash_change));
                toolbarCenterText.setText(getResources().getString(R.string.str_cash_change));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_change));
                tvHistoryTotal.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()) / 2));
                tvTotalMoneyTransfer.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()) / 2));
                tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                layoutContent.setVisibility(View.GONE);

                tvCashChange.setText(getResources().getString(R.string.str_number_cash_transfer));
                tvCashTake.setVisibility(View.VISIBLE);
                cvCashTake.setVisibility(View.VISIBLE);
                break;
            case TYPE_PAYTO:
                if (transactionsHistoryModel.getCashLogType().equals(STR_CASH_IN)) {
                    tvSenderReceiver.setText(getResources().getString(R.string.sender));
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvHumanCode.setText(transactionsHistoryModel.getSenderAccountId());
                    tvHistoryName.setText(transactionsHistoryModel.getSenderName());
                } else {
                    tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                    tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                }
                tvType.setText(getResources().getString(R.string.str_payment));
                toolbarCenterText.setText(getResources().getString(R.string.str_payment));
                tvHistoryType.setText(getResources().getString(R.string.str_payment));

                break;
        }

        if (Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_SUCCESS) {
            tvTransactionsStatus.setText(getResources().getString(R.string.str_cash_take_success));
        } else if (Integer.parseInt(transactionsHistoryModel.getTransactionStatus()) == TRANSACTION_FAIL) {
            tvTransactionsStatus.setText(getResources().getString(R.string.str_fail));
        }
    }

    private void checkIsHaveQRCode() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        listQRCodeSender = WalletDatabase.getAllTransactionLogQR(transactionsHistoryModel.getTransactionSignature());
        if (null != listQRCodeSender) {
            if (listQRCodeSender.size() > 0) {
                layoutBottom.setVisibility(View.VISIBLE);
                layoutQrCode.setVisibility(View.VISIBLE);
                setAdapterQRCode(listQRCodeSender);
            } else {
                layoutBottom.setVisibility(View.GONE);
                layoutQrCode.setVisibility(View.GONE);
            }
        } else {
            layoutBottom.setVisibility(View.GONE);
            layoutQrCode.setVisibility(View.GONE);
        }
    }

    private void setAdapterListCash() {
        if (transactionsHistoryModel.getTransactionType().equals(TYPE_CASH_EXCHANGE)) {
            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
            List<CashLogTransaction> listCashLogTransactionIn = WalletDatabase.getAllCashByTransactionLogByType(transactionsHistoryModel.getTransactionSignature(), STR_CASH_IN);
            List<CashLogTransaction> listCashLogTransactionOut = WalletDatabase.getAllCashByTransactionLogByType(transactionsHistoryModel.getTransactionSignature(), STR_CASH_OUT);

            LinearLayoutManager mLayoutManagerOut = new LinearLayoutManager(getActivity());
            LinearLayoutManager mLayoutManagerIn = new LinearLayoutManager(getActivity());
            rvListCashTransfer.setLayoutManager(mLayoutManagerOut);
            rvListCashTake.setLayoutManager(mLayoutManagerIn);

            adapterCashLogTransactionsHistoryIn = new AdapterCashLogTransactionsHistory(listCashLogTransactionIn, getActivity());
            adapterCashLogTransactionsHistoryOut = new AdapterCashLogTransactionsHistory(listCashLogTransactionOut, getActivity());

            rvListCashTransfer.setAdapter(adapterCashLogTransactionsHistoryOut);
            rvListCashTake.setAdapter(adapterCashLogTransactionsHistoryIn);

        } else {
            WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
            List<CashLogTransaction> listCashLogTransaction = WalletDatabase.getAllCashByTransactionLog(transactionsHistoryModel.getTransactionSignature());

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rvListCashTransfer.setLayoutManager(mLayoutManager);
            adapterCashLogTransactionsHistoryIn = new AdapterCashLogTransactionsHistory(listCashLogTransaction, getActivity());
            rvListCashTransfer.setAdapter(adapterCashLogTransactionsHistoryIn);
        }
    }

    private ArrayList<Uri> uris;

    @SuppressLint("StaticFieldLeak")
    private void saveImageQRCode(List<QRCodeSender> qrCodeSender) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... voids) {
                uris = new ArrayList<>();
                for (int i = 0; i < qrCodeSender.size(); i++) {
                    Gson gson = new Gson();
                    Bitmap bitmap = CommonUtils.generateQRCode(gson.toJson(qrCodeSender.get(i)));
                    String root = Environment.getExternalStorageDirectory().toString();
                    File mFolder = new File(root + "/qr_image");
                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }
                    String imageName = transactionsHistoryModel.getReceiverAccountId() + "_" + currentTime + "_" + i + ".jpg";
                    File file = new File(mFolder, imageName);
                    Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
                    uris.add(uri);
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
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissProgress();
                if (isSaveQR) {
                    showDialogSuccess(getResources().getString(R.string.str_save_to_device_success));
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_STREAM, uris);
                    //A content: URI holding a stream of data associated with the Intent, used with ACTION_SEND to supply the data being sent.
                    intent.setType("image/*"); //any kind of images can support.
                    Intent chooser = Intent.createChooser(intent, "QR Code");//choosers title
                    startActivity(chooser);
                }
            }
        }.execute();
    }

    private ArrayList<Bitmap> listQR = new ArrayList<>();
    private int index = 0;

    private void setAdapterQRCode(List<QRCodeSender> listQRCodeSender) {
        Gson gson = new Gson();
        for (QRCodeSender qrCodeSender : listQRCodeSender) {
            listQR.add(CommonUtils.generateQRCode(gson.toJson(qrCodeSender)));
        }
        btBack.setVisibility(View.GONE);
        ivQrCode.setImageBitmap(listQR.get(0));
        ivQrCode.setOnClickListener(v -> DialogUtil.getInstance()
                .showDialogViewQRCode(getActivity(), listQR.get(index)));
    }

    @OnClick({R.id.layout_share, R.id.layout_download, R.id.iv_back, R.id.bt_back, R.id.bt_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_share:
                if (!CommonUtils.isExternalStorageWritable()) {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getString(R.string.err_store_image));
                    return;
                }
                isSaveQR = false;
                if (listQRCodeSender.size() > 0) {
                    if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                        showProgress();
                        saveImageQRCode(listQRCodeSender);
                    }
                }
                break;
            case R.id.layout_download:
                if (!CommonUtils.isExternalStorageWritable()) {
                    DialogUtil.getInstance().showDialogWarning(getActivity(), getString(R.string.err_store_image));
                    return;
                }
                isSaveQR = true;
                if (listQRCodeSender.size() > 0) {
                    if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                        showProgress();
                        saveImageQRCode(listQRCodeSender);
                    }
                }
                break;
            case R.id.bt_back:
                if (index == 0) {
                    btBack.setVisibility(View.GONE);
                    return;
                } else {
                    btBack.setVisibility(View.VISIBLE);
                    btNext.setVisibility(View.VISIBLE);
                    ivQrCode.setImageBitmap(listQR.get(index - 1));
                    index = index - 1;
                }
                break;
            case R.id.bt_next:
                if (index == listQR.size() - 1) {
                    btNext.setVisibility(View.GONE);
                    return;
                } else {
                    btNext.setVisibility(View.VISIBLE);
                    btBack.setVisibility(View.VISIBLE);
                    ivQrCode.setImageBitmap(listQR.get(index + 1));
                    index = index + 1;
                }
                break;
            case R.id.iv_back:
                if (getActivity() != null)
                    getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgress();
                    saveImageQRCode(listQRCodeSender);
                }
            }
            default:
                break;
        }
    }
}
