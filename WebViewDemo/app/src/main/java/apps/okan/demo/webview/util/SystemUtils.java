package apps.okan.demo.webview.util;

import android.os.Build;

/**
 * Utilities Class to gather System and OS Methods.
 *
 * @author osayilgan
 */
public class SystemUtils {

    /**
     * Checks wheter the Print Framework is included in the SDK.
     * It's supported after Android KitKat Release.
     *
     * @return
     */
    public static boolean isPrintFrameworkSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
