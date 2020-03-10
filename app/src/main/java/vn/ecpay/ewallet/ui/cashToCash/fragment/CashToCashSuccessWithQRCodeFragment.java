package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.common.utils.QRCodeUtil;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.ui.billingQRCode.BillingQRCodeFragment;
import vn.ecpay.ewallet.ui.cashIn.CashInActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashActivity;
import vn.ecpay.ewallet.ui.cashToCash.CashToCashSuccessWithQRCodeActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.SlideQRCodeAdapter;
import vn.ecpay.ewallet.ui.lixi.MyLixiActivity;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class CashToCashSuccessWithQRCodeFragment extends ECashBaseFragment {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.view_pager)
    ViewPager view_pager;
    @BindView(R.id.iv_left)
    ImageView iv_left;
    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.view_share)
    View view_share;
    @BindView(R.id.view_download)
    View view_save;
    private List<CashTotal> valuesListAdapter;
    private List<Contact> multiTransferList;
    private String content;
    private String type;
    private SlideQRCodeAdapter adapter;
    private Bitmap bitmap;
    private int pagePosition=0;

    public static CashToCashSuccessWithQRCodeFragment newInstance(List<CashTotal> valuesListAdapter, List<Contact> multiTransferList, String content, String type) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.CASH_TOTAL_TRANSFER, (Serializable) valuesListAdapter);
        args.putSerializable(Constant.CONTACT_MULTI_TRANSFER, (Serializable) multiTransferList);
        args.putSerializable(Constant.CONTENT_TRANSFER, content);
        args.putSerializable(Constant.TYPE_TRANSFER, type);
        CashToCashSuccessWithQRCodeFragment fragment = new CashToCashSuccessWithQRCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cash_to_cash_success_qr_code;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            valuesListAdapter = (List<CashTotal>) bundle.getSerializable(Constant.CASH_TOTAL_TRANSFER);
            multiTransferList = (List<Contact>) bundle.getSerializable(Constant.CONTACT_MULTI_TRANSFER);
            content = bundle.getString(Constant.CONTENT_TRANSFER);
            type = bundle.getString(Constant.TYPE_TRANSFER);
            setData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CashToCashSuccessWithQRCodeActivity) getActivity()).updateTitle(getString(R.string.str_transfer));
    }

    private void setData() {
        if (valuesListAdapter != null && multiTransferList != null) {
            tv_title.setText(String.format(getString(R.string.str_you_have_successfully_qr_press_arrow_to_view_other_qr), multiTransferList.size() + ""));
            adapter = new SlideQRCodeAdapter(getActivity(), valuesListAdapter, multiTransferList, content, type);
            view_pager.setAdapter(adapter);
            if(multiTransferList.size()<=1){
                iv_left.setVisibility(View.GONE);
                iv_right.setVisibility(View.GONE);
            }else{
                iv_left.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
            }
           // view_pager.setCurrentItem(0);
            view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }
                @Override
                public void onPageSelected(int position) {
                    pagePosition=position;
                    if(multiTransferList.size()<=1){
                        return;
                    }else{
                        if(position==0){
                            iv_left.setVisibility(View.GONE);
                            iv_right.setVisibility(View.VISIBLE);
                        }else if(position==multiTransferList.size()-1){
                            iv_left.setVisibility(View.VISIBLE);
                            iv_right.setVisibility(View.GONE);
                        }else{
                            iv_left.setVisibility(View.VISIBLE);
                            iv_right.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
    @OnClick({R.id.iv_left, R.id.iv_right, R.id.view_download,R.id.view_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                pagePosition-=1;
                if(pagePosition>=0&&pagePosition<multiTransferList.size()){
                    view_pager.setCurrentItem(pagePosition);
                }
                break;
            case R.id.iv_right:
                pagePosition+=1;
                if(pagePosition>=0&&pagePosition<multiTransferList.size()){
                    view_pager.setCurrentItem(pagePosition);
                }
                break;
            case R.id.view_share:
                getBitmap(true);
                break;
            case R.id.view_download:
                getBitmap(false);
                break;
        }
    }


    private void getBitmap(boolean share){
        bitmap = adapter.getBitmap();
        if(share){
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
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
        }else{
            if(bitmap!=null){
                QRCodeUtil.saveImageQRCode(this,bitmap, CommonUtils.getCurrentTime(Constant.FORMAT_DATE_SEND_CASH), Constant.DIRECTORY_QR_IMAGE);
            }
        }

    }
}
