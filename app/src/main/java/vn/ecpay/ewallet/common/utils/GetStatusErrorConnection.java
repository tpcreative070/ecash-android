package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.HttpException;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.model.ErrorStatusConnectionModel;

public class GetStatusErrorConnection {
    public ErrorStatusConnectionModel error(Context context, Throwable t) {
        ErrorStatusConnectionModel error = new ErrorStatusConnectionModel();
        error.setTitle(context.getString(R.string.str_error_connection));
        if (t instanceof SocketTimeoutException) {
            // error.setMessage(context.getString(R.string.str_error_connection_request_timeout));
            error.setMessage(context.getString(R.string.str_error_connection_internet));
        } else if (t instanceof ConnectException || t instanceof UnknownHostException) {
            error.setMessage(context.getString(R.string.str_error_connection_internet));
        } else if (t instanceof HttpException) {
            switch (((HttpException) t).code()) {
                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
                    // Log.e("Error  ","Internal Server Error");
                    error.setMessage(context.getString(R.string.str_error_connection_internal_server));
                    break;
                // ToDo
                //  case HttpsURLConnection.HTTP_BAD_REQUEST:
                // Log.e("Error  ","Bad Request");
                //   break;
//                case HttpsURLConnection.HTTP_UNAUTHORIZED:
//                    Log.e("Error  ","Unauthorised User");
//                    break;
//                case HttpsURLConnection.HTTP_FORBIDDEN:
//                    Log.e("Error  ","Forbidden");
//                    break;
                default:
                    // error.setMessage(context.getString(R.string.str_error_connection_internet));
                    error.setMessage(context.getString(R.string.err_upload));
                    break;
            }
        } else if (t instanceof JsonSyntaxException) {
            //Log.e("Error  ","the API not response");
            error.setMessage(context.getString(R.string.err_upload));
        } else {
            error.setMessage(context.getString(R.string.err_upload));
        }
        return error;

    }
}
