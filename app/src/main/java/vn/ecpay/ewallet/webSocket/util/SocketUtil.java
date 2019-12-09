package vn.ecpay.ewallet.webSocket.util;

import android.content.Context;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.eccrypto.SHA256;
import vn.ecpay.ewallet.common.keystore.KeyStoreUtils;
import vn.ecpay.ewallet.model.account.register.register_response.AccountInfo;
import vn.ecpay.ewallet.common.utils.CommonUtils;
import vn.ecpay.ewallet.common.utils.Constant;
import vn.ecpay.ewallet.webSocket.genenSignature.ChangeSignature;
import vn.ecpay.ewallet.webSocket.genenSignature.WalletSignature;

public class SocketUtil {

    public static String getUrl(AccountInfo accountInfo, Context context) {
        String auditNumber = CommonUtils.getAuditNumber();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("ws")
                .encodedAuthority(context.getString(R.string.url_socket))
                .appendPath("sync")
                .appendQueryParameter("channelCode", Constant.CHANNEL_CODE)
                .appendQueryParameter("username", accountInfo.getUsername())
                .appendQueryParameter("walletId", String.valueOf(accountInfo.getWalletId()))
                .appendQueryParameter("functionCode", Constant.FUNCTION_WEB_SOCKET)
                .appendQueryParameter("terminalId", accountInfo.getTerminalId())
                .appendQueryParameter("terminalInfo", accountInfo.getTerminalInfo())
                .appendQueryParameter("auditNumber", auditNumber)
                .appendQueryParameter("sessionId", ECashApplication.getAccountInfo().getSessionId())
                .appendQueryParameter("channelSignature", getChannelSignature(accountInfo, auditNumber))
                .appendQueryParameter("token", CommonUtils.getToken(accountInfo))
                .appendQueryParameter("walletSignature", getWalletSignature(accountInfo, context));
        return builder.build().toString();
    }

    public static String getChannelSignature(AccountInfo accountInfo, String auditNumber) {
        ChangeSignature changeSignature = new ChangeSignature();

        changeSignature.setAuditNumber(auditNumber);
        changeSignature.setChannelCode(Constant.CHANNEL_CODE);
        changeSignature.setFunctionCode(Constant.FUNCTION_WEB_SOCKET);
        changeSignature.setSessionId(ECashApplication.getAccountInfo().getSessionId());
        changeSignature.setTerminalId(accountInfo.getTerminalId());
        changeSignature.setTerminalInfo(accountInfo.getTerminalInfo());
        changeSignature.setUsername(accountInfo.getUsername());
        changeSignature.setWalletId(String.valueOf(accountInfo.getWalletId()));
        changeSignature.setToken(CommonUtils.getToken(accountInfo));

        CommonUtils.logJson(changeSignature);
        String data = CommonUtils.getStringAlphabe(changeSignature);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(changeSignature));
        return CommonUtils.generateSignature(dataSign);
    }

    public static String getWalletSignature(AccountInfo accountInfo, Context context) {
        WalletSignature walletSignature = new WalletSignature();

        walletSignature.setTerminalId(accountInfo.getTerminalId());
        walletSignature.setTerminalInfo(accountInfo.getTerminalInfo());
        walletSignature.setWalletId(String.valueOf(accountInfo.getWalletId()));

        String data = CommonUtils.getStringAlphabe(walletSignature);
        byte[] dataSign = SHA256.hashSHA256(CommonUtils.getStringAlphabe(walletSignature));
        return CommonUtils.generateSignature(dataSign, KeyStoreUtils.getPrivateKey(context));
    }
}
