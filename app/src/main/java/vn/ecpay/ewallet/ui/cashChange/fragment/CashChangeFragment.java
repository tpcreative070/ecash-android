package vn.ecpay.ewallet.ui.cashChange.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.ui.cashChange.CashChangeActivity;

public class CashChangeFragment extends ECashBaseFragment {
    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_over_ecash)
    TextView tvOverEcash;
    @BindView(R.id.tv_500)
    TextView tv500;
    @BindView(R.id.tv_sl_500)
    TextView tvSl500;
    @BindView(R.id.iv_down_500)
    ImageView ivDown500;
    @BindView(R.id.tv_total_500)
    TextView tvTotal500;
    @BindView(R.id.iv_up_500)
    ImageView ivUp500;
    @BindView(R.id.tv_200)
    TextView tv200;
    @BindView(R.id.tv_sl_200)
    TextView tvSl200;
    @BindView(R.id.iv_down_200)
    ImageView ivDown200;
    @BindView(R.id.tv_total_200)
    TextView tvTotal200;
    @BindView(R.id.iv_up_200)
    ImageView ivUp200;
    @BindView(R.id.tv_100)
    TextView tv100;
    @BindView(R.id.tv_sl_100)
    TextView tvSl100;
    @BindView(R.id.iv_down_100)
    ImageView ivDown100;
    @BindView(R.id.tv_total_100)
    TextView tvTotal100;
    @BindView(R.id.iv_up_100)
    ImageView ivUp100;
    @BindView(R.id.tv_50)
    TextView tv50;
    @BindView(R.id.tv_sl_50)
    TextView tvSl50;
    @BindView(R.id.iv_down_50)
    ImageView ivDown50;
    @BindView(R.id.tv_total_50)
    TextView tvTotal50;
    @BindView(R.id.iv_up_50)
    ImageView ivUp50;
    @BindView(R.id.tv_20)
    TextView tv20;
    @BindView(R.id.tv_sl_20)
    TextView tvSl20;
    @BindView(R.id.iv_down_20)
    ImageView ivDown20;
    @BindView(R.id.tv_total_20)
    TextView tvTotal20;
    @BindView(R.id.iv_up_20)
    ImageView ivUp20;
    @BindView(R.id.tv_10)
    TextView tv10;
    @BindView(R.id.tv_sl_10)
    TextView tvSl10;
    @BindView(R.id.iv_down_10)
    ImageView ivDown10;
    @BindView(R.id.tv_total_10)
    TextView tvTotal10;
    @BindView(R.id.iv_up_10)
    ImageView ivUp10;
    @BindView(R.id.btn_cash_change)
    Button btnCashChange;
    @BindView(R.id.btn_cash_take)
    Button btnCashTake;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected int getLayoutResId() {
        return R.layout.cash_change_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CashChangeActivity) getActivity()).updateTitle(getString(R.string.str_change_cash));
    }

    @OnClick({R.id.iv_down_500, R.id.iv_up_500, R.id.iv_down_200, R.id.iv_up_200, R.id.iv_down_100, R.id.iv_up_100, R.id.iv_down_50, R.id.iv_up_50, R.id.iv_down_20, R.id.iv_up_20, R.id.iv_down_10, R.id.iv_up_10, R.id.btn_cash_change, R.id.btn_cash_take, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_down_500:
                break;
            case R.id.iv_up_500:
                break;
            case R.id.iv_down_200:
                break;
            case R.id.iv_up_200:
                break;
            case R.id.iv_down_100:
                break;
            case R.id.iv_up_100:
                break;
            case R.id.iv_down_50:
                break;
            case R.id.iv_up_50:
                break;
            case R.id.iv_down_20:
                break;
            case R.id.iv_up_20:
                break;
            case R.id.iv_down_10:
                break;
            case R.id.iv_up_10:
                break;
            case R.id.btn_cash_change:
                break;
            case R.id.btn_cash_take:
                break;
            case R.id.btn_confirm:
                break;
        }
    }
}
