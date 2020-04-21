package vn.ecpay.ewallet.ui.wallet.presenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.api_request.APIService;
import vn.ecpay.ewallet.common.api_request.RetroClientApi;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.common.utils.CheckErrCodeUtil;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.common.utils.FileUtils;
import vn.ecpay.ewallet.common.utils.ImageBase64;
import vn.ecpay.ewallet.cropImage.CropView;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.account.updateAvartar.request.RequestUpdateAvartar;
import vn.ecpay.ewallet.model.account.updateAvartar.response.ResponseUpdateAvartar;
import vn.ecpay.ewallet.ui.wallet.view.AccountInfoView;

import static android.app.Activity.RESULT_OK;
import static vn.ecpay.ewallet.common.utils.Constant.REQUEST_IMAGE_CAPTURE;
import static vn.ecpay.ewallet.common.utils.Constant.REQUEST_TAKE_PHOTO;

public class AccountInfoPresenterImpl implements AccountInfoPresenter {
    private AccountInfoView accountInfoView;
    private Uri imageUri;
    @Inject
    ECashApplication application;

    @Inject
    public AccountInfoPresenterImpl() {
    }

    @Override
    public void setView(AccountInfoView view) {
        this.accountInfoView = view;
    }

    @Override
    public void onViewCreate() {

    }

    @Override
    public void onViewStart() {

    }

    @Override
    public void onViewResume() {

    }

    @Override
    public void onViewPause() {

    }

    @Override
    public void onViewStop() {

    }

    @Override
    public void onViewDestroy() {

    }

    @Override
    public void onChooseImage(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((MainActivity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode);
    }

    @Override
    public void onCaptureImage(Context activity, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        try {
            imageUri = application.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                ((MainActivity) activity).startActivityForResult(takePictureIntent, requestCode);
            }
        } catch (UnsupportedOperationException e) {
            accountInfoView.showDialogError(activity.getResources().getString(R.string.err_device_un_support));
        }
    }

    @Override
    public void onEvent(EventDataChange event, AccountInfo accountInfo) {
        onActivityResult(event.getRequestCode(), event.getResultCode(), event.getDataImage(), event.getActivity(), accountInfo);
    }

    private void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity, AccountInfo accountInfo) {
        File mFileImage;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            startCropActivity(imageUri, activity, accountInfo);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
            mFileImage = FileUtils.resolveImage(activity, data.getData());
            if (!CommonUtils.validateLeghtFileImage(mFileImage).equals("")) {
                accountInfoView.onUpdateFail(CommonUtils.validateLeghtFileImage(mFileImage));
                return;
            }
            startCropActivity(data.getData(), activity, accountInfo);
        }
    }

    private void startCropActivity(@NonNull Uri uri, Activity activity, AccountInfo accountInfo) {
        Dialog dialog = new Dialog(activity, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_crop_image);
        CropView cropView = dialog.findViewById(R.id.cropView);
        cropView.of(uri).asSquare().initialize(activity);

        Button btnCancel = dialog.findViewById(R.id.cancelBtn);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        Button btnDone = dialog.findViewById(R.id.doneBtn);
        btnDone.setOnClickListener(v -> {
            handleCropResult(cropView.getOutput(), activity, accountInfo);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void handleCropResult(@NonNull Bitmap result, Context context, AccountInfo accountInfo) {
        accountInfoView.showLoading();
        try {
            try {
                final String encodedImage = ImageBase64.encodeTobase64(result).replace("\n", "");
                if (encodedImage != null) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> changeAvatar(encodedImage, context, accountInfo), 1000);
                } else {
                    accountInfoView.showDialogError(application.getResources().getString(R.string.err_upload));
                }
            } catch (Exception e) {
                e.printStackTrace();
                accountInfoView.showDialogError(application.getResources().getString(R.string.err_update_avatar_fails));
                accountInfoView.dismissLoading();
                try {
                    throw new IOException();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeAvatar(String encodedImage, Context context, AccountInfo accountInfo) {
        Retrofit retrofit = RetroClientApi.getRetrofitClient(application.getString(R.string.api_base_url));
        APIService apiService = retrofit.create(APIService.class);

        RequestUpdateAvartar requestUpdateAvartar = new RequestUpdateAvartar();
        requestUpdateAvartar.setChannelCode(Constant.CHANNEL_CODE);
        requestUpdateAvartar.setFunctionCode(Constant.FUNCTION_UPDATE_AVARTAR);
        requestUpdateAvartar.setLarge(encodedImage);
        requestUpdateAvartar.setSessionId(CommonUtils.getSessionId(context));
        requestUpdateAvartar.setToken(CommonUtils.getToken(context));
        requestUpdateAvartar.setmUsername(ECashApplication.getAccountInfo().getUsername());
        requestUpdateAvartar.setAuditNumber(CommonUtils.getAuditNumber());
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(requestUpdateAvartar));
        requestUpdateAvartar.setChannelSignature(CommonUtils.generateSignature(dataSign));
        CommonUtils.logJson(requestUpdateAvartar);
        Call<ResponseUpdateAvartar> call = apiService.updateAvartar(requestUpdateAvartar);
        call.enqueue(new Callback<ResponseUpdateAvartar>() {
            @Override
            public void onResponse(Call<ResponseUpdateAvartar> call, Response<ResponseUpdateAvartar> response) {
                accountInfoView.dismissLoading();
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (null != response.body().getResponseCode()) {
                        if (response.body().getResponseCode().equals(Constant.CODE_SUCCESS)) {
                            if (null != response.body().getResponseData()) {
                                ECashApplication.getAccountInfo().setLarge(encodedImage);
                                WalletDatabase.getINSTANCE(context, ECashApplication.masterKey);
                                WalletDatabase.updateAccountAvatar(encodedImage, accountInfo.getUsername());
                                EventBus.getDefault().postSticky(new EventDataChange(Constant.EVENT_UPDATE_AVARTAR));
                            } else {
                                accountInfoView.showDialogError(application.getString(R.string.err_upload));
                            }
                        } else {
                            CheckErrCodeUtil.errorMessage(context, response.body().getResponseCode());
                        }
                    } else {
                        accountInfoView.showDialogError(application.getString(R.string.err_upload));
                    }
                } else {
                    accountInfoView.showDialogError(application.getString(R.string.err_upload));
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdateAvartar> call, Throwable t) {
                accountInfoView.dismissLoading();
                accountInfoView.showDialogError(application.getString(R.string.err_upload));
            }
        });
    }
}
