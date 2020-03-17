package vn.ecpay.ewallet.ui.cashToCash.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.PermissionUtils;

import vn.ecpay.ewallet.common.utils.QRCodeUtil;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransfer;

import vn.ecpay.ewallet.ui.cashToCash.CashToCashSuccessWithQRCodeActivity;
import vn.ecpay.ewallet.ui.cashToCash.adapter.SlideQRCodeAdapter;
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

    @BindView(R.id.tv_scan_receive)
    TextView tv_scan_receive;
    @BindView(R.id.view_qr_code)
    View view_qr_code;
    @BindView(R.id.view_share)
    View view_share;
    @BindView(R.id.view_download)
    View view_save;

    private List<CashTotal> valuesListAdapter;
    private List<Contact> multiTransferList;
    private List<ContactTransfer> contactsList;
    private ArrayList<Bitmap> listBitmap;
    private ArrayList<Uri> listUri;
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
      //  Log.e("onViewCreated","onViewCreated");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            valuesListAdapter = (List<CashTotal>) bundle.getSerializable(Constant.CASH_TOTAL_TRANSFER);
            multiTransferList = (List<Contact>) bundle.getSerializable(Constant.CONTACT_MULTI_TRANSFER);
            content = bundle.getString(Constant.CONTENT_TRANSFER);
            type = bundle.getString(Constant.TYPE_TRANSFER);
           // listUri = (ArrayList<Bitmap>) bundle.getSerializable(Constant.URI_TRANSFER);

            initUI();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }
    private void initUI(){
        tv_title.setText("");
        view_qr_code.setVisibility(View.GONE);
        tv_scan_receive.setVisibility(View.GONE);
        showProgress();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mappingContact();
                //setData();
            }
        },2000);
    }

    @Override
    public void onResume() {
        super.onResume();
      //  Log.e("onResume","onResume");
        ((CashToCashSuccessWithQRCodeActivity) getActivity()).updateTitle(getString(R.string.str_transfer));
    }

    private void mappingContact(){
        listBitmap = new ArrayList<>();
        listUri = new ArrayList<>();
        contactsList= new ArrayList<>();

        if (valuesListAdapter != null && multiTransferList != null) {
            boolean checked=false;
            for (int i=0;i< multiTransferList.size();i++) {
                contactsList.add(new ContactTransfer(multiTransferList.get(i)));
                if(i== multiTransferList.size()-1){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createBitmap();
                        }
                    },3000);
                }
            }

        }else{
            dismissProgress();
        }
    }
    private void setData() {
        if (valuesListAdapter != null && contactsList != null) {
            view_qr_code.setVisibility(View.VISIBLE);
            tv_scan_receive.setVisibility(View.VISIBLE);
            tv_title.setText(String.format(getString(R.string.str_you_have_successfully_qr_press_arrow_to_view_other_qr), contactsList.size() + ""));
            adapter = new SlideQRCodeAdapter(getActivity(), valuesListAdapter, contactsList, content, type);

            view_pager.setAdapter(adapter);
            dismissProgress();
            if(multiTransferList.size()<=1){
                iv_left.setVisibility(View.GONE);
                iv_right.setVisibility(View.GONE);
            }else{
                iv_left.setVisibility(View.GONE);
                iv_right.setVisibility(View.VISIBLE);
            }

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
        }else{
            dismissProgress();
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
                if(valuesListAdapter == null && contactsList == null)
                    return;
                handleShareSave(true);
                break;
            case R.id.view_download:
                if(valuesListAdapter == null && contactsList == null)
                    return;
                handleShareSave(false);
                break;
        }
    }


    private void handleShareSave(boolean share){
       // bitmap = adapter.getBitmap();
        if(share){
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                handleShareList();
            }
        }else{
            if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                if (!CommonUtils.isExternalStorageWritable()) {
                    dismissProgress();
                    if (getActivity() != null)
                        showDialogErr(R.string.err_store_image);
                    return;
                }
                if(contactsList.size()==0){
                    showDialogErr(R.string.err_upload);
                    return;
                }
                boolean showToast=false;
                for(int i=0;i<contactsList.size();i++){
                    String currentTime = CommonUtils.getCurrentTime();
                    String imageName = contactsList.get(i).getWalletId() + "_" + currentTime + "_" + i;
                    if(i== listBitmap.size()-1){
                        showToast=true;
                    }
                    if(contactsList.get(i).getBitmap()!=null){
                        QRCodeUtil.saveImageQRCode(this,contactsList.get(i).getBitmap(),imageName, Constant.DIRECTORY_QR_IMAGE,showToast);
                    }
                }
            }
        }
    }
    private void showDialogErr(int err) {
        ((CashToCashSuccessWithQRCodeActivity) getActivity()).showDialogError(getString(err));
    }
    private void cashOutSuccess() {
        dismissProgress();
        Toast.makeText(getActivity(),getString(R.string.str_saved),Toast.LENGTH_SHORT).show();
    }
    private ArrayList<Uri> genericListUri(){
        return CommonUtils.genericListUri(getActivity(),multiTransferList,valuesListAdapter,content,type);

    }
    @SuppressLint("StaticFieldLeak")
    private void createBitmap(){
        String userName = ECashApplication.getAccountInfo().getUsername();
       AccountInfo accountInfo = DatabaseUtil.getAccountInfo(userName, getActivity());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (int i = 0; i < contactsList.size(); i++) {
                    ContactTransfer contact = contactsList.get(i);
                    Gson gson = new Gson();
                    ResponseMessSocket responseMessSocket = CommonUtils.getObjectJsonSendCashToCash(getActivity(), valuesListAdapter,
                            contact, content, i, type, accountInfo);
                    String jsonCash = gson.toJson(responseMessSocket);
                  //  Log.e("jsonCash",jsonCash);
                    List<String> stringList = CommonUtils.getSplittedString(jsonCash, 1000);
                    ArrayList<QRCodeSender> codeSenderArrayList = new ArrayList<>();
                   // ArrayList<QRScanBase> codeSenderArrayList = new ArrayList<>();
                    if (stringList.size() > 0) {
                        for (int j = 0; j < stringList.size(); j++) {
                            QRCodeSender qrCodeSender = new QRCodeSender();
                            qrCodeSender.setCycle(j + 1);
                            qrCodeSender.setTotal(stringList.size());
                            qrCodeSender.setContent(stringList.get(j));
                            qrCodeSender.setType(null);
                            codeSenderArrayList.add(qrCodeSender);
                        }
                        if (codeSenderArrayList.size() > 0) {
                            for (int j = 0; j < codeSenderArrayList.size(); j++) {
                               // Log.e("codeSenderArrayList",gson.toJson(codeSenderArrayList.get(j)));
                                Bitmap bitmap = CommonUtils.generateQRCode(gson.toJson(codeSenderArrayList.get(j)));
                                contact.setBitmap(bitmap);
                                listUri.add(CommonUtils.getBitmapUri(getActivity(),bitmap));
                                listBitmap.add(bitmap);
                                DatabaseUtil.saveTransactionLogQR(codeSenderArrayList, responseMessSocket, getActivity());
//                                if(j==codeSenderArrayList.size()-1){
//                                    setData();
//                                }
                            }
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().postSticky(new EventDataChange(Constant.CASH_OUT_MONEY_SUCCESS));
                    }
                },1000);

            }
        }.execute();
    }
    private void handleShareList(){
        if(listUri.size()>0){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUri);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, getString(R.string.str_share)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgress();
//                    CashOutFunction cashOutSocketFunction = new CashOutFunction(CashToCashSuccessWithQRCodeFragment.this, valuesListAdapter,
//                            multiTransferList, content, type);
//                    cashOutSocketFunction.handleCashOutQRCode(() -> cashOutSuccess());
                    handleShareSave(false);
                }
            }
            default:
                break;
        }
    }
}
