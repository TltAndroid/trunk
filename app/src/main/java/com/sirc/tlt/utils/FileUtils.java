package com.sirc.tlt.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * 文件工具类
 * Created by Hooliganiam on 17/5/13.
 */

public class FileUtils {

    private File cropIconDir;
    private File iconDir;

    public FileUtils() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            isFolderExists(Config.cameraPath);
        }
    }

    public static void isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    public File createCropFile() {
        File file = new File(Config.IMAGE_FILE_LOCATION);
        return  file;
    }

    public File createIconFile() {

        File file = new File(Config.TEMP_IMAGE_FILE_LOCATION);
        return  file;
    }

    public static boolean isFileExit(String strFolder){
        File file = new File(strFolder);
        return file.exists();
    }


}
