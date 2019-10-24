package vn.ecpay.ewallet.common.keystore;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import vn.ecpay.ewallet.common.utils.Constant;

public class KeyStoreUtils {
    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void saveKeyPrivateWallet(String mPriKey, Context context) {
        KSEnCrypt mEncrypt = KSEnCrypt.createInstance();
        mEncrypt.saveKey(Constant.WALLET_ALIAS_PRIVATE_KEY, mPriKey);
        Gson gson = new Gson();
        String jsKSEncrypt = gson.toJson(mEncrypt);

        SharedPreferences prefs = getSharedPreference(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constant.INSTANCE_KS_ENCRYPT_PRIVATE, jsKSEncrypt);
        editor.apply();
    }

    public static void saveMasterKey(String masterKey, Context context) {
        KSEnCrypt mEncrypt = KSEnCrypt.createInstance();
        mEncrypt.saveKey(Constant.WALLET_ALIAS_MASTER_KEY, masterKey);
        Gson gson = new Gson();
        String jsKSEncrypt = gson.toJson(mEncrypt);

        SharedPreferences prefs = getSharedPreference(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constant.INSTANCE_KS_ENCRYPT_MASTER, jsKSEncrypt);
        editor.apply();
    }

    public static String getPrivateKey(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreference(context);
        String jsEncrypt = prefs.getString(Constant.INSTANCE_KS_ENCRYPT_PRIVATE, null);
        KSEnCrypt ksEnCrypt = gson.fromJson(jsEncrypt, KSEnCrypt.class);
        KSDeCrypt ksDeCrypt = KSDeCrypt.createInstance();
        try {
            return ksDeCrypt.getKey(Constant.WALLET_ALIAS_PRIVATE_KEY,
                    ksEnCrypt.getEncryption(), ksEnCrypt.getIv());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMasterKey(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = getSharedPreference(context);
        String jsEncrypt = prefs.getString(Constant.INSTANCE_KS_ENCRYPT_MASTER, null);
        KSEnCrypt ksEnCrypt = gson.fromJson(jsEncrypt, KSEnCrypt.class);
        KSDeCrypt ksDeCrypt = KSDeCrypt.createInstance();
        try {
            return ksDeCrypt.getKey(Constant.WALLET_ALIAS_MASTER_KEY,
                    ksEnCrypt.getEncryption(), ksEnCrypt.getIv());
        } catch (Exception e) {
            return null;
        }
    }
}
