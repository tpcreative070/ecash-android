package vn.ecpay.ewallet.ui.cashToCash.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransfer;

public class SlideQRCodeAdapter extends PagerAdapter {
    private List<CashTotal> listCashTotal;
    private List<ContactTransfer> listContact;
    private ArrayList<Bitmap> listUri;
    private LayoutInflater inflater;
    private Context context;
    private String content;
    private String typeSend;
    private AccountInfo accountInfo;

    public SlideQRCodeAdapter(Context context, List<CashTotal> listCashTotal, List<ContactTransfer> multiTransferList, String content, String typeSend) {
        this.context = context;
    //    this.listUri = listUri;
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
            ContactTransfer contact =listContact.get(position);
            tv_wallet_receive.setText(String.valueOf(contact.getWalletId()));
            if(contact.getBitmap()!=null){
                iv_qr_code.setImageBitmap(contact.getBitmap());
            }


        }
        view.addView(imageLayout, 0);

        return imageLayout;
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
