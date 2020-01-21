package vn.ecpay.ewallet.ui.billingQRCode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.QRCode.QRCodePayment;

public class BillingQRCodeFragment extends ECashBaseFragment {

    @BindView(R.id.iv_qr_code)
    ImageView ivQRCode;
    private QRCodePayment qrCodePayment;
    private Bitmap bitmap;
    public static BillingQRCodeFragment newInstance(QRCodePayment qrCodePayment) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.QR_CODE_TOPAY_MODEL, qrCodePayment);
        BillingQRCodeFragment fragment = new BillingQRCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_create_qr_code_to_pay;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            qrCodePayment = (QRCodePayment) bundle.getSerializable(Constant.QR_CODE_TOPAY_MODEL);
            generateQRCode();
        }
    }

    @Override
    public void onResume() {
        ((BillingQRCodeActivity) getActivity()).updateTitle(getString(R.string.str_create_a_bill_of_payment));
        super.onResume();
    }
    @OnClick({R.id.view_share,R.id.view_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_share:
                handleShare();
                break;
            case R.id.view_download:
                handleSave();
                break;
        }
    }
    private void generateQRCode(){
        if(qrCodePayment!=null){
            Log.e("qrToPay",qrCodePayment.toString());
            Gson gson = new Gson();
            bitmap = CommonUtils.generateQRCode(gson.toJson(qrCodePayment));
            ivQRCode.setImageBitmap(bitmap);
        }else{
            ivQRCode.setVisibility(View.GONE);
        }
    }
    private void handleSave(){
        if(bitmap!=null&&qrCodePayment!=null){
            saveImageQRCode(bitmap,CommonUtils.getCurrentTime(Constant.FORMAT_DATE_SEND_CASH));
        }
    }
    private void handleShare(){
        if(bitmap!=null){
            Uri uri = CommonUtils.getBitmapUri(getActivity(),bitmap);
            if(uri!=null){
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/jpeg");
                getContext().startActivity(Intent.createChooser(shareIntent, getString(R.string.str_share)));
            }else{
                Toast.makeText(getActivity(),getString(R.string.str_have_warning),Toast.LENGTH_SHORT).show();
            }
        }
    }
}

