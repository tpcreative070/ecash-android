package vn.ecpay.ewallet.ui.wallet.fragment;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.common.utils.QRCodeUtil;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.contact.QRContact;
import vn.ecpay.ewallet.ui.wallet.activity.MyQRCodeActivity;

import static vn.ecpay.ewallet.common.utils.Constant.QR_CONTACT;

public class FragmentMyQRCode extends ECashBaseFragment {
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    private AccountInfo accountInfo;
    private QRContact qrContact;
    private Bitmap bitmap;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my_qr_code;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WalletDatabase.getINSTANCE(getContext(), ECashApplication.masterKey);
        accountInfo = WalletDatabase.getAccountInfoTask(ECashApplication.getAccountInfo().getUsername());
        setData();
    }

    private void setData() {
        if (null != accountInfo) {
            tvName.setText(CommonUtils.getFullName(accountInfo));
            tvPhoneNumber.setText(accountInfo.getPersonMobilePhone());
            qrContact = new QRContact();
            qrContact.setWalletId(accountInfo.getWalletId());
            qrContact.setFullname(CommonUtils.getFullName(accountInfo));
            qrContact.setPersonMobiPhone(accountInfo.getPersonMobilePhone());
            qrContact.setPublicKey(accountInfo.getEcKeyPublicValue());
            qrContact.setTerminalInfo(accountInfo.getTerminalInfo());
            Gson gson = new Gson();
            QRScanBase qrScanBase = new QRScanBase();
            qrScanBase.setType(QR_CONTACT);
            qrScanBase.setContent(gson.toJson(qrContact));

            bitmap = CommonUtils.generateQRCode(gson.toJson(qrScanBase));
            ivQrCode.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.tv_download)
    public void onViewClicked() {
        if(bitmap!=null){
            QRCodeUtil.saveImageQRCode(this,bitmap,String.valueOf(accountInfo.getWalletId()), Constant.DIRECTORY_QR_MY_CONTACT);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(bitmap!=null){
                        QRCodeUtil.saveImageQRCode(this,bitmap,String.valueOf(accountInfo.getWalletId()), Constant.DIRECTORY_QR_MY_CONTACT);
                    }

                }
            }
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MyQRCodeActivity) getActivity()).updateTitle(getResources().getString(R.string.str_my_qr_code));
    }
}
