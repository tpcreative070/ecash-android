//package vn.ecpay.ewallet;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//
//import androidx.annotation.Nullable;
//
//import com.google.gson.Gson;
//
//import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
//import org.bouncycastle.crypto.params.ECPublicKeyParameters;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.OnClick;
//import vn.ecpay.ewallet.common.base.ECashBaseActivity;
//import vn.ecpay.ewallet.common.eccrypto.EllipticCurve;
//import vn.ecpay.ewallet.common.keystore.KSDeCrypt;
//import vn.ecpay.ewallet.common.keystore.KSEnCrypt;
//import vn.ecpay.ewallet.common.utils.Constant;
//import vn.ecpay.ewallet.database.profile.UserDatabase;
//import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
//
//public class TestKeyStore extends ECashBaseActivity {
//
//    @BindView(R.id.start)
//    Button start;
//    @BindView(R.id.add)
//    Button add;
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.test_socket;
//    }
//
//    @Override
//    protected void setupActivityComponent() {
//
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//
//    private void setPassAndAddData() {
//        AccountInfo accountInfo = new AccountInfo();
//        accountInfo.setNickname("ahihi");
//        UserDatabase.getINSTANCE(this, getMasterKey());
//        UserDatabase.insertTask(accountInfo);
//    }
//
//    private void getNOPass() {
//        try {
//            UserDatabase.getINSTANCE(this, getMasterKey());
//            List<AccountInfo> list = UserDatabase.getAllProfile();
//            Log.e("size", "" + list.size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void changePass() {
//        UserDatabase.getINSTANCE(this, Constant.MASTER_KEY_STRING);
//        UserDatabase.changeKey(Constant.MASTER_KEY_STRING_NEW);
//    }
//
//    private void getListKey() {
//        KSDeCrypt ksDeCrypt = KSDeCrypt.createInstance();
//        ksDeCrypt.getListKey();
//        Log.e("listkey", "listkey");
//    }
//
//    public SharedPreferences getSharedPreference() {
//        return getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
//    }
//
//    private String getPublicKey() {
//        EllipticCurve ec = EllipticCurve.getSecp256k1();
//        ECPrivateKeyParameters ks = ec.generatePrivateKeyParameters();
//        ECPublicKeyParameters kp = ec.getPublicKeyParameters(ks);
//
//        byte[] mPriKey = ks.getD().toByteArray();
//        byte[] mPubKey = kp.getQ().getEncoded(false);
//
//        String privateKeyBase64 = Base64.encodeToString(mPriKey, Base64.DEFAULT).replaceAll("\n", "");
//        String publicKeyBase64 = Base64.encodeToString(mPubKey, Base64.DEFAULT).replaceAll("\n", "");
//        return publicKeyBase64;
//    }
//
//    @OnClick({R.id.add_pass_private, R.id.add_pass_master, R.id.get_pass_private, R.id.get_pass_master})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.add_pass_private:
//                addPrivateKey();
////                addMasterKey();
//                break;
//            case R.id.add_pass_master:
//                setPassAndAddData();
//                break;
//            case R.id.get_pass_private:
//                getPrivateKey();
////                getMasterKey();
//                break;
//            case R.id.get_pass_master:
//                getNOPass();
//                break;
//        }
//    }
//
//    private String getMasterKey() {
//        Gson gson = new Gson();
//        SharedPreferences prefs = getSharedPreference();
//        String jsEncrypt = prefs.getString(Constant.INSTANCE_KS_ENCRYPT_MASTER, null);
//        KSEnCrypt ksEnCrypt = gson.fromJson(jsEncrypt, KSEnCrypt.class);
//        KSDeCrypt ksDeCrypt = KSDeCrypt.createInstance();
//        String a = ksDeCrypt.getKey(Constant.WALLET_ALIAS_MASTER_KEY,
//                ksEnCrypt.getEncryption(), ksEnCrypt.getIv());
//        return ksDeCrypt.getKey(Constant.WALLET_ALIAS_MASTER_KEY,
//                ksEnCrypt.getEncryption(), ksEnCrypt.getIv());
//    }
//
//    private void getPrivateKey() {
//        Gson gson = new Gson();
//        SharedPreferences prefs = getSharedPreference();
//        String jsEncrypt = prefs.getString(Constant.INSTANCE_KS_ENCRYPT_PRIVATE, null);
//        KSEnCrypt ksEnCrypt = gson.fromJson(jsEncrypt, KSEnCrypt.class);
//        KSDeCrypt ksDeCrypt = KSDeCrypt.createInstance();
//        String strPriKeyHashed = ksDeCrypt.getKey(Constant.WALLET_ALIAS_PRIVATE_KEY,
//                ksEnCrypt.getEncryption(), ksEnCrypt.getIv());
//        Log.e("key", "ahihi");
//    }
//
//    private void addMasterKey() {
//        KSEnCrypt mEncrypt = KSEnCrypt.createInstance();
//        mEncrypt.saveKey(Constant.WALLET_ALIAS_MASTER_KEY, getPublicKey());
//        Gson gson = new Gson();
//        String jsKSEncrypt = gson.toJson(mEncrypt);
//        SharedPreferences prefs = getSharedPreference();
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(Constant.INSTANCE_KS_ENCRYPT_MASTER, jsKSEncrypt);
//        editor.apply();
//    }
//
//    private void addPrivateKey() {
//        KSEnCrypt mEncrypt = KSEnCrypt.createInstance();
//        mEncrypt.saveKey(Constant.WALLET_ALIAS_PRIVATE_KEY, getPublicKey());
//        Gson gson = new Gson();
//        String jsKSEncrypt = gson.toJson(mEncrypt);
//        SharedPreferences prefs = getSharedPreference();
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(Constant.INSTANCE_KS_ENCRYPT_PRIVATE, jsKSEncrypt);
//        editor.apply();
//    }
//}
