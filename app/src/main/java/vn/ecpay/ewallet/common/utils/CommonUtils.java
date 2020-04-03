package vn.ecpay.ewallet.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
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
import vn.ecpay.ewallet.common.base.ECashBaseActivity;
import vn.ecpay.ewallet.common.base.ECashBaseFragment;
import vn.ecpay.ewallet.common.eccrypto.ECElGamal;
import vn.ecpay.ewallet.common.eccrypto.ECashCrypto;
import vn.ecpay.ewallet.common.eccrypto.EllipticCurve;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.eccrypto.Test;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.database.WalletDatabase;
import vn.ecpay.ewallet.database.table.CashLogs_Database;
import vn.ecpay.ewallet.model.BaseObject;
import vn.ecpay.ewallet.model.QRCode.QRCashTransfer;
import vn.ecpay.ewallet.model.QRCode.QRCodeSender;
import vn.ecpay.ewallet.model.account.login.responseLoginAfterRegister.EdongInfo;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.model.cashValue.CashTotal;
import vn.ecpay.ewallet.model.contactTransfer.Contact;
import vn.ecpay.ewallet.model.contactTransfer.ContactTransfer;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyByPhone.ResponseDataGetWalletByPhone;
import vn.ecpay.ewallet.model.getPublicKeyWallet.responseGetPublicKeyWallet.ResponseDataGetPublicKeyWallet;
import vn.ecpay.ewallet.model.payment.CashValid;
import vn.ecpay.ewallet.model.transactionsHistory.CashLogTransaction;
import vn.ecpay.ewallet.ui.account.AccountActivity;
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
            allItem.append(entry.getValue());
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

    public static String formatPrice(long money) {
        DecimalFormat fmt = new DecimalFormat();
        DecimalFormatSymbols fmts = new DecimalFormatSymbols();

        fmts.setGroupingSeparator('.');

        fmt.setGroupingSize(3);
        fmt.setGroupingUsed(true);
        fmt.setDecimalFormatSymbols(fmts);
        return fmt.format(money);
    }

    public static String formatPriceVND(Double money) {
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
        String regex = "[A-Za-z0-9]{3,100}";
        return code.matches(regex);
    }

    public static boolean isValidateEmail(String email) {
        String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,64})$";
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
        return lenght == 12 || lenght == 9;
    }

    public static boolean isValidateName(String name) {
        name = name.trim();
        if (name.isEmpty())
            return false;
        String regex = "[A-Za-z0-9aáàạảâẫấầậẩăằắặẳôốồộổỗơớờởợưứữừựửđóòỏọuúùụủeééẹẻêễễếềệểuúũùụủíìịĩỉýỳỵỹỷAÁÀẠÃẢÂẤẦẬẨĂẰẮẶẲÔỐỒỖỘỔƠỚỜỠỞỢƯỨIỪỰỬĐÓÒỎỌUÚÙỤỦEÉÉẸẺÊẾỀỆỂUÚÙỤỦÍÌỊỈÝỲỴỶ\\s]{2,100}";
        return name.matches(regex);
    }

    public static boolean isValidateNameContact(String name) {
        name = name.trim();
        if (name.isEmpty())
            return false;
        String regex = "[A-Za-z0-9aáàạảâẫấầậẩăằắặẳôốồộổỗơớờởợưứữừựửđóòỏọuúùụủeééẹẻêễễếềệểuúũùụủíìịĩỉýỳỵỹỷAÁÀẠÃẢÂẤẦẬẨĂẰẮẶẲÔỐỒỖỘỔƠỚỜỠỞỢƯỨIỪỰỬĐÓÒỎỌUÚÙỤỦEÉÉẸẺÊẾỀỆỂUÚÙỤỦÍÌỊỈÝỲỴỶ!@#$%^&*?<>~\\s]{2,64}";
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
            String time = date.substring(9);
            return context.getString(R.string.str_date, strDay, strMonth, strYear, time);
        } else {
            return Constant.STR_EMPTY;
        }
    }

    public static List<String> getListPhoneNumber(Context context) {
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP};
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        List<String> userList = new ArrayList<>();
        List<Long> dateList = new ArrayList<>();
        Long maxDateSharedPrefs = SharedPrefs.getInstance().get(SharedPrefs.contactMaxDate, Long.class);
        if (maxDateSharedPrefs > 0 && null != phones) {
            if (phones.getCount() > 0) {
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            .replace("+84", "0")
                            .replace(" ", "");
                    Long date = Long.valueOf(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP)));
                    dateList.add(date);
                    if (CommonUtils.isValidatePhoneNumber(phoneNumber) && date > maxDateSharedPrefs) {
                        userList.add(phoneNumber);
                    }
                }
            }

        } else {
            assert phones != null;
            if (phones.getCount() > 0) {
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            .replace("+84", "0")
                            .replace(" ", "");
                    Long date = Long.valueOf(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP)));
                    dateList.add(date);
                    if (CommonUtils.isValidatePhoneNumber(phoneNumber)) {
                        userList.add(phoneNumber);
                    }
                }
            }
        }
        if (dateList.size() > 0) {
            ECashApplication.lastTimeAddContact = Collections.max(dateList);
        }
        phones.close();
        return userList;
    }

    public static Long getMoneyEDong(EdongInfo edongInfo) {
        return (edongInfo.getAccBalance() - edongInfo.getAccLock());
    }

    public static Bitmap generateQRCode(String value) {
        int WIDTH = 400;
        Writer writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            bitMatrix = writer.encode(value, BarcodeFormat.QR_CODE, WIDTH, WIDTH, hints);
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
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static String getPublicServerKey() {
        return SharedPrefs.getInstance().get(SharedPrefs.channelKp, String.class);
    }

    static String getPrivateChannelKey() {
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
//        return "287496598275927459872";
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
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, context.getString(R.string.app_name), null);
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

    public static ResponseMessSocket getObjectJsonSendCashToCash(Context context, List<CashTotal> valuesListAdapter,
                                                                 Contact contact, String contentSendMoney, int index, String typeSend, AccountInfo accountInfo) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        for (int i = 0; i < valuesListAdapter.size(); i++) {
            if (valuesListAdapter.get(i).getTotal() > 0) {
                List<CashLogs_Database> cashList = DatabaseUtil.getListCashForMoney(context, String.valueOf(valuesListAdapter.get(i).getParValue()));
                int totalCashSend = valuesListAdapter.get(i).getTotal();
                for (int j = 0; j < valuesListAdapter.get(i).getTotal(); j++) {
                    if (index > 0) {
                        int location = j + index * totalCashSend;
                        listCashSend.add(cashList.get(location));
                    } else {
                        listCashSend.add(cashList.get(j));
                    }
                }
            }
        }

        if (listCashSend.size() > 0) {
            String[][] cashArray = new String[listCashSend.size()][3];
            for (int i = 0; i < listCashSend.size(); i++) {
                CashLogs_Database cash = listCashSend.get(i);
                String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                cashArray[i] = moneyItem;
            }
            String encData = CommonUtils.getEncrypData(cashArray, contact.getPublicKeyValue());
            ResponseMessSocket responseMess = new ResponseMessSocket();
            responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
            responseMess.setReceiver(String.valueOf(contact.getWalletId()));
            responseMess.setTime(CommonUtils.getCurrentTime());
            responseMess.setType(typeSend);
            responseMess.setContent(contentSendMoney);
            responseMess.setCashEnc(encData);
            responseMess.setId(CommonUtils.getIdSender(responseMess, context));

            CommonUtils.logJson(responseMess);
            DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, context, accountInfo.getUsername());
            return responseMess;
        }
        return null;
    }

    public static ResponseMessSocket getObjectJsonSendCashToCash(Context context, List<CashTotal> valuesListAdapter,
                                                                 ContactTransfer contact, String contentSendMoney, int index, String typeSend, AccountInfo accountInfo) {
        WalletDatabase.getINSTANCE(context, KeyStoreUtils.getMasterKey(context));
        ArrayList<CashLogs_Database> listCashSend = new ArrayList<>();

        try {
            for (int i = 0; i < valuesListAdapter.size(); i++) {
                if (valuesListAdapter.get(i).getTotal() > 0) {
                    List<CashLogs_Database> cashList = DatabaseUtil.getListCashForMoney(context, String.valueOf(valuesListAdapter.get(i).getParValue()));
                    int totalCashSend = valuesListAdapter.get(i).getTotal();
                    for (int j = 0; j < valuesListAdapter.get(i).getTotal(); j++) {
                        if (index > 0) {
                            int location = j + index * totalCashSend;
                            listCashSend.add(cashList.get(location));
                        } else {
                            listCashSend.add(cashList.get(j));
                        }
                    }
                }
            }

            if (listCashSend.size() > 0) {
                String[][] cashArray = new String[listCashSend.size()][3];
                for (int i = 0; i < listCashSend.size(); i++) {
                    CashLogs_Database cash = listCashSend.get(i);
                    String[] moneyItem = {CommonUtils.getAppenItemCash(cash), cash.getAccSign(), cash.getTreSign()};
                    cashArray[i] = moneyItem;
                }
                String encData = CommonUtils.getEncrypData(cashArray, contact.getPublicKeyValue());
                ResponseMessSocket responseMess = new ResponseMessSocket();
                responseMess.setSender(String.valueOf(accountInfo.getWalletId()));
                responseMess.setReceiver(String.valueOf(contact.getWalletId()));
                responseMess.setTime(CommonUtils.getCurrentTime());
                responseMess.setType(typeSend);
                responseMess.setContent(contentSendMoney);
                responseMess.setCashEnc(encData);
                responseMess.setId(CommonUtils.getIdSender(responseMess, context));

                CommonUtils.logJson(responseMess);
                DatabaseUtil.updateTransactionsLogAndCashOutDatabase(listCashSend, responseMess, context, accountInfo.getUsername());
                return responseMess;
            }
        } catch (Exception e) {
            Log.e("Exception ", e.getMessage());
        }

        return null;
    }

    public static ArrayList<Contact> getListTransfer(List<Contact> mSectionList) {
        ArrayList<Contact> multiTransferList = new ArrayList<>();
        for (Contact contact : mSectionList) {
            if (contact.isAddTransfer) {
                multiTransferList.add(contact);
            }
        }
        return multiTransferList;
    }

    public static int getCountTransfer(List<Contact> mSectionList) {
        int count = 0;
        for (Contact contact : mSectionList) {
            if (contact.isAddTransfer) {
                count++;
            }
        }
        return count;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ECashApplication.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static AccountInfo getAccountByUserName(Context context) {
        String username = ECashApplication.getAccountInfo().getUsername();
        return DatabaseUtil.getAccountInfo(username, context);
    }

    public static boolean checkWalletIDisMe(Context context, String walletID) {
        String userName = ECashApplication.getAccountInfo().getUsername();
        AccountInfo accountInfo = DatabaseUtil.getAccountInfo(userName, context);
        if (accountInfo != null && walletID != null) {
            return walletID.equals(String.valueOf(accountInfo.getWalletId()));
        }
        return false;
    }

    public static ArrayList<Uri> genericListUri(Context context, List<Contact> multiTransferList, List<CashTotal> valuesListAdapter, String content, String type) {
        ArrayList<Uri> listUri = new ArrayList<>();
        try {
            String userName = ECashApplication.getAccountInfo().getUsername();
            AccountInfo accountInfo = DatabaseUtil.getAccountInfo(userName, context);
            for (int i = 0; i < multiTransferList.size(); i++) {
                Gson gson = new Gson();
                ResponseMessSocket responseMessSocket = CommonUtils.getObjectJsonSendCashToCash(context, valuesListAdapter,
                        multiTransferList.get(i), content, i, type, accountInfo);
                String jsonCash = gson.toJson(responseMessSocket);
                List<String> stringList = CommonUtils.getSplittedString(jsonCash, 1000);
                ArrayList<QRCodeSender> codeSenderArrayList = new ArrayList<>();
                if (stringList.size() > 0) {
                    for (int j = 0; j < stringList.size(); j++) {
                        QRCodeSender qrCodeSender = new QRCodeSender();
                        qrCodeSender.setCycle(j + 1);
                        qrCodeSender.setTotal(stringList.size());
                        qrCodeSender.setContent(stringList.get(j));
                        codeSenderArrayList.add(qrCodeSender);
                    }
                    if (codeSenderArrayList.size() > 0) {
                        for (int j = 0; j < codeSenderArrayList.size(); j++) {
                            Bitmap bitmap = CommonUtils.generateQRCode(gson.toJson(codeSenderArrayList.get(j)));
                            listUri.add(CommonUtils.getBitmapUri(context, bitmap));
                        }
                    }

                }
            }
        } catch (Exception e) {
            /// showDialogErr(R.string.err_upload);
            Log.e("Error genericListUri ", e.getMessage());
        }

        return listUri;
    }

    public static void restartApp(ECashBaseActivity activity) {
        Intent intent = new Intent(activity, AccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}