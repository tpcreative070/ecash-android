package vn.ecpay.ewallet.ui.wallet.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;

import javax.inject.Inject;

import vn.ecpay.ewallet.MainActivity;
import vn.ecpay.ewallet.common.eventBus.EventDataChange;
import vn.ecpay.ewallet.ui.wallet.view.AccountInfoView;

import static android.app.Activity.RESULT_OK;
import static vn.ecpay.ewallet.common.utils.Constant.REQUEST_IMAGE_CAPTURE;
import static vn.ecpay.ewallet.common.utils.Constant.REQUEST_TAKE_PHOTO;

public class AccountInfoPresenterImpl implements AccountInfoPresenter {

    @Inject
    public AccountInfoPresenterImpl() {
    }

    @Override
    public void setView(AccountInfoView view) {

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
    public void onCaptureImage(int requestCode) {

    }

    @Override
    public void onEvent(EventDataChange event) {
//        onActivityResult(event.getRequestCode(), event.getResultCode(), event.getDataImage(), event.getActivity());
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
//        Bitmap imageBitmap = null;
//        File mFileImage = null;
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            startCropActivity(imageUri, activity);
//        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK && data != null) {
//            // Get the file instance
//            Timber.e("REQUEST_TAKE_PHOTO");
//            mFileImage = FileUtils.resolveImage(activity, data.getData());
//            if (!ValidateUtils.validateLeghtFileImage(mFileImage).equals("")) {
//                mInfoUserView.onUpdateFail(ValidateUtils.validateLeghtFileImage(mFileImage));
//                return;
//            }
//            startCropActivity(data.getData(), activity);
//        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
//            Timber.e("REQUEST_TAKE_PHOTO");
//            handleCropResult(data);
//        }
//    }
//
//    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";
//    private void startCropActivity(@NonNull Uri uri, Activity activity) {
//        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
//        destinationFileName += ".png";
//        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(activity.getCacheDir(), destinationFileName)));
//        UCrop.Options options = new UCrop.Options();
//        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//        options.setCompressionQuality(90);
//        options.setHideBottomControls(false);
//        options.setFreeStyleCropEnabled(true);
//        uCrop.withOptions(options);
//        uCrop.withMaxResultSize(480, 480);
//        uCrop = uCrop.useSourceImageAspectRatio();
//        uCrop.start(activity);
//    }
}
