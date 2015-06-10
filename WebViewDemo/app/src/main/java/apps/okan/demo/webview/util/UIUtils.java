package apps.okan.demo.webview.util;

import android.content.res.Resources;

/**
 * @author Okan SAYILGAN
 */
public class UIUtils {

    /**
     * Converts DP values to Pixel.
     *
     * @param dp
     * @return
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
