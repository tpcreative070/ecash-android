package vn.ecpay.ewallet.ui.lixi.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.model.lixi.CashTemp;
import vn.ecpay.ewallet.ui.interfaceListener.OnItemLixiClickListener;

public class MyLixiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<CashTemp> listCashTemp;
    private OnItemLixiClickListener onItemLixiClickListener;

    public MyLixiAdapter(List<CashTemp> mListCashTemp, Context activity, OnItemLixiClickListener onItemLixiClickListener) {
        this.context = activity;
        this.listCashTemp = mListCashTemp;
        this.onItemLixiClickListener = onItemLixiClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lixi, parent, false);
        return new ItemLixiHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (context == null) {
            return;
        }
        ItemLixiHolder itemViewHolder = (ItemLixiHolder) holder;
        CashTemp cashTemp = listCashTemp.get(position);
        itemViewHolder.tvTitle.setText(context.getString(R.string.str_lixi_sender, cashTemp.getSenderAccountId()));
        if (cashTemp.getStatus().equals(Constant.OPEN)) {
            itemViewHolder.ivLixi.setImageDrawable(context.getDrawable(R.drawable.ic_lixi_open));
            itemViewHolder.ivGift.setVisibility(View.GONE);
            itemViewHolder.tvStatus.setText(context.getResources().getString(R.string.str_lixi_open));
            itemViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            itemViewHolder.tvStatus.setTypeface(null, Typeface.NORMAL);
        } else {
            itemViewHolder.ivLixi.setImageDrawable(context.getDrawable(R.drawable.ic_lixi_close));
            itemViewHolder.ivGift.setVisibility(View.VISIBLE);
            itemViewHolder.tvStatus.setText(context.getResources().getString(R.string.str_lixi_close));
            itemViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.blue));
            itemViewHolder.tvStatus.setTypeface(null, Typeface.BOLD);
        }
        itemViewHolder.layoutContent.setOnClickListener(v -> {
            listCashTemp.get(position).setStatus(Constant.OPEN);
            notifyItemChanged(position);
            onItemLixiClickListener.lixiClickListener(cashTemp);
        });
    }

    @Override
    public int getItemCount() {
        return listCashTemp.size();
    }

    public static class ItemLixiHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvStatus;
        ImageView ivLixi, ivGift;
        RelativeLayout layoutContent;

        public ItemLixiHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvStatus = itemView.findViewById(R.id.tv_status);
            ivLixi = itemView.findViewById(R.id.iv_lixi);
            ivGift = itemView.findViewById(R.id.iv_gift);
            layoutContent = itemView.findViewById(R.id.layout_content);
        }
    }
}