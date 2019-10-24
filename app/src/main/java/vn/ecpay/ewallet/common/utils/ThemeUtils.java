package vn.ecpay.ewallet.common.utils;


import vn.ecpay.fragmentcommon.ui.actionbar.Theme;
import java.util.HashMap;

public class ThemeUtils extends Theme {

    public static final String key_actionBarDefault = "actionBarDefault";
    public static final String key_backgroundDefault = "backgroundDefault";
    public static final String key_lineDefault = "lineDefault";
    public static final String key_drawerProfileDefault = "drawerProfileDefault";

    private static HashMap<String, Integer> defaultColors = new HashMap<>();

    static {
        defaultColors.put(key_actionBarDefault, 0xff4b4b4b);
        defaultColors.put(key_backgroundDefault, 0xffffffff);
        defaultColors.put(key_lineDefault, 0xffdfdfdf);
        defaultColors.put(key_drawerProfileDefault, 0xff666666);
    }

    public static int getColor(String key) {
        if (defaultColors.containsKey(key)) {
            return defaultColors.get(key);
        }
        return 0xffffffff;
    }
}
