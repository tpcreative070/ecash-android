package vn.ecpay.ewallet.common.utils;

import  android.content.Context;




import vn.ecpay.ewallet.R;

public class GetStringErrorCode {

    public String errorMessage(Context context, String error_code,String messageError){
        String message=context.getString(R.string.err_upload);
        //String message=messageError;

        switch (error_code) {
            case Constant.sesion_expid:
                message= context.getString(R.string.err_end_of_the_session);
            break;
            case  Constant.ERROR_CODE_1003:
                message= context.getString(R.string.error_message_code_1003);
                break;
            case  Constant.ERROR_CODE_3034:
                message= context.getString(R.string.error_message_code_3034);
                break;
            case  Constant.ERROR_CODE_0998:
                message= context.getString(R.string.error_message_code_0998);
                break;
            case  Constant.ERROR_CODE_3024:
                message= context.getString(R.string.error_message_code_3024);
                break;
            case  Constant.ERROR_CODE_3019:
                message= context.getString(R.string.error_message_code_3019);
                break;
            case  Constant.ERROR_CODE_3014:
                message= context.getString(R.string.error_message_code_3014);
                break;
            case  Constant.ERROR_CODE_3016:
                message= context.getString(R.string.error_message_code_3016);
                break;
            case  Constant.ERROR_CODE_3104:
                message= context.getString(R.string.error_message_code_3104);
                break;
            case  Constant.ERROR_CODE_0001:
                message= context.getString(R.string.error_message_code_0001);
                break;
            case  Constant.ERROR_CODE_0997:
                message= context.getString(R.string.error_message_code_0997);
                break;
            case  Constant.ERROR_CODE_3029:
                message= context.getString(R.string.error_message_code_3029);
                break;
            case  Constant.ERROR_CODE_4009:
                message= context.getString(R.string.error_message_code_4009);
                break;
            case  Constant.ERROR_CODE_4010:
                message= context.getString(R.string.error_message_code_4010);
                break;
            case  Constant.ERROR_CODE_4008:
                message= context.getString(R.string.error_message_code_4008);
                break;
            case  Constant.ERROR_CODE_4012:
                message= context.getString(R.string.error_message_code_4012);
                break;
            case  Constant.ERROR_CODE_4011:
                message= context.getString(R.string.error_message_code_4011);
                break;
            default:
                message=context.getString(R.string.err_upload);;
                break;
        }
        return message;
    }
}
