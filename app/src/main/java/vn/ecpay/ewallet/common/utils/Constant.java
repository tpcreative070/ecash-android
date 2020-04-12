package vn.ecpay.ewallet.common.utils;

public class Constant {
    public static final String ACTIVITY_RESULT = "ActivityResult";
    public static final int REQUEST_IMAGE_CAPTURE = 3;
    public static final int REQUEST_TAKE_PHOTO = 2;
    public static final int REQUEST_CONTACT_PAYTO = 4;
    public static final int REQUEST_QR_CODE = 5;

    public static final String app_name = "eCash App";
    public static final String sesion_expid = "3043";
    public static final String ERROR_CODE_1095 = "1095";
    public static final String ERROR_CODE_3077 = "3077";
    public static final String ERROR_CODE_0998 = "0998";
    public static final String ERROR_CODE_1003 = "1003";
    public static final String ERROR_CODE_1006 = "1006";
    public static final String ERROR_CODE_3034 = "3034";
    public static final String ERROR_CODE_3024 = "3024";
    public static final String ERROR_CODE_3019 = "3019";
    public static final String ERROR_CODE_1094 = "1094";
    public static final String ERROR_CODE_4160 = "4160";
    public static final String ERROR_CODE_3014 = "3014";
    public static final String ERROR_CODE_3015 = "3015";
    public static final String ERROR_CODE_3016 = "3016";
    public static final String ERROR_CODE_3104 = "3104";
    public static final String ERROR_CODE_0001 = "0001";
    public static final String ERROR_CODE_0997 = "0997";
    public static final String ERROR_CODE_3035 = "3035";
    // public static final String ERROR_CODE_3019 = "3019";
    // public static final String ERROR_CODE_0998 = "0998";
    public static final String ERROR_CODE_3029 = "3029";
    //  public static final String ERROR_CODE_3035 = "3035";
    public static final String ERROR_CODE_0000 = "0000";
    public static final String ERROR_CODE_4009 = "4009";
    public static final String ERROR_CODE_4010 = "4010";
    public static final String ERROR_CODE_4008 = "4008";
    // public static final String ERROR_CODE_4010 = "4010";
    public static final String ERROR_CODE_4012 = "4012";
    public static final String ERROR_CODE_4011 = "4011";
    public static final String ERROR_CODE_4129 = "4129";

    public static final int DEFAULT_LANGUAGE_ID = 0;

    public static final String DATABASE_NAME = "eWallet";
    public static final int DATABASE_VERSION = 1;
    public static final byte ELEMENT_SPLIT = '$';

    public static final String DIRECTORY_QR_MY_CONTACT = "/qr_my_account";
    public static final String DIRECTORY_QR_MY_PAYMENT = "/qr_my_payment";
    public static final String DIRECTORY_QR_IMAGE = "/qr_image";


    //type notification
    public static final String ON = "on";
    public static final String OFF = "off";

    //type Lixi
    public static final String OPEN = "OPEN";
    public static final String CLOSE = "CLOSE";

    //object transfer
    public static final String USER_NAME = "USER_NAME";
    public static final String FORGOT_PASS_TRANSFER_MODEL = "FORGOT_PASS_TRANSFER_MODEL";
    public static final String CONTACT_TRANSFER_MODEL = "CONTACT_TRANSFER_MODEL";
    public static final String CONTACT_MULTI_TRANSFER = "CONTACT_MULTI_TRANSFER";
    public static final String TRANSACTIONS_HISTORY_MODEL = "TRANSACTIONS_HISTORY_MODEL";
    public static final String IS_SESSION_TIMEOUT = "IS_SESSION_TIMEOUT";
    public static final String CONTACT_MULTIPLE_CHOICE = "CONTACT_MULTIPLE_CHOICE";
    public static final String CASH_TOTAL_TRANSFER = "CASH_TOTAL_TRANSFER";
    public static final String CONTENT_TRANSFER = "CONTENT_TRANSFER";
    public static final String TYPE_TRANSFER = "TYPE_TRANSFER";
    public static final String URI_TRANSFER = "URI_TRANSFER";
    //date
    public static String FORMAT_DATE_SEND_CASH = "yyyyMMdd HH:mm:ss";
    public static String FORMAT_DATE_NOTIFICATION = "HH:mm:ss dd-MM-yyyy";
    public static String FORMAT_DATE_TOPAY = "yyyyMMdd HH:mm:ss";
    //type and key
    public static final String STR_CASH_IN = "in";
    public static final String STR_CASH_OUT = "out";
    public static final int CONTACT_ON = 1;
    public static final int CONTACT_OFF = 0;
    //    public static final String STR_PRIVATE_KEY_CHANEL = "AO6scDS+DBCtzmHVaQGnMM28Ir+kBJpQjOhPkc/fQeJb";
//    public static final String STR_SERVER_KEY_CHANEL = "BJ/wxgRRdijU/YaJCmJ/jUera8SduNDqJdKra4Iph7ErEsvNQgNu7tpmwD+XLbxXTPpY9MBP08H5GS54Wb7XmB0=";
    public static final String STR_EMPTY = "";
    public static final String DEVICE_IMEI = "DEVICE_IMEI";

