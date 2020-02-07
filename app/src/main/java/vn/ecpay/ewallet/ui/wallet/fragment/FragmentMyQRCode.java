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
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
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
            qrContact.setType(QR_CONTACT);
            Gson gson = new Gson();
            bitmap = CommonUtils.generateQRCode(gson.toJson(qrContact));
            ivQrCode.setImageBitmap(bitmap);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void saveImageQRCode() {
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
                String imageName = accountInfo.getWalletId() + ".jpg";
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

    @OnClick(R.id.tv_download)
    public void onViewClicked() {
        if(bitmap!=null){
            saveImageQRCode(bitmap,String.valueOf(accountInfo.getWalletId()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgress();
                    saveImageQRCode();
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
