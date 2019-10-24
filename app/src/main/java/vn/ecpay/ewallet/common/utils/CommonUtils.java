package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import vn.ecpay.ewallet.common.eccrypto.ECElGamal;
import vn.ecpay.ewallet.common.eccrypto.ECashCrypto;
import vn.ecpay.ewallet.common.eccrypto.EllipticCurve;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eccrypto.Test;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.database.table.Cash;
import vn.ecpay.ewallet.database.table.TransactionLog;
import vn.ecpay.ewallet.model.BaseObject;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cash.getPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.webSocket.object.ResponseCashMess;

public class CommonUtils {
    public static String getModelName() {
        return Build.MODEL;
    }

    static final byte elementSplit = '$';

    private static int checkSelfPermission(String readPhoneState) {
        return 0;
    }

    private void putToPreference() {
    }

    public static String getStringAlphabe(BaseObject baseObject) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : baseObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(baseObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, Object> treeMap = new TreeMap<>(map);
        Set s = treeMap.entrySet();
        Iterator it = s.iterator();
        StringBuilder allItem = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            allItem.append(String.valueOf(entry.getValue()));
        }
        Gson gson = new Gson();
        Log.e("json", gson.toJson(baseObject));
        return allItem.toString().replaceAll("null", "");
    }

    public static String getAuditNumber() {
        Log.i("time current", " " + System.currentTimeMillis());
        String ret = String.valueOf(System.currentTimeMillis());
        ret = ret.substring(ret.length() - 9, ret.length() - 3);
        if (ret.startsWith("0")) {
            ret = "1" + ret.substring(1);
        }
        Log.i("audit number", ret);
        return ret;
    }

    public static String getKeyAlias() {
        String ret = String.valueOf(System.currentTimeMillis());
        ret = ret.substring(ret.length() - 9, ret.length() - 3);
        return "KP" + ret;
    }

    public static String getErrocodeRegisterAccount(String code) {
        switch (code) {
            case "1000":
                return "Tạo khách hàng không thành công";
            case "1001":
                return "Loại khách hàng không hợp lệ";
            case "1002":
                return "Loại giấy tờ tùy thân không hợp lệ";
            case "1004":
                return "Khách hàng không tồn tại trên hệ thống";
            case "1005":
                return "Mã khách hàng không đúng";
            case "1006":
                return "Mã khách hàng đã tồn tại trên hệ thống";
            case "1007":
                return "Số điện thoại không hợp lệ";
            case "1008":
                return "Ngày cấp giấy tờ tùy thân không hợp lệ";
            default:
                return "Đã có lỗi của server xảy ra.Xin vui lòng thử lại";
        }
    }

    public static String generateSignature(byte[] strDataSign) {
        byte[] privateKey = Base64.decode(Constant.STR_PRIVATE_KEY_CHANEL, Base64.DEFAULT);

        EllipticCurve ec = EllipticCurve.getSecp256k1();
        ECPrivateKeyParameters privateKeyParameters = ec.generatePrivateKeyParameters(
                new BigInteger(privateKey)
        );

        byte[] signature = ec.sign(privateKeyParameters, strDataSign);
        return Base64.encodeToString(signature, Base64.DEFAULT).replace("\n", "");
    }

    public static String generateSignature(byte[] strDataSign, String key) {
        byte[] privateKey = Base64.decode(key, Base64.DEFAULT);

        EllipticCurve ec = EllipticCurve.getSecp256k1();
        ECPrivateKeyParameters privateKeyParameters = ec.generatePrivateKeyParameters(
                new BigInteger(privateKey)
        );

        byte[] signature = ec.sign(privateKeyParameters, strDataSign);
        return Base64.encodeToString(signature, Base64.DEFAULT).replace("\n", "");
    }

    public static String encryptPassword(String pass) {
        byte[] privateKey = Base64.decode(Constant.STR_PRIVATE_KEY_CHANEL, Base64.DEFAULT);
        EllipticCurve ec = EllipticCurve.getSecp256k1();
        ECPrivateKeyParameters keyPrivate = ec.generatePrivateKeyParameters(
                new BigInteger(privateKey)
        );
        ECPublicKeyParameters kp = ec.getPublicKeyParameters(keyPrivate);

        byte[][] blockEncrypted = ECElGamal.encrypt(ec, kp, pass.getBytes());
        String passEncode = Base64.encodeToString(blockEncrypted[0], Base64.DEFAULT) + "$"
                + Base64.encodeToString(blockEncrypted[1], Base64.DEFAULT) + "$"
                + Base64.encodeToString(blockEncrypted[2], Base64.DEFAULT);
        return passEncode.replaceAll("\n", "");
    }

    public static String[][] decrypEcash(String eCash, String privateKeyWallet) {
        byte[] privateKey = Base64.decode(privateKeyWallet, Base64.DEFAULT);
        EllipticCurve ec = EllipticCurve.getSecp256k1();
        ECPrivateKeyParameters keyPrivate = ec.generatePrivateKeyParameters(
                new BigInteger(privateKey)
        );

        String[] listEncData = eCash.split("\\$");
        byte[] zero = Base64.decode(listEncData[0], Base64.DEFAULT);
        byte[] one = Base64.decode(listEncData[1], Base64.DEFAULT);
        byte[] tow = Base64.decode(listEncData[2], Base64.DEFAULT);

        byte[][] result = {zero, one, tow};
        try {
            return ECashCrypto.decryptV2(ec, keyPrivate, result);
        } catch (IOException e) {
            return null;
        }
    }


    public static String endCrypEcash(String[][] cashArray, String publicKey) {
        byte elementSplit = '$';
        byte[][] blockEnc;
        try {
            blockEnc = ECashCrypto.encryptV2(Test.ec, Test.kp, cashArray);
        } catch (IOException e) {
            return null;
        }

        return (Base64.encodeToString(blockEnc[0], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[1], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[2], Base64.DEFAULT)).replaceAll("\n", "");
    }

    public static String getToken(AccountInfo accountInfo) {
        if (accountInfo.getPassword() == null || accountInfo.getPassword().isEmpty()) {
            return accountInfo.getToken();
        } else {
            return accountInfo.getPassword();
        }
    }

    public static String getFullName(AccountInfo accountInfo) {
        try {
            return accountInfo.getPersonFirstName() + " " + accountInfo.getPersonMiddleName() + " " + accountInfo.getPersonLastName();
        } catch (NullPointerException e) {
            return Constant.STR_EMPTY;
        }
    }

    public static String getFullName(ResponseDataGetPublicKeyWallet responseDataGetPublicKeyWallet) {
        try {
            return responseDataGetPublicKeyWallet.getPersonFirstName() + " " + responseDataGetPublicKeyWallet.getPersonMiddleName() + " " + responseDataGetPublicKeyWallet.getPersonLastName();
        } catch (NullPointerException e) {
            return Constant.STR_EMPTY;
        }
    }

    public static byte[] getDataSign(boolean flag, Cash cash) {
        return SHA256.hashSHA256(cash.getCountryCode() + ";" + cash.getIssuerCode() + ";" + cash.getSerialNo() + ";"
                + cash.getDecisionNo() + ";" + cash.getParValue() + ";" + cash.getActiveDate() + ";"
                + cash.getExpireDate() + (flag ? "" : ";" + cash.getCycle()));
    }

    public static String getSignTransactionLog(TransactionLog transactionLog) {
        String cashSign = transactionLog.getId() + transactionLog.getSenderAccountId()
                + transactionLog.getReceiverAccountId() + transactionLog.getType()
                + transactionLog.getTime() + transactionLog.getContent()
                + transactionLog.getCashEnc() + transactionLog.getTransactionSignature();
        byte[] dataSign = SHA256.hashSHA256(cashSign);
        return CommonUtils.generateSignature(dataSign, Constant.STR_PRIVATE_KEY_CHANEL);
    }

    public static boolean verifyCash(Cash cash, String decisionTrekp, String decisionAcckp) {
        boolean treResult = true;
        boolean accResult = true;

        if (!cash.getTreSign().isEmpty()) {
            treResult = EllipticCurve.verify(getDataSign(false, cash),
                    Base64.decode(cash.getTreSign(), Base64.DEFAULT),
                    Base64.decode(decisionTrekp, Base64.DEFAULT));
            Log.e("treResult", "treResult");
        }
        if (!cash.getAccSign().isEmpty()) {
            accResult = EllipticCurve.verify(getDataSign(true, cash),
                    Base64.decode(cash.getAccSign(), Base64.DEFAULT),
                    Base64.decode(decisionAcckp, Base64.DEFAULT));
            Log.e("accResult", "accResult");
        }
        return treResult && accResult;
    }

    public static String formatPriceVND(long money) {
        DecimalFormat fmt = new DecimalFormat();
        DecimalFormatSymbols fmts = new DecimalFormatSymbols();

        fmts.setGroupingSeparator('.');

        fmt.setGroupingSize(3);
        fmt.setGroupingUsed(true);
        fmt.setDecimalFormatSymbols(fmts);
        return fmt.format(money) + " VNĐ";
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constant.FORMAT_DATE_SEND_CASH);
        return df.format(c.getTime());
    }

    //validate data input
    public static boolean isValidateUserName(String code) {
        String regex = "[A-Za-z0-9]{0,100}";
        return code.matches(regex);
    }

    public static boolean isValidatePass(String pass) {
        if (pass == null)
            return false;
        if (pass.trim().length() < 6 || pass.trim().length() >20) {
            return false;
        }
        return true;
    }

    public static boolean isValidatePhoneNumberNew(String number) {
        if (number == null)
            return false;
        if (number.trim().length() != 10) {
            return false;
        }
        return number.substring(0, 1).equals("0");
    }

    public static boolean validatePassPort(String idNumber) {
        int lenght = idNumber.length();
        return !(idNumber.isEmpty() || lenght > 12 || lenght < 9);
    }

    public static boolean isValidateName(String name) {
        name = name.trim();
        if (name.isEmpty())
            return false;
        String regex = "[A-Za-zaáàạảâẫấầậẩăằắặẳôốồộổỗơớờởợưứữừựửđóòỏọuúùụủeééẹẻêễễếềệểuúũùụủíìịĩỉýỳỵỹỷAÁÀẠÃẢÂẤẦẬẨĂẰẮẶẲÔỐỒỖỘỔƠỚỜỠỞỢƯỨIỪỰỬĐÓÒỎỌUÚÙỤỦEÉÉẸẺÊẾỀỆỂUÚÙỤỦÍÌỊỈÝỲỴỶ\\s]{0,100}";
        return name.matches(regex);
    }

    public static String getAppenItemCash(Cash cash) {
        return (cash.getCountryCode() + ";" + cash.getIssuerCode() + ";" + cash.getDecisionNo() + ";"
                + cash.getSerialNo() + ";" + cash.getParValue() + ";" + cash.getActiveDate() + ";"
                + cash.getExpireDate() + ";" + cash.getCycle());
    }

    public static String getEncrypData(String[][] cashArray, String publicKyReceiver) {
        byte[][] blockEnc;
        try {
            EllipticCurve ec = EllipticCurve.getSecp256k1();
            byte[] keyPublic = Base64.decode(publicKyReceiver, Base64.DEFAULT);
            ECPublicKeyParameters publicKeyParameters = ec.getPublicKeyParameters(keyPublic);
            blockEnc = ECashCrypto.encryptV2(ec, publicKeyParameters, cashArray);
        } catch (IOException e) {
            e.printStackTrace();
            return Constant.STR_EMPTY;
        }

        String encData = (Base64.encodeToString(blockEnc[0], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[1], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[2], Base64.DEFAULT)).replaceAll("\n", "");
        return encData;
    }

    public static String getIdSender(ResponseCashMess responseMess, Context context) {
        byte[] dataSign = SHA256.hashSHA256(getSignBodySender(responseMess));
        return CommonUtils.generateSignature(dataSign, KeyStoreUtils.getPrivateKey(context));
    }

    public static String getSignBodySender(ResponseCashMess responseMess) {
        return responseMess.getSender() + responseMess.getReceiver() + responseMess.getTime() + responseMess.getType()
                + responseMess.getContent() + responseMess.getCashEnc();
    }

    public static List<String> getSplittedString(String stringtoSplit,
                                                 int length) {

        List<String> returnStringList = new ArrayList<String>(
                (stringtoSplit.length() + length - 1) / length);

        for (int start = 0; start < stringtoSplit.length(); start += length) {
            returnStringList.add(stringtoSplit.substring(start,
                    Math.min(stringtoSplit.length(), start + length)));
        }

        return returnStringList;
    }

    public static String getParamFilter(String filter) {
        return "%" + filter + "%";
    }

    public static boolean verifyData(ResponseCashMess responseMess, String publicKey) {
        byte[] mDataSign = SHA256.hashSHA256(CommonUtils.getSignBodySender(responseMess));
        return EllipticCurve.verify(mDataSign,
                Base64.decode(responseMess.getId(), Base64.DEFAULT),
                Base64.decode(publicKey, Base64.DEFAULT));

    }

    public static QRCashTransfer getQrCashTransfer(Cash cash, ResponseCashMess responseMess, ResponseDataGetPublicKeyWallet responseGetPublicKeyWallet, boolean status){
        QRCashTransfer qrCashTransfer = new QRCashTransfer();
        qrCashTransfer.setParValue(cash.getParValue());
        qrCashTransfer.setSuccess(status);
        qrCashTransfer.setName(CommonUtils.getFullName(responseGetPublicKeyWallet));
        qrCashTransfer.setPhone(responseGetPublicKeyWallet.getPersonMobilePhone());
        qrCashTransfer.setContent(responseMess.getContent());
        return qrCashTransfer;
    }

}
