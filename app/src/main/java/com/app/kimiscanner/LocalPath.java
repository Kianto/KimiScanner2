package com.app.kimiscanner;

import android.os.Environment;

public final class LocalPath {
    // Ex: Environment.getExternalStorageDirectory().getPath()

    public static String ROOT_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/KimiScanner/";
    public static String ROOT_DIRECTORY_NAME = "KimiScanner";

}
