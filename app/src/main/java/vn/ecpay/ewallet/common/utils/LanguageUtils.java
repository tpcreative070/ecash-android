package vn.ecpay.ewallet.common.utils;

import vn.ecpay.ewallet.ECashApplication;
import vn.ecpay.ewallet.R;
import vn.ecpay.ewallet.common.language.SharedPrefs;
import vn.ecpay.ewallet.model.language.LanguageObject;

public class LanguageUtils {
    private static LanguageObject sCurrentLanguage = null;

    public static LanguageObject getCurrentLanguage() {
        if (sCurrentLanguage == null) {
            sCurrentLanguage = initCurrentLanguage();
        }
        return sCurrentLanguage;
    }

    private static LanguageObject initCurrentLanguage() {
        LanguageObject currentLanguage =
                SharedPrefs.getInstance().get(SharedPrefs.LANGUAGE, LanguageObject.class);
        if (currentLanguage != null) {
            return currentLanguage;
        }
        currentLanguage = new LanguageObject(Constant.DEFAULT_LANGUAGE_ID,
                ECashApplication.getInstance().getString(R.string.language_vietnamese),
                ECashApplication.getInstance().getString(R.string.language_vietnamese_code));
        SharedPrefs.getInstance().put(SharedPrefs.LANGUAGE, currentLanguage);
        return currentLanguage;
    }

    static void changeLanguage(LanguageObject language) {
        SharedPrefs.getInstance().put(SharedPrefs.LANGUAGE, language);
        sCurrentLanguage = language;
    }
}