    //key store
    public static final String INSTANCE_KS_ENCRYPT_PRIVATE = "INSTANCE_KS_ENCRYPT_PRIVATE";
    public static final String INSTANCE_KS_ENCRYPT_MASTER = "INSTANCE_KS_ENCRYPT_MASTER";

    public static final String WALLET_ALIAS_PRIVATE_KEY = "WALLET_ALIAS_PRIVATE_KEY";
    public static final String WALLET_ALIAS_MASTER_KEY = "WALLET_ALIAS_PUB_KEY";

    //vent bus
    public static final String EVENT_UPDATE_AVARTAR = "EVENT_UPDATE_AVARTAR";
    public static final String EVENT_CHOSE_IMAGE = "EVENT_CHOSE_IMAGE";
    public static final String UPDATE_NOTIFICATION = "UPDATE_NOTIFICATION";
    public static final String UPDATE_ACCOUNT_LOGIN = "update_account_login";

    public static final String EVENT_CASH_OUT_MONEY = "EVENT_CASH_OUT_MONEY";
    public static final String CASH_OUT_MONEY_SUCCESS = "cash_out_money_success";

    public static final String EVENT_CONNECT_SOCKET_FAIL = "EVENT_CONNECT_SOCKET_FAIL";
    public static final String EVENT_VERIFY_CASH_FAIL = "EVENT_VERIFY_CASH_FAIL";
    public static final String EVENT_UPDATE_CONTACT = "EVENT_UPDATE_CONTACT";
    public static final String EVENT_NETWORK_CHANGE = "EVENT_NETWORK_CHANGE";
    public static final String EVENT_UPDATE_ACCOUNT_INFO = "EVENT_UPDATE_ACCOUNT_INFO";
    public static final String EVENT_UPDATE_LIXI = "EVENT_UPDATE_LIXI";
    public static final String EVENT_SCAN_CONTACT_PAYTO = "EVENT_SCAN_CONTACT_PAYTO";
    public static final String SCAN_QR_TOPAY = "SCAN_QR_TOPAY";
    public static final String PAYTO_SUCCESS = "PAYTO_SUCCESS";

