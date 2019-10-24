package vn.ecpay.ewallet.common.utils;

public class Constant {
    public static final String DATABASE_NAME = "eWallet";
    public static final String CONTACT_TRANSFER = "CONTACT_TRANSFER";
    public static final String IS_SESSION_TIMEOUT = "IS_SESSION_TIMEOUT";
    //date
    public static String FORMAT_DATE_SEND_CASH = "yyyyMMddHHmmss";
    //type and key
    public static final String STR_CASH_IN = "in";
    public static final String STR_CASH_OUT = "out";
    public static final String MASTER_KEY_STRING = "0123456789ABCDEFGHIJKL0123456789";
    public static final String MASTER_KEY_STRING_NEW = "123456";
    public static final String STR_PRIVATE_KEY_CHANEL = "AIEGIA+wEeww8G/u3FF2/839hgTYb+8xD0gWo3X/Me/+";
    public static final String STR_PUBLIC_KEY_CHANEL = "BPKwnS/5ZO9EQTgIh4SsJfdDnjTaUltcatm6lMl8AH/n4K47E7cW5bIAmE5aI7C9KwmcUqsvccV9tUWY7wBuauM=";
    public static final String STR_EMPTY = "";
    public static final String DEVICE_IMEI = "DEVICE_IMEI";

    //key store
    public static final String INSTANCE_KS_ENCRYPT_PRIVATE = "INSTANCE_KS_ENCRYPT_PRIVATE";
    public static final String INSTANCE_KS_ENCRYPT_MASTER = "INSTANCE_KS_ENCRYPT_MASTER";

    public static final String MASTER_KEY = "MASTER_KEY";
    public static final String WALLET_ALIAS_PRIVATE_KEY = "WALLET_ALIAS_PRIVATE_KEY";
    public static final String WALLET_ALIAS_MASTER_KEY = "WALLET_ALIAS_PUB_KEY";

    //vent bus
    public static final String EVENT_ACTIVE_ACCOUNT = "EVENT_ACTIVE_ACCOUNT";
    public static final String UPDATE_ACCOUNT_LOGIN = "update_account_login";
    public static final String UPDATE_ACCOUNT_LOGOUT = "update_account_logout";
    public static final String UPDATE_MONEY = "update_money";
    public static final String UPDATE_MONEY_SOCKET = "update_money_socket";
    public static final String CASH_OUT_MONEY_SUCCESS = "cash_out_money_success";
    public static final String EVENT_CONNECT_SOCKET_FAIL = "EVENT_CONNECT_SOCKET_FAIL";
    public static final String EVENT_VERIFY_CASH_FAIL = "EVENT_VERIFY_CASH_FAIL";
    public static final String EVENT_QR_CODE_SCAN_CASH_SUCCESS = "EVENT_QR_CODE_SCAN_CASH_SUCCESS";

    //function
    public static final String FUNCTION_GET_WALLET_INFO = "FU00012";
    public static final String FUNCTION_LOGIN_AFTER_REGISTER = "FU00004";
    public static final String FUNCTION_CHECK_ID_NUMBER = "FU100";
    public static final String FUNCTION_CHECK_USER_NAME = "FU00003";
    public static final String FUNCTION_ACTIVE_ACCOUNT = "FU0003";
    public static final String FUNCTION_REGISTER = "FU0001";
    public static final String FUNCTION_GET_EDONG_INFO = "FU0002";
    public static final String FUNCTION_WEB_SOCKET = "EC00027";
    public static final String FUNCTION_GET_PUBLIC_KEY_WALLET = "GWEC0007";
    public static final String FUNCTION_GET_PUBLIC_KEY_CASH = "GWEC0005";
    public static final String FUNCTION_TRANSFER_EDONG_TO_ECASH = "GWEC0004";
    public static final String FUNCTION_TRANSFER_ECASH_TO_EDONG = "GWEC0009";
    public static final String FUNCTION_GET_PUBLIC_KEY_ORGANIZATION = "GWEC0006";
    public static final String FUNCTION_LOGOUT = "GW000001";
    public static final String FUNCTION_GET_OTP = "GW000004";
    public static final String FUNCTION_CHANGE_PASSWORD = "GW000003";
    public static final String FUNCTION_CANCEL_ACCOUNT = "GWEC0010";

    //channel
    public static final String CHANNEL_CODE = "MB001";
    public static final String TYPE_SEN_SOCKET = "CF";
    public static final String TYPE_SEND_ECASH_TO_EDONG = "ND";
    public static final String CREDIT_DEBIT_ACCOUNT = "1238161606";
    public static final String ISSUER_CODE = "ECPAY";

    //put money
    public static final String TOTAL_500 = "TOTAL_500";
    public static final String TOTAL_200 = "TOTAL_200";
    public static final String TOTAL_100 = "TOTAL_100";
    public static final String TOTAL_50 = "TOTAL_50";
    public static final String TOTAL_20 = "TOTAL_20";
    public static final String TOTAL_10 = "TOTAL_10";
    public static final String KEY_PUBLIC_RECEIVER = "key_public_receiver";
    public static final String WALLET_RECEIVER = "wallet_receiver";
    public static final String TYPE_SEND_MONEY = "CT";
    public static final String CONTENT_SEND_MONEY = "content_send_money";

    //cashin
    public static final String EDONG_TO_ECASH = "edong_to_ecash";
    public static final String TRANSACTION_SIGNATURE = "transactionSignature";
    public static final String ACCOUNT_INFO = "account_info";
    public static final String LIST_CASH_RESULT = "LIST_CASH_RESULT";

    //error code
    public static final String CODE_SUCCESS = "0000";

    //Qr Code
    public static final String IS_QR_CODE  = "IS_QR_CODE";
    public static final String RESPONSE_CASH_MESS = "ResponseCashMess";

}
