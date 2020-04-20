package vn.ecpay.ewallet.common.eccrypto;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;

import android.util.Base64;

import java.util.List;

/**
 * Created by Joe on August, 20 2019 .
 */
public class ECashCrypto {
    static final byte elementSplit = ';';//59
    static final byte signatureSplit = ':';//58
    static final byte cashSplit = '|';//124

    /*
     * {"countryCode;issuerCode;decisionNo;serial;value;cycle;actived;expired","accsign","tresign"}*/
    public static byte[][] encryptV1(EllipticCurve ec, ECPublicKeyParameters kp, String[][] cashArray) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (String[] cash : cashArray) {
                baos.write(cash[0].getBytes());
                baos.write(signatureSplit);
                baos.write(Base64.decode(cash[1], Base64.DEFAULT));
                baos.write(elementSplit);
                baos.write(Base64.decode(cash[2], Base64.DEFAULT));
                baos.write(cashSplit);
            }
            byte[] rawData = baos.toByteArray();
            return ECElGamal.encrypt(ec, kp, rawData);
        } finally {
            if (baos != null)
                baos.close();
        }
    }

//    public static byte[][] encryptV2(EllipticCurve ec, byte[] publicKey, String[][] cashArray) throws IOException {
//        ByteArrayOutputStream baos = null;
//        try {
//            ECPublicKeyParameters pubKey = EllipticCurve.getPublicKeyParameters(publicKey);
//            baos = new ByteArrayOutputStream();
//            for (String[] cash : cashArray) {
//                byte[] b1 = cash[0].getBytes();
//                byte[] b2 = Base64.decode(cash[1], Base64.DEFAULT);
//                byte[] b3 = Base64.decode(cash[2], Base64.DEFAULT);
//
//                baos.write(getLength(b1.length + 3 + b2.length + 3 + b3.length + 3).getBytes());
//                baos.write(getLength(b1.length).getBytes());
//                baos.write(b1);
//                baos.write(getLength(b2.length).getBytes());
//                baos.write(b2);
//                baos.write(getLength(b3.length).getBytes());
//                baos.write(b3);
//            }
//            byte[] rawData = baos.toByteArray();
//            return ECElGamal.encrypt(ec, pubKey, rawData);
//        } finally {
//            if (baos != null)
//                baos.close();
//        }
//    }

    public static byte[][] encryptV2(EllipticCurve ec, ECPublicKeyParameters kp, String[][] cashArray) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (String[] cash : cashArray) {
                byte[] b1 = cash[0].getBytes();
                byte[] b2 = Base64.decode(cash[1], Base64.DEFAULT);
                byte[] b3 = Base64.decode(cash[2], Base64.DEFAULT);

                baos.write(getLength(b1.length + 3 + b2.length + 3 + b3.length + 3).getBytes());
                baos.write(getLength(b1.length).getBytes());
                baos.write(b1);
                baos.write(getLength(b2.length).getBytes());
                baos.write(b2);
                baos.write(getLength(b3.length).getBytes());
                baos.write(b3);
            }
            byte[] rawData = baos.toByteArray();
            return ECElGamal.encrypt(ec, kp, rawData);
        } finally {
            if (baos != null)
                baos.close();
        }
    }

    public static ECPublicKeyParameters loadPubKey(byte[] pubKey) {
        Security.addProvider(new BouncyCastleProvider());
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECDomainParameters domain = new ECDomainParameters(spec.getCurve(), spec.getG(), spec.getN());
        return new ECPublicKeyParameters(spec.getCurve().decodePoint(pubKey), domain);
    }

    public static String[][] decryptV1(EllipticCurve ec, ECPrivateKeyParameters ks, byte[][] blockEnc) {
        byte[] rawData = ECElGamal.decrypt(ec, ks, blockEnc);
        int offset = 0;
        int length = 0;
        List<String[]> listCash = new ArrayList<>();

        for (int i = 0; i < rawData.length; i++) {
            if (rawData[i] == cashSplit) {
                String[] cashString = new String[3];
                byte[] cashBytes = new byte[length];
                System.arraycopy(rawData, offset, cashBytes, 0, length);
                offset += length + 1;
                length = 0;
                for (int j = 0; j < cashBytes.length; j++) {
                    if (cashBytes[j] == signatureSplit) {
                        byte[] elementBytes = new byte[j];
                        System.arraycopy(cashBytes, 0, elementBytes, 0, j);
                        cashString[0] = new String(elementBytes);

                        byte[] signatureBytes = new byte[cashBytes.length - j - 1];
                        System.arraycopy(cashBytes, j + 1, signatureBytes, 0, cashBytes.length - j - 1);
                        for (int k = 0; k < signatureBytes.length; k++) {
                            if (signatureBytes[k] == elementSplit) {
                                byte[] accsignBytes = new byte[k];
                                System.arraycopy(signatureBytes, 0, accsignBytes, 0, k);
                                cashString[1] = Base64.encodeToString(accsignBytes, Base64.DEFAULT);

                                byte[] tresignBytes = new byte[signatureBytes.length - k - 1];
                                System.arraycopy(signatureBytes, k + 1, tresignBytes, 0, signatureBytes.length - k - 1);
                                cashString[2] = Base64.encodeToString(tresignBytes, Base64.DEFAULT);

                                listCash.add(cashString);
                            }
                        }
                        break;
                    }
                }
            } else {
                length++;
            }
        }
        String[][] retVal = new String[listCash.size()][];
        for (int i = 0; i < listCash.size(); i++) {
            retVal[i] = listCash.get(i);
        }
        return retVal;
    }

    public static String[][] decryptV2(EllipticCurve ec, ECPrivateKeyParameters ks, byte[][] blockEnc) throws IOException {
        byte[] rawData = ECElGamal.decrypt(ec, ks, blockEnc);
        List<String[]> listCash = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(rawData))) {
            do {
                byte[] bLength = new byte[3];
                dis.readFully(bLength, 0, 3);
                int nLength = Integer.parseInt(new String(bLength));
                byte[] bElement = new byte[nLength];
                dis.readFully(bElement, 0, nLength);

                String[] cash = new String[3];
                ByteArrayInputStream bais = new ByteArrayInputStream(bElement);

                bais.read(bLength, 0, 3);
                nLength = Integer.parseInt(new String(bLength));
                bElement = new byte[nLength];
                bais.read(bElement, 0, nLength);
                cash[0] = new String(bElement);

                bais.read(bLength, 0, 3);
                nLength = Integer.parseInt(new String(bLength));
                bElement = new byte[nLength];
                bais.read(bElement, 0, nLength);
                cash[1] = Base64.encodeToString(bElement, Base64.DEFAULT);

                bais.read(bLength, 0, 3);
                nLength = Integer.parseInt(new String(bLength));
                bElement = new byte[nLength];
                bais.read(bElement, 0, nLength);
                cash[2] = Base64.encodeToString(bElement, Base64.DEFAULT);
                listCash.add(cash);
                bais.close();

            } while (dis.available() != 0);
        }

        String[][] retVal = new String[listCash.size()][];
        for (int i = 0; i < listCash.size(); i++) {
            retVal[i] = listCash.get(i);
        }
        return retVal;
    }

    public static String getLength(int length) throws IOException {
        if (length < 10)
            return "00" + length;
        else if (length < 100)
            return "0" + length;
        else if (length < 1000)
            return String.valueOf(length);
        else
            throw new IOException("Length less than 1000 bytes");
    }

    public static void main(String... args) throws IOException {
        String[] s0 = {"VN;GPB;GPB01001;2716791183927;500000;20181220;20501220;2",
                "MEUCIQCq18K/LHBxKqcpY1Q6wcqSheZKt/GjrMJbmypxazlVxwIgL+UYif0Fkbm7mdH/92oJj3UQsj/m0fVMoiXhSmia3+E=",
                "MEUCIQCY2mEltt1BY0sKQUZ6xCswSjha5iW+15Pw0kQygiXZPAIgMw1FlAb8tPoLcSG4lKOBvfkAiF1aoRO00o8YZpOV20s="};
        String[][] cashArray = {s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0, s0};
        long t1 = System.currentTimeMillis();
        byte[][] blockEnc = encryptV2(Test.ec, Test.kp, cashArray);

        String encData = Base64.encodeToString(blockEnc[0], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[1], Base64.DEFAULT) + (char) elementSplit
                + Base64.encodeToString(blockEnc[2], Base64.DEFAULT);

        long t2 = System.currentTimeMillis();
        String[][] ok = decryptV2(Test.ec, Test.ks, blockEnc);
        long t3 = System.currentTimeMillis();
        System.out.println((t2 - t1) + "  milliseconds");
        System.out.println((t3 - t2) + "  milliseconds");
//    for (int i = 0; i < ok.length; i++) {
//      System.out.println(Arrays.toString(ok[i]));
//    }
    }
}
