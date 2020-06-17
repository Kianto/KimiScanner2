package com.app.util;

import java.text.DecimalFormat;

public class FileSizeConvertor {
    private static final long ONE_GB = 1073741824;
    private static final long ONE_KB = 1024;
    private static final long ONE_MB = 1048576;

    public static String formatFileSize(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (size < ONE_KB) {
            return decimalFormat.format((double) size) + "B";
        } else if (size < ONE_MB) {
            return decimalFormat.format(size / 1024.0d) + "K";
        } else if (size < ONE_GB) {
            return decimalFormat.format(size / 1048576.0d) + "M";
        } else {
            return decimalFormat.format(size / 1.073741824E9d) + "G";
        }
    }

}
