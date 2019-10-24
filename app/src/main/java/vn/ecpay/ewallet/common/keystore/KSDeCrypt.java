package vn.ecpay.ewallet.common.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class KSDeCrypt {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static KSDeCrypt mInstance = null;
    private KeyStore keyStore;

    public static KSDeCrypt createInstance() {
        if (mInstance == null) {
            return mInstance = new KSDeCrypt();
        }
        return mInstance;
    }

    public KSDeCrypt() {
        initKeyStore();
    }

    private void initKeyStore() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey(final String alias, final byte[] encryptedData, final byte[] encryptionIv) {
        try {
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);
            return new String(cipher.doFinal(encryptedData), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList getListKey() {
        ArrayList keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
        } catch (Exception e) {
        }
        return keyAliases;
    }

    public void deleteKeystore(String alias) throws KeyStoreException {
        keyStore.deleteEntry(alias);
    }

    private SecretKey getSecretKey(final String alias) {
        try {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
