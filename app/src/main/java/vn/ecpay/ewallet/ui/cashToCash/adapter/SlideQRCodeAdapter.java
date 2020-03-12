package vn.ecpay.ewallet.ui.cashToCash.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.QRCodeUtil;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

public class SlideQRCodeAdapter extends PagerAdapter {
    private List<CashTotal> listCashTotal;
    private List<Contact> listContact;
    private LayoutInflater inflater;
    private Context context;
    private String content;
    private String typeSend;
    private AccountInfo accountInfo;
    private Bitmap bitmap;

    public SlideQRCodeAdapter(Context context, List<CashTotal> listCashTotal, List<Contact> multiTransferList, String content, String typeSend) {
        this.context = context;
        this.listCashTotal = listCashTotal;
        this.listContact = multiTransferList;
        this.typeSend = typeSend;
        inflater = LayoutInflater.from(context);
        String userName = ECashApplication.getAccountInfo().getUsername();
        accountInfo = DatabaseUtil.getAccountInfo(userName, context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listContact.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.item_pager_qr_code_transfer, view, false);
        if (imageLayout != null) {
            ImageView iv_qr_code = imageLayout.findViewById(R.id.iv_qr_code);
            TextView tv_wallet_receive = imageLayout.findViewById(R.id.tv_wallet_receive);
            String currentTime = CommonUtils.getCurrentTime();
            Gson gson = new Gson();
            Contact contact =listContact.get(position);
            ResponseMessSocket responseMessSocket = CommonUtils.getObjectJsonSendCashToCash(context, listCashTotal,
                    contact, content, position, typeSend, accountInfo);
            String jsonCash = gson.toJson(responseMessSocket);
            List<String> stringList = CommonUtils.getSplittedString(jsonCash, 1000);
            ArrayList<QRCodeSender> codeSenderArrayList = new ArrayList<>();
            if (stringList.size() > 0) {
                for (int j = 0; j < stringList.size(); j++) {
                    QRCodeSender qrCodeSender = new QRCodeSender();
                    qrCodeSender.setCycle(j + 1);
                    qrCodeSender.setTotal(stringList.size());
                    qrCodeSender.setContent(stringList.get(j));
                    codeSenderArrayList.add(qrCodeSender);
                }
                tv_wallet_receive.setText(String.valueOf(contact.getWalletId()));
                if (codeSenderArrayList.size() > 0) {
                    for (int j = 0; j < codeSenderArrayList.size(); j++) {
                        bitmap = CommonUtils.generateQRCode(gson.toJson(codeSenderArrayList.get(j)));
                       // String imageName = contact.getWalletId() + "_" + currentTime + "_" + j;
                        //QRCodeUtil.saveImageQRCode(context,bitmap,imageName, Constant.DIRECTORY_QR_IMAGE);
                        iv_qr_code.setImageBitmap(bitmap);
                    }
                    //save log
                   // DatabaseUtil.saveTransactionLogQR(codeSenderArrayList, responseMessSocket, context);
                }
            }
        }
        view.addView(imageLayout, 0);

        return imageLayout;
    }
    public Bitmap getBitmap(){
        return this.bitmap;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
