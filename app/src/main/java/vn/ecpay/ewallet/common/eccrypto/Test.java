package vn.ecpay.ewallet.common.eccrypto;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;

/**
 * Created by Joe on June, 20 2019 .
 */
public class Test {
    public static EllipticCurve ec = EllipticCurve.getSecp256k1();
    public static ECPrivateKeyParameters ks = ec.generatePrivateKeyParameters();
    public static ECPublicKeyParameters kp = ec.getPublicKeyParameters(ks);
    static String dataSign = "ABC123xyz";
    static String rawData = "123456";

    /*
     * sinh chữ ký điện tử bằng giải thuật ECDSA*/
    public static byte[] sign() throws IOException {
        return ec.sign(ks, dataSign.getBytes());
    }

    /*
     * xác thực chữ ký điện tử bằng giải thuật ECDSA*/
    public static boolean verify(byte[] signature) {
        return ec.verify(dataSign.getBytes(), signature, kp);
    }

    /*
     * mã hóa dữ liệu bằng giải thuật ElGamal kết hợp AES*/
    public static byte[][] encrypt() {
        return ECElGamal.encrypt(ec, kp, rawData.getBytes());
    }

    /*
     * giải mã dữ liệu bằng giải thuật ElGamal kết hợp AES*/
    public static byte[] decrypt(byte[][] blockEncrypted) {
        return ECElGamal.decrypt(ec, ks, blockEncrypted);
    }

    /*mô phỏng giải thuật Massey-Omura kết hợp AES*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void simulateMasseyOmura() {
        BigInteger ks1 = ec.randomD();
        BigInteger ks1Inverse = ks1.modInverse(ec.getN());

        BigInteger ks2 = ec.randomD();
        BigInteger ks2Inverse = ks2.modInverse(ec.getN());

        byte[][] blockEncrypted = ECMasseyOmura.encryptAll(ec, ks1, rawData.getBytes());
        System.out.println("blockEncrypted[0] >> " + Base64.getEncoder().encodeToString(blockEncrypted[0]));
        System.out.println("blockEncrypted[1] >> " + Base64.getEncoder().encodeToString(blockEncrypted[1]));
        blockEncrypted = ECMasseyOmura.encryptKey(ec, ks2, blockEncrypted);
        System.out.println("blockEncrypted[0] >> " + Base64.getEncoder().encodeToString(blockEncrypted[0]));
        System.out.println("blockEncrypted[1] >> " + Base64.getEncoder().encodeToString(blockEncrypted[1]));
        blockEncrypted = ECMasseyOmura.decryptKey(ec, ks1Inverse, blockEncrypted);
        System.out.println("blockEncrypted[0] >> " + Base64.getEncoder().encodeToString(blockEncrypted[0]));
        System.out.println("blockEncrypted[1] >> " + Base64.getEncoder().encodeToString(blockEncrypted[1]));
        byte[] blockDecrypted = ECMasseyOmura.decryptAll(ec, ks2Inverse, blockEncrypted);
        System.out.println(new String(blockDecrypted));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String... args) throws IOException {
        /*begin sign-verify*/
        System.out.println("dataSign          >> " + dataSign);
        System.out.println("rawData           >> " + rawData);
        long t1 = System.currentTimeMillis();
//        byte[] signature = sign();
        System.out.println("sig : " + (System.currentTimeMillis() - t1) + " milliseconds");
//        System.out.println("signature(" + signature.length + ")     >> " + Base64.getEncoder().encodeToString(signature));
//        long t2 = System.currentTimeMillis();
//        boolean valid = verify(signature);
//        System.out.println("ver : " + (System.currentTimeMillis() - t2) + " milliseconds");
//        System.out.println("verify            >> " + valid);
        /*end sign-verify*/

        /*begin Elgamal*/
        long t3 = System.currentTimeMillis();
        byte[][] blockEncrypted = encrypt();
        System.out.println("enc : " + (System.currentTimeMillis() - t3) + " milliseconds");
        System.out.println("blockEncrypted[0] >> " + Base64.getEncoder().encodeToString(blockEncrypted[0]));
        System.out.println("blockEncrypted[1] >> " + Base64.getEncoder().encodeToString(blockEncrypted[1]));
        System.out.println("blockEncrypted[2] >> " + Base64.getEncoder().encodeToString(blockEncrypted[2]));
        long t4 = System.currentTimeMillis();
        byte[] blockDecrypted = decrypt(blockEncrypted);
        System.out.println("dec : " + (System.currentTimeMillis() - t4) + " milliseconds");
        System.out.println("decrypted         >> " + new String(blockDecrypted));


//    byte[][] dataEnc = ECElGamal.encrypt(ec, kp, rawData.getBytes());
//    System.out.println(dataEnc);
//    System.out.println(ECElGamal.decrypt(ec, ks, dataEnc));
        /*end Elgamal*/

        /*begin MO*/
//    simulateMasseyOmura();
        /*end MO*/

    }
}