    public static final String EVENT_UPDATE_CASH_IN = "EVENT_UPDATE_CASH_IN";
    public static final String EVENT_CASH_IN_SUCCESS = "EVENT_CASH_IN_SUCCESS";
    public static final String EVENT_CASH_IN_PAYTO = "EVENT_CASH_IN_PAYTO";
    public static final String EVENT_CASH_IN_CHANGE = "EVENT_CASH_IN_CHANGE";
    public static final String EVENT_PAYMENT_SUCCESS = "EVENT_PAYMENT_SUCCESS";
    public static final String EVENT_SEND_REQUEST_PAYTO = "EVENT_SEND_REQUEST_PAYTO";
    public static final String EVENT_UPDATE_BALANCE = "EVENT_UPDATE_BALANCE";
    public static final String EVENT_CLOSE_SOCKET = "EVENT_CLOSE_SOCKET";
    public static final String EVENT_NEW_PAYMENT = "EVENT_NEW_PAYMENT";
    public static final String EVENT_UPDATE_MASTER_KEY_ERR = "EVENT_UPDATE_MASTER_KEY_ERR";
    //function
    public static final String FUNCTION_GET_WALLET_INFO = "GWEC0030";
    public static final String FUNCTION_LOGIN = "FU00004";
    public static final String FUNCTION_CHECK_ID_NUMBER = "FU100";
    public static final String FUNCTION_CHECK_USER_NAME = "FU00003";
    public static final String FUNCTION_ACTIVE_ACCOUNT = "FU0003";
    public static final String FUNCTION_REGISTER = "FU0001";
    public static final String FUNCTION_GET_EDONG_INFO = "FU0002";
    public static final String FUNCTION_WEB_SOCKET = "EC00027";
    public static final String FUNCTION_GET_PUBLIC_KEY_WALLET = "GWEC0007";
    public static final String FUNCTION_GET_PUBLIC_KEY_BY_PHONE = "GWEC0016";
    public static final String FUNCTION_GET_PUBLIC_KEY_CASH = "GWEC0005";
    public static final String FUNCTION_TRANSFER_EDONG_TO_ECASH = "GWEC0004";
    public static final String FUNCTION_TRANSFER_ECASH_TO_EDONG = "GWEC0009";
    public static final String FUNCTION_GET_PUBLIC_KEY_ORGANIZATION = "GWEC0006";
    public static final String FUNCTION_LOGOUT = "GW000001";
    public static final String FUNCTION_GET_OTP = "GW000004";
    public static final String FUNCTION_CHANGE_PASSWORD = "GW000003";
    public static final String FUNCTION_CANCEL_ACCOUNT = "GWEC0020";
    public static final String FUNCTION_CHANGE_CASH = "GWEC0011";
    public static final String FUNCTION_SYNC_CONTACT = "GWEC0017";
    public static final String FUNCTION_ADD_CONTACT = "GWEC0023";
    public static final String FUNCTION_DELETE_CONTACT = "GWEC0038";
    public static final String FUNCTION_FORGOT_PASS_OTP = "GWEC0024";
    public static final String FUNCTION_CHANGE_PASS = "GWEC0025";
    public static final String FUNCTION_GET_OTP_ACTIVE_ACCOUNT = "GWEC0029";
    public static final String FUNCTION_UPDATE_ACCOUNT_INFO = "GWEC0021";
    public static final String FUNCTION_UPDATE_AVARTAR = "GWEC0022";
    public static final String FUNCTION_GET_MONEY_VALUE = "GWEC0031";
    public static final String FUNCTION_UPDATE_MASTER_KEY = "GWEC0008";

    //channel
    public static final String CHANNEL_CODE = "MB001";
    public static final String TYPE_SEN_SOCKET = "CF";
    public static final String CREDIT_DEBIT_ACCOUNT = "1238161606";
    public static final String ISSUER_CODE = "ECPAY";

    //error code
    public static final String CODE_SUCCESS = "0000";

    public static final String TYPE_SEND_EDONG_TO_ECASH = "NC";
    public static final String TYPE_SEND_ECASH_TO_EDONG = "ND";
    public static final String TYPE_CASH_EXCHANGE = "DC";
    public static final String TYPE_ECASH_TO_ECASH = "CT";
    public static final String TYPE_LIXI = "LX";
    public static final String TYPE_SYNC_CONTACT = "DB";
    public static final String TYPE_CANCEL_CONTACT = "HV";
    public static final int TRANSACTION_SUCCESS = 0;
    public static final int TRANSACTION_FAIL = 1;

    public static final String TYPE_PAYTO = "TT";
    public static final String TYPE_TOPAY = "YT";
    public static final String QR_CODE_TOPAY_MODEL = "QR_CODE_TOPAY_MODEL";

    // todo Qr code
    public static final String QR_CONTACT = "MC";
    public static final String QR_TO_PAY = "TP";
    public static final long AMOUNT_LIMITED_MAX = 20000000;
    public static final long AMOUNT_LIMITED_MIN = 1000;

    public static final String SAVED_BUNDLE_TAG = "saved_bundle";
}
