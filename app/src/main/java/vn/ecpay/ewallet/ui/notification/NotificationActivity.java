package vn.ecpay.ewallet.ui.notification;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DatabaseUtil;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.model.notification.NotificationObj;

public class NotificationActivity extends ECashBaseActivity {
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.rv_notification)
    RecyclerView rvNotification;
    @BindView(R.id.iv_option)
    ImageView ivOption;
    private List<NotificationObj> listNotification;
    private NotificationAdapter notificationAdapter;
    private boolean isCheck = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.notification_activity;
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAdapter(false);
    }

    private void setAdapter(boolean isCheckBox) {
        listNotification = DatabaseUtil.getAllNotification(this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNotification.setLayoutManager(mLayoutManager);
        notificationAdapter = new NotificationAdapter(isCheckBox, listNotification, this);
        rvNotification.setAdapter(notificationAdapter);
    }

    @OnClick({R.id.tv_delete, R.id.tv_cancel, R.id.iv_back, R.id.iv_delete, R.id.iv_option})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_delete:
                for (int i = listNotification.size() - 1; i >= 0; i--) {
                    if (listNotification.get(i).getIsCheck() == 1) {
                        DatabaseUtil.deleteNotification(this, listNotification.get(i).getId());
                        listNotification.remove(i);
                    }
                }
                notificationAdapter.notifyDataSetChanged();
                EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_NOTIFICATION));
                break;
            case R.id.tv_cancel:
                isCheck = false;
                setAdapter(false);
                layoutBottom.setVisibility(View.GONE);
                ivOption.setImageDrawable(getDrawable(R.drawable.ic_list_add));
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_delete:
                if (listNotification.size() > 0) {
                    DialogUtil.getInstance().showDialogConfirm(this, getString(R.string.confirm),
                            getString(R.string.str_confirm_delete_notification), new DialogUtil.OnConfirm() {
                                @Override
                                public void OnListenerOk() {
                                    DatabaseUtil.delAllNotification(getApplicationContext());
                                    setAdapter(false);
                                    EventBus.getDefault().postSticky(new EventDataChange(Constant.UPDATE_NOTIFICATION));
                                }

                                @Override
                                public void OnListenerCancel() {

                                }
                            });
                } else {
                    Toast.makeText(this, getString(R.string.str_delete_all_notification), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.iv_option:
                if (isCheck) {
                    isCheck = false;
                    setAdapter(false);
                    layoutBottom.setVisibility(View.GONE);
                    ivOption.setImageDrawable(getDrawable(R.drawable.ic_list_add));
                } else {
                    isCheck = true;
                    setAdapter(true);
                    layoutBottom.setVisibility(View.VISIBLE);
                    ivOption.setImageDrawable(getDrawable(R.drawable.ic_list_add_active));
                }
                break;
        }
    }
}
