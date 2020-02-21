package vn.ecpay.ewallet.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;

import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;

public class QRCodeUtil {
  public final static int QRCodeWidth = 400;

  private Bitmap generateQrcode(String Value, Context context) throws WriterException {
    BitMatrix bitMatrix;
    try {
      bitMatrix = new MultiFormatWriter().encode(
              Value,
              BarcodeFormat.DATA_MATRIX.QR_CODE,
              QRCodeWidth, QRCodeWidth, null
      );
    } catch (IllegalArgumentException Illegalargumentexception) {
      return null;
    }
    int bitMatrixWidth = bitMatrix.getWidth();

    int bitMatrixHeight = bitMatrix.getHeight();

    int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

    for (int y = 0; y < bitMatrixHeight; y++) {
      int offset = y * bitMatrixWidth;

      for (int x = 0; x < bitMatrixWidth; x++) {

        pixels[offset + x] = bitMatrix.get(x, y) ?
                context.getResources().getColor(R.color.black) : context.getResources().getColor(R.color.white);
      }
    }
    Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

    bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
    return bitmap;
  }
  @SuppressLint("StaticFieldLeak")
  public static  void saveImageQRCode(ECashBaseFragment fragment, Bitmap bitmap, String name, String directory){
    if (PermissionUtils.checkPermissionWriteStore(fragment, null)) {
      fragment.showProgress();
      new AsyncTask<Void, Void, Void>() {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
          String root = Environment.getExternalStorageDirectory().toString();
          File mFolder = new File(root + directory);
          if (!mFolder.exists()) {
            mFolder.mkdir();
          }
          String imageName = name + ".jpg";
          File file = new File(mFolder, imageName);
          if (file.exists())
            file.delete();
          try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
          } catch (Exception e) {
            Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.err_upload), Toast.LENGTH_LONG).show();
            e.printStackTrace();
          }
          return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
          fragment.dismissProgress();
          Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.str_save_to_device_success), Toast.LENGTH_LONG).show();
        }
      }.execute();
    }

  }
}
