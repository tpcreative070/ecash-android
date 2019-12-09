package vn.ecpay.ewallet.ui.wallet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.DialogUtil;
import vn.ecpay.ewallet.common.utils.LanguageUtils;
import vn.ecpay.ewallet.common.utils.PermissionUtils;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.ui.wallet.activity.MyQRCodeActivity;
import vn.ecpay.ewallet.ui.wallet.module.AccountInfoModule;
import vn.ecpay.ewallet.ui.wallet.module.MyWalletModule;
import vn.ecpay.ewallet.ui.wallet.presenter.AccountInfoPresenter;
import vn.ecpay.ewallet.ui.wallet.view.AccountInfoView;

import static vn.ecpay.ewallet.common.utils.Constant.REQUEST_IMAGE_CAPTURE;
import static vn.ecpay.ewallet.common.utils.Constant.REQUEST_TAKE_PHOTO;

public class FragmentAccountInfo extends ECashBaseFragment implements AccountInfoView {
    @BindView(R.id.iv_account)
    CircleImageView ivAccount;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_sdt)
    TextView tvSdt;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_cmnd)
    TextView tvCmnd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    private AccountInfo accountInfo;
    private ArrayList<EdongInfo> listEDongInfo;
    @Inject
    AccountInfoPresenter accountInfoPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_account_info;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ECashApplication.get(getActivity()).getApplicationComponent().plus(new AccountInfoModule(this)).inject(this);
        accountInfoPresenter.setView(this);
        accountInfoPresenter.onViewCreate();
        setData();
    }

    private void setData() {
        WalletDatabase.getINSTANCE(getContext(), ECashApplication.masterKey);
        accountInfo = WalletDatabase.getAccountInfoTask(ECashApplication.getAccountInfo().getUsername());
        listEDongInfo = ECashApplication.getListEDongInfo();
        if (accountInfo != null) {
            tvId.setText(String.valueOf(accountInfo.getWalletId()));
            tvName.setText(CommonUtils.getFullName(accountInfo));
            tvSdt.setText(accountInfo.getPersonMobilePhone());
            tvEmail.setText("");
            tvCmnd.setText(accountInfo.getIdNumber());
        } else {
            tvId.setText("");
            tvName.setText("");
            tvSdt.setText("");
            tvEmail.setText("");
            tvCmnd.setText("");
        }
        if (LanguageUtils.getCurrentLanguage().getCode().equals(getActivity().getResources().getString(R.string.language_english_code))) {
            tvLanguage.setText(getActivity().getResources().getString(R.string.language_english));
        } else {
            tvLanguage.setText(getActivity().getResources().getString(R.string.language_vietnamese));
        }
    }

    @OnClick({R.id.layout_change_language, R.id.layout_export_db, R.id.iv_account, R.id.btn_login, R.id.layout_image_account, R.id.layout_name, R.id.layout_phone, R.id.layout_email, R.id.layout_cmnd, R.id.layout_qr_code})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_change_language:
                DialogUtil.getInstance().showDialogChangeLanguage(getActivity(), new DialogUtil.OnChangeLanguage() {
                    @Override
                    public void OnListenerVn() {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                    @Override
                    public void OnListenerEn() {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
                break;
            case R.id.layout_image_account:
                if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                    if (PermissionUtils.checkPermissionCamera(this, null)) {
                        showDialogChangeAvatar();
                    }
                }
                break;
            case R.id.iv_account:
                break;
            case R.id.btn_login:
                break;
            case R.id.layout_name:
                break;
            case R.id.layout_phone:
                break;
            case R.id.layout_email:
                break;
            case R.id.layout_cmnd:
                break;
            case R.id.layout_qr_code:
                Intent intent = new Intent(getActivity(), MyQRCodeActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.layout_export_db:
                if (PermissionUtils.checkPermissionWriteStore(this, null)) {
                    showProgress();
                    exPortDBFile(getActivity());
                }
                break;
        }
    }

    private void showDialogChangeAvatar() {
        DialogUtil.getInstance().showDialogChangeAvatar(getActivity(), new DialogUtil.OnChangeAvatar() {
            @Override
            public void OnListenerFromStore() {
                accountInfoPresenter.onChooseImage(getActivity(), REQUEST_TAKE_PHOTO);
            }

            @Override
            public void OnListenerTakePhoto() {
                accountInfoPresenter.onCaptureImage(REQUEST_IMAGE_CAPTURE);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionUtils.MY_CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialogChangeAvatar();
                }
            }
            default:
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateData(EventDataChange event) {
        if (event.getData().equals(Constant.UPDATE_MONEY)) {
            setData();
        }
        if (event.getData().equals(Constant.UPDATE_ACCOUNT_LOGIN)) {
            setData();
        }
        if (event.getData().equals(Constant.UPDATE_IMAGE_AVATAR)) {
            accountInfoPresenter.onEvent(event);
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    @OnClick(R.id.layout_change_language)
    public void onViewClicked() {
    }

    private void exPortDBFile(Context context) {
        try {
            exportFile(context.getDatabasePath(Constant.DATABASE_NAME).getAbsolutePath(), Constant.DATABASE_NAME);
            exportFile(context.getDatabasePath(Constant.DATABASE_NAME + "-shm").getAbsolutePath(), Constant.DATABASE_NAME + "-shm");
            exportFile(context.getDatabasePath(Constant.DATABASE_NAME + "-wal").getAbsolutePath(), Constant.DATABASE_NAME + "-wal");
        } catch (Exception e) {
            dismissProgress();
            Toast.makeText(getActivity(), getResources().getString(R.string.err_upload), Toast.LENGTH_LONG).show();
        }
        dismissProgress();
        Toast.makeText(getActivity(), "Export file DB thành công", Toast.LENGTH_LONG).show();
    }

    private File exportFile(String input, String dst) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        File mFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/eCash_DB");
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }
        File expFile = new File(mFolder, dst);
        FileOutputStream outputStream = new FileOutputStream(expFile);
        try {
            inChannel = new FileInputStream(input).getChannel();
            outChannel = outputStream.getChannel();
        } catch (FileNotFoundException e) {
            dismissProgress();
            Toast.makeText(getActivity(), getResources().getString(R.string.err_upload), Toast.LENGTH_LONG).show();
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }

        return expFile;
    }

    @Override
    public void showLoading() {
        showProgress();
    }

    @Override
    public void dismissLoading() {
        dismissProgress();
    }
}
