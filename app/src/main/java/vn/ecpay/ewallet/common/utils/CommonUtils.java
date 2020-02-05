package vn.ecpay.ewallet.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.base.CircleImageView;
import vn.ecpay.ewallet.common.eccrypto.ECElGamal;
import vn.ecpay.ewallet.common.eccrypto.ECashCrypto;
import vn.ecpay.ewallet.common.eccrypto.EllipticCurve;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eccrypto.Test;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.BaseObject;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.QRCode.QRScanBase;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseDataGetWalletByPhone;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.webSocket.object.ResponseMessSocket;

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
        return allItem.toString().replaceAll("null", "");
    }

    public static String getAuditNumber() {
        Random random = new Random();
        return String.valueOf((long) (100000000000000L + random.nextFloat() * 900000000000000L));
    }

    public static String getKeyAlias() {
        String ret = String.valueOf(System.currentTimeMillis());
        ret = ret.substring(ret.length() - 9, ret.length() - 3);
        return "KP" + ret;
    }

    public static String generateSignature(byte[] strDataSign) {
        String key = getPrivateChannelKey();
        byte[] privateKey = Base64.decode(getPrivateChannelKey(), Base64.DEFAULT);

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
        EllipticCurve ec = EllipticCurve.getSecp256k1();
        if (CommonUtils.getPublicServerKey().isEmpty()) {
            return Constant.STR_EMPTY;
        }
        ECPublicKeyParameters kp = EllipticCurve.getSecp256k1().getPublicKeyParameters(Base64.decode(CommonUtils.getPublicServerKey(), Base64.DEFAULT));
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

    public static String getToken() {
        if (null != ECashApplication.getAccountInfo()) {
            if (ECashApplication.getAccountInfo().getPassword() == null || ECashApplication.getAccountInfo().getPassword().isEmpty()) {
                return ECashApplication.getAccountInfo().getToken();
            } else {
                return ECashApplication.getAccountInfo().getPassword();
            }
        } else {
            return Constant.STR_EMPTY;
        }
    }

    public static String getToken(AccountInfo accountInfo) {
        if (accountInfo.getToken() == null || accountInfo.getToken().isEmpty()) {
            return accountInfo.getPassword();
        } else {
            return accountInfo.getToken();
        }
    }

    public static String getFullName(AccountInfo accountInfo) {
        try {
            return accountInfo.getPersonFirstName() + " " + accountInfo.getPersonMiddleName() + " " + accountInfo.getPersonLastName();
        } catch (NullPointerException e) {
            return Constant.STR_EMPTY;
        }
    }

    public static String getFullName(ResponseDataGetWalletByPhone responseDataGetWalletByPhone) {
        try {
            return responseDataGetWalletByPhone.getPersonFirstName() + " " + responseDataGetWalletByPhone.getPersonMiddleName() + " " + responseDataGetWalletByPhone.getPersonLastName();
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

    public static byte[] getDataSign(boolean flag, CashLogs_Database cash) {
        return SHA256.hashSHA256(cash.getCountryCode() + ";" + cash.getIssuerCode() + ";" + cash.getSerialNo() + ";"
                + cash.getDecisionNo() + ";" + cash.getParValue() + ";" + cash.getActiveDate() + ";"
                + cash.getExpireDate() + (flag ? "" : ";" + cash.getCycle()));
    }

    public static boolean verifyCash(CashLogs_Database cash, String decisionTrekp, String decisionAcckp) {
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
    public static String getCurrentTime(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c.getTime());
    }
    public static String getCurrentTimeNotification() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constant.FORMAT_DATE_NOTIFICATION);
        return df.format(c.getTime());
    }

    //validate data input
    public static boolean isValidateUserName(String code) {
        String regex = "[A-Za-z0-9]{0,100}";
        return code.matches(regex);
    }

    public static boolean isValidateEmail(String email) {
        String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(regex);
    }

    public static boolean isValidatePass(String pass) {
        String regex = "[a-zA-Z0-9!@#$%^&*()~`=_+|:\";',./<>?]{6,20}";
        return pass.matches(regex);
    }


    public static boolean isValidateNumber(String code) {
        String regex = "[0-9]{10}";
        return code.matches(regex);
    }

    public static boolean isValidatePhoneNumber(String number) {
        if (number == null)
            return false;
        if (number.trim().length() != 10) {
            return false;
        }
        String regex = "((09|03|07|08|05)+([0-9]{8})\\b)";
        return number.matches(regex);
    }

    public static boolean validatePassPort(String idNumber) {
        int lenght = idNumber.length();
        if (idNumber.isEmpty()) {
            return false;
        }
        if (lenght == 12 || lenght == 9) {
            return true;
        }
        return false;
    }

    public static boolean isValidateName(String name) {
        name = name.trim();
        if (name.isEmpty())
            return false;
        String regex = "[A-Za-z0-9aáàạảâẫấầậẩăằắặẳôốồộổỗơớờởợưứữừựửđóòỏọuúùụủeééẹẻêễễếềệểuúũùụủíìịĩỉýỳỵỹỷAÁÀẠÃẢÂẤẦẬẨĂẰẮẶẲÔỐỒỖỘỔƠỚỜỠỞỢƯỨIỪỰỬĐÓÒỎỌUÚÙỤỦEÉÉẸẺÊẾỀỆỂUÚÙỤỦÍÌỊỈÝỲỴỶ\\s]{0,100}";
        return name.matches(regex);
    }

    public static String getAppenItemCash(CashLogs_Database cash) {
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

    public static String getIdSender(ResponseMessSocket responseMess, Context context) {
        byte[] dataSign = SHA256.hashSHA256(getSignBodySender(responseMess));
        return CommonUtils.generateSignature(dataSign, KeyStoreUtils.getPrivateKey(context));
    }

    public static String getSignBodySender(ResponseMessSocket responseMess) {
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

    public static boolean verifyData(ResponseMessSocket responseMess, String publicKey) {
        byte[] mDataSign = SHA256.hashSHA256(CommonUtils.getSignBodySender(responseMess));
        return EllipticCurve.verify(mDataSign,
                Base64.decode(responseMess.getId(), Base64.DEFAULT),
                Base64.decode(publicKey, Base64.DEFAULT));

    }

    public static QRCashTransfer getQrCashTransfer(CashLogs_Database cash, ResponseMessSocket responseMess, ResponseDataGetPublicKeyWallet responseGetPublicKeyWallet, boolean status) {
        QRCashTransfer qrCashTransfer = new QRCashTransfer();
        qrCashTransfer.setParValue(cash.getParValue());
        qrCashTransfer.setSuccess(status);
        qrCashTransfer.setName(CommonUtils.getFullName(responseGetPublicKeyWallet));
        qrCashTransfer.setPhone(responseGetPublicKeyWallet.getPersonMobilePhone());
        qrCashTransfer.setContent(responseMess.getContent());
        return qrCashTransfer;
    }

    public static String getDateTransfer(Context context, String date) {
        if (date.length() > 8) {
            String strYear = date.substring(0, 4);
            String strMonth = date.substring(4, 6);
            String strDay = date.substring(6, 8);
            return context.getString(R.string.str_date, strDay, strMonth, strYear);
        } else {
            return Constant.STR_EMPTY;
        }
    }

    public static List<String> getListPhoneNumber(Context context) {
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        List<String> userList = new ArrayList<>();
        assert phones != null;
        if (phones.getCount() > 0) {
            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replace("+84", "0")
                        .replace(" ", "");
                if (CommonUtils.isValidatePhoneNumber(phoneNumber)) {
                    userList.add(phoneNumber);
                }
            }
        }
        phones.close();
        return userList;
    }

    public static int getTypeScan(QRScanBase qrScanBase) {
        if (null != qrScanBase.getContent()) {
            return Constant.IS_SCAN_CASH;
        } else if (null != qrScanBase.getWalletId() && null != qrScanBase.getPublicKey()) {
            return Constant.IS_SCAN_CONTACT;
        } else {
            return Constant.IS_SCAN_FAIL;
        }
    }

    public static Long getMoneyEdong(Long money) {
        if (money > 0) {
            return money;
        } else {
            return 0L;
        }
    }

    public static Bitmap generateQRCode(String value) {
        int WIDTH = 400;
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = writer.encode(value, BarcodeFormat.QR_CODE, WIDTH, WIDTH);
            Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < 400; i++) {
                for (int j = 0; j < 400; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK
                            : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static String getPublicServerKey() {
        Log.e("channelKp", SharedPrefs.getInstance().get(SharedPrefs.channelKp, String.class));
        return SharedPrefs.getInstance().get(SharedPrefs.channelKp, String.class);
    }

    static String getPrivateChannelKey() {
        Log.e("clientKs", SharedPrefs.getInstance().get(SharedPrefs.clientKs, String.class));
        return SharedPrefs.getInstance().get(SharedPrefs.clientKs, String.class);
    }

    public static void logJson(BaseObject baseObject) {
        Gson gson = new Gson();
        String json = gson.toJson(baseObject);
        Log.e("json", json);
    }

    public static String getIMEI(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString(Constant.DEVICE_IMEI, null);
    }

    public static String validateLeghtFileImage(File file) {
        if (file != null) {
            double bytes = file.length();
            double kilobytes = (bytes / 1024);
            double megabytes = (kilobytes / 1024);
            if (megabytes > 4) {
                return "Kích thước ảnh hiển thị không được vượt quá 4MB";
            }
        }
        return "";
    }

    public static void loadAvatar(Context mContext, CircleImageView avatar, String image) {
        if (mContext != null) {
            Glide.get(mContext).clearMemory();
            RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(150, 150)
                    //.transform(new MyTransformation(mContext))
                    .error(R.drawable.ic_avatar);
            Glide.with(mContext).load("data:image/png;base64," + image).apply(requestOptions).into(avatar);
        }
    }

    public static Long getTotalMoney(List<CashTotal> valuesList) {
        if (valuesList != null) {
            long totalMoney = 0;
            for (int i = 0; i < valuesList.size(); i++) {
                totalMoney = totalMoney + (valuesList.get(i).getTotal() * valuesList.get(i).getParValue());
            }
            return totalMoney;
        }
        return 0L;
    }

    public static Long getTotalMoneyByCashLog(List<CashLogTransaction> listCashLogTransaction) {
        if (listCashLogTransaction != null) {
            long totalMoney = 0;
            for (int i = 0; i < listCashLogTransaction.size(); i++) {
                totalMoney = totalMoney + (listCashLogTransaction.get(i).getParValue() * listCashLogTransaction.get(i).getValidCount());
            }
            return totalMoney;
        }
        return 0L;
    }

    public static List<CashTotal> getListCashConfirm(List<CashTotal> valuesList) {
        List<CashTotal> cashTotals = new ArrayList<>();
        for (int i = 0; i < valuesList.size(); i++) {
            if (valuesList.get(i).getTotal() > 0) {
                cashTotals.add(valuesList.get(i));
            }
        }
        return cashTotals;
    }

    public static String getNameHeader(Contact contact) {
        try {
            return String.valueOf(contact.getFullName().charAt(0));
        } catch (StringIndexOutOfBoundsException e) {
            return String.valueOf(contact.getPhone().charAt(0));
        }
    }

    public static Uri getBitmapUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,context.getString(R.string.app_name) , null);
        return Uri.parse(path);
    }

    public static boolean isAccountExit(Context context) {
        if (KeyStoreUtils.getMasterKey(context) != null) {
            List<AccountInfo> listAccount = DatabaseUtil.getAllAccountInfo(context);
            if (listAccount != null) {
                return listAccount.size() <= 0;
            } else {
                return true;
            }
        }
        return true;
    }
}