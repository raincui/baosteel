package com.android.baosteel.lan.basebusiness.util;

/**
 * @author yulong.cui
 * @Package com.android.baosteel.lan.basebusiness.util
 * @Title: FileUtils
 * Create DateTime: 2017/4/17
 */
public class FileUtils {
    public static boolean isLegalFile(String suffix) {
        suffix = suffix.toUpperCase();

        if (suffix.endsWith("TIF") || suffix.endsWith("DOC") || suffix.endsWith("TXT")
                || suffix.endsWith("XLS") || suffix.endsWith("PDF")
                || suffix.endsWith("WPS") || suffix.endsWith("PPT")
                || suffix.endsWith("ET") || suffix.endsWith("DPS")
                || suffix.endsWith("HTML") || suffix.endsWith("HTM")
                || suffix.endsWith("RTF")) {
            return true;
        }
        return false;
    }
}
