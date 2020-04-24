package vn.ecpay.ewallet.ui.QRCode;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.QRCode.fragment.ScannerQRCodeFragment;

import static androidx.core.provider.FontsContractCompat.FontRequestCallback.RESULT_OK;

public class QRCodeActivity extends ECashBaseActivity {
    private boolean scanQRCodePayTo;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_qr_bar_code;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtils.checkPermissionCamera(null, this)) {
            addFragment(new ScannerQRCodeFragment(), false);
            intentData();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addFragment(new ScannerQRCodeFragment(), true, R.id.containerQRCode);
            } else {
                DialogUtil.getInstance().showDialogErrorTitleMessage(this, getResources().getString(R.string.str_dialog_notification_title),
                        getResources().getString(R.string.str_open_camera), this::finish);
            }
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.containerQRCode);
    }

    private void intentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String data = intent.getStringExtra(Constant.EVENT_SCAN_CONTACT_PAYTO);
            if (data != null) {
                if (data.equals(Constant.EVENT_SCAN_CONTACT_PAYTO)) {
                    //EventBus.getDefault().post(new EventDataChange(Constant.EVENT_SCAN_CONTACT_PAYTO));
                    setScanQRCodePayTo(true);
                }
            }
        }
    }

    public void checkPayTo(Contact contact) {
        if (isScanQRCodePayTo()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constant.EVENT_SCAN_CONTACT_PAYTO, (Parcelable) contact);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    public boolean isScanQRCodePayTo() {
        return scanQRCodePayTo;
    }

    public void setScanQRCodePayTo(boolean scanQRCodePayTo) {
        this.scanQRCodePayTo = scanQRCodePayTo;
    }
}
