package vn.ecpay.ewallet.ui.QRCode;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.ui.QRCode.fragment.ScannerQRCodeFragment;

public class QRCodeActivity extends ECashBaseActivity {
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.MY_CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addFragment(new ScannerQRCodeFragment(), true, R.id.containerQRCode);
                } else {
                    finish();
                }
            }
            default:
                break;
        }
    }

    public void addFragment(Fragment pFragment, boolean isAnimation) {
        addFragment(pFragment, isAnimation, R.id.containerQRCode);
    }
}
