package apps.okan.demo.webview.util;

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

        File directory = new File(Environment.getExternalStorageDirectory(), "/breezy");

        if (directory.exists() == false) {
            directory.mkdir();
        }

        return directory;
    }
}
