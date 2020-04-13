package vn.ecpay.ewallet.common.utils;

import android.content.Context;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;

public class CheckErrCodeUtil {
    public static void errorMessage(Context context, String error_code) {
        String message;
        switch (error_code) {
            case Constant.sesion_expid:
                ECashApplication.showDialogSwitchLogin(context.getString(R.string.err_end_of_the_session));
                break;
            case Constant.ERROR_CODE_3019:
                message = context.getString(R.string.error_message_code_3019);
                ECashApplication.showDialogSwitchLogin(message);
                break;
            case Constant.ERROR_CODE_1003:
                message = context.getString(R.string.error_message_code_1003);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_1006:
                message = context.getString(R.string.error_message_code_1006);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3034:
                message = context.getString(R.string.error_message_code_3034);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_1094:
                message = context.getString(R.string.error_message_code_1094);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_1095:
                message = context.getString(R.string.error_message_code_1095);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3014:
                message = context.getString(R.string.error_message_code_3014);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3015:
                message = context.getString(R.string.error_message_code_3015);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3016:
                message = context.getString(R.string.error_message_code_3016);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3035:
                message = context.getString(R.string.error_message_code_3035);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3077:
                message = context.getString(R.string.error_message_code_3077);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3104:
                message = context.getString(R.string.error_message_code_3104);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_0001:
                message = context.getString(R.string.error_message_code_0001);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_0997:
                message = context.getString(R.string.error_message_code_0997);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_3029:
                message = context.getString(R.string.error_message_code_3029);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4009:
                message = context.getString(R.string.error_message_code_4009);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4010:
                message = context.getString(R.string.error_message_code_4010);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4008:
                message = context.getString(R.string.error_message_code_4008);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4012:
                message = context.getString(R.string.error_message_code_4012);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4011:
                message = context.getString(R.string.error_message_code_4011);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4129:
                message = context.getString(R.string.error_message_code_4129);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_4160:
                message = context.getString(R.string.error_message_code_4160);
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
            case Constant.ERROR_CODE_0998:
            default:
                message = context.getString(R.string.err_upload) + " => mã lỗi: " + error_code;
                DialogUtil.getInstance().showDialogWarning(context, message);
                break;
        }
    }
}
