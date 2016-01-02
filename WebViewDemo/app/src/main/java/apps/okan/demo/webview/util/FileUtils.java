package apps.okan.demo.webview.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author Okan SAYILGAN
 */
public class FileUtils {


    /**
     * Creates or returns the "breezy" Folder in external storage directory.
     *
     * @return
     */
    public static File getExternalBreezyDirectory() {

        File directory = new File(Environment.getExternalStorageDirectory(), "/mWebViewDemo");

        if (directory.exists() == false) {
            directory.mkdirs();
        }

        return directory;
    }

    public static File getDexCacheDirectory(Context context) {
        return context.getDir("dex-cache", Context.MODE_PRIVATE);
    }
}
