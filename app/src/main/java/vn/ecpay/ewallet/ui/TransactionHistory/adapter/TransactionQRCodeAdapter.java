package vn.ecpay.ewallet.ui.TransactionHistory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;

public class TransactionQRCodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  List<QRCodeSender> listQRCode;
  Context context;

  public TransactionQRCodeAdapter(List<QRCodeSender> mListQRCode, Context context) {
    this.listQRCode = mListQRCode;
    this.context = context;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new TransactionQRCodeAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr_code, parent, false));
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
    TransactionQRCodeAdapter.ItemViewHolder itemViewHolder = (TransactionQRCodeAdapter.ItemViewHolder) holder;
    QRCodeSender qrCodeSender = listQRCode.get(position);
    Gson gson = new Gson();
    itemViewHolder.ivQRCode.setImageBitmap(CommonUtils.generateQRCode(gson.toJson(qrCodeSender)));
    itemViewHolder.ivQRCode.setOnClickListener(v -> DialogUtil.getInstance().showDialogViewQRCode(context, CommonUtils.generateQRCode(gson.toJson(qrCodeSender))));
  }

  @Override
  public int getItemCount() {
    return listQRCode.size();
  }

  public static class ItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivQRCode;

    public ItemViewHolder(View itemView) {
      super(itemView);
      ivQRCode = itemView.findViewById(R.id.iv_qr_code);
    }
  }
}