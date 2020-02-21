package vn.ecpay.ewallet.ui.TransactionHistory.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import vn.ecpay.ewallet.ui.TransactionHistory.TransactionsHistoryDetailActivity;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.AdapterCashLogTransactionsHistory;
import vn.ecpay.ewallet.ui.TransactionHistory.adapter.TransactionQRCodeAdapter;

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
    @BindView(R.id.rv_list_qr_code)
    RecyclerView rvListQrCode;
    @BindView(R.id.layout_content)
    RelativeLayout layoutContent;
    @BindView(R.id.tv_sender_receiver)
    TextView tvSenderReceiver;
    private TransactionsHistoryModel transactionsHistoryModel;
    private AdapterCashLogTransactionsHistory adapterCashLogTransactionsHistory;
    private TransactionQRCodeAdapter transactionQRCodeAdapter;
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
            updateView();
            setAdapterListCash();
            checkIsHaveQRCode();
            currentTime = CommonUtils.getCurrentTime();
        }
    }

    private void updateView() {
        tvHistoryPhone.setText(transactionsHistoryModel.getReceiverPhone());
        tvHistoryTotal.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount())));
        tvHistoryContent.setText(transactionsHistoryModel.getTransactionContent());
        tvHistoryDate.setText(CommonUtils.getDateTransfer(getActivity(), transactionsHistoryModel.getTransactionDate()));

        switch (transactionsHistoryModel.getTransactionType()) {
            case TYPE_SEND_ECASH_TO_EDONG:
                tvType.setText(getResources().getString(R.string.str_cash_out));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_out));
                tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                layoutContent.setVisibility(View.GONE);
                tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                tvHumanCode.setText(transactionsHistoryModel.getSenderAccountId());
                tvHistoryName.setText(transactionsHistoryModel.getSenderName());
                break;
            case TYPE_SEND_EDONG_TO_ECASH:
                tvType.setText(getResources().getString(R.string.str_cash_in));
                tvHistoryType.setText(getResources().getString(R.string.str_cash_in));
                tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                        CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                tvSenderReceiver.setText(getResources().getString(R.string.sender));
                tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                layoutContent.setVisibility(View.GONE);
                break;
            case TYPE_ECASH_TO_ECASH:
                if (transactionsHistoryModel.getCashLogType().equals(Constant.STR_CASH_IN)) {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_in,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvSenderReceiver.setText(getResources().getString(R.string.sender));
                    tvHumanCode.setText(transactionsHistoryModel.getSenderAccountId());
                    tvHistoryName.setText(transactionsHistoryModel.getSenderName());
                } else {
                    tvTotalMoneyTransfer.setText(getResources().getString(R.string.str_type_cash_out,
                            CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()))));
                    tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                    tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                    tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                }
                tvType.setText(getResources().getString(R.string.str_transfer));
                tvHistoryType.setText(getResources().getString(R.string.str_transfer));
                break;
            case TYPE_LIXI:
                if (transactionsHistoryModel.getCashLogType().equals(Constant.STR_CASH_IN)) {
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
                tvHistoryType.setText(getResources().getString(R.string.str_cash_change));
                tvHistoryTotal.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()) / 2));
                tvTotalMoneyTransfer.setText(CommonUtils.formatPriceVND(Long.valueOf(transactionsHistoryModel.getTransactionAmount()) / 2));
                tvSenderReceiver.setText(getResources().getString(R.string.receiver));
                tvHumanCode.setText(transactionsHistoryModel.getReceiverAccountId());
                tvHistoryName.setText(transactionsHistoryModel.getReceiverName());
                layoutContent.setVisibility(View.GONE);
                break;
            case TYPE_PAYTO:
                if (transactionsHistoryModel.getCashLogType().equals(Constant.STR_CASH_IN)) {
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

    private void setAdapterQRCode(List<QRCodeSender> listQRCodeSender) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvListQrCode.setLayoutManager(layoutManager);
        transactionQRCodeAdapter = new TransactionQRCodeAdapter(listQRCodeSender, getActivity());
        rvListQrCode.setAdapter(transactionQRCodeAdapter);
    }

    private void setAdapterListCash() {
        WalletDatabase.getINSTANCE(getActivity(), ECashApplication.masterKey);
        List<CashLogTransaction> listCashLogTransaction = WalletDatabase.getAllCashByTransactionLog(transactionsHistoryModel.getTransactionSignature());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvListCashTransfer.setLayoutManager(mLayoutManager);
        adapterCashLogTransactionsHistory = new AdapterCashLogTransactionsHistory(listCashLogTransaction, getActivity());
        rvListCashTransfer.setAdapter(adapterCashLogTransactionsHistory);
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.str_save_to_device_success), Toast.LENGTH_LONG).show();
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

    @OnClick({R.id.layout_share, R.id.layout_download})
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

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((TransactionsHistoryDetailActivity) getActivity()).updateTitle(getResources().getString(R.string.str_transactions_history_detail));
        } catch (ClassCastException e) {
//            ((QRCodeActivity) getActivity()).updateTitle(getResources().getString(R.string.str_transactions_history_detail));
        }
    }

    private void exPortDBFile(Context context) {
        try {
            exportFile(context.getDatabasePath(Constant.DATABASE_NAME).getAbsolutePath(), Constant.DATABASE_NAME);
            exportFile(context.getDatabasePath(Constant.DATABASE_NAME + "-shm").getAbsolutePath(), Constant.DATABASE_NAME + "-shm");
            exportFile(context.getDatabasePath(Constant.DATABASE_NAME + "-wal").getAbsolutePath(), Constant.DATABASE_NAME + "-wal");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File exportFile(String input, String dst) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        File mFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/eCash_DB");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        File expFile = new File(mFolder, dst);

        try {
            inChannel = new FileInputStream(input).getChannel();
            outChannel = new FileOutputStream(expFile).getChannel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }
}
