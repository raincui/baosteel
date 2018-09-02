package com.android.yl.baowu.basebusiness.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.Date;

/**
 * SD卡路径工具类
 */
public class PathAction {
    /**
     * 检测SD卡是否可用
     *
     * @return
     */
    public static boolean checkSDCardAvailability() {
        boolean result = false;
        try {
            Date now = new Date();
            long times = now.getTime();
            String fileName = Environment.getExternalStorageDirectory().toString() + File.separator + "delete" + times + ".test";
            File file = new File(fileName);
            result = file.createNewFile();
            file.delete();
            result = true;
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 获取程序安装目录
     *
     * @param ct
     * @return
     */
    public static String getCurrentDataPath(Context ct) {
        if (ct != null) {
            return File.separator + "data" + File.separator + "data" + File.separator + ct.getPackageName().toString()
                    + File.separator;
        }
        return null;
    }

    /**
     * SD卡剩余空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize;
        long freeBlocks;
        // 获取单个数据块的大小(Byte)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = sf.getBlockSizeLong();
            freeBlocks = sf.getAvailableBlocksLong();
        } else {
            blockSize = sf.getBlockSize();
            // 空闲的数据块的数量
            freeBlocks = sf.getAvailableBlocks();
        }

        // 返回SD卡空闲大小
        return (freeBlocks * blockSize) / 1048576;
    }

    /**
     * 获取手机内部内存可用空间大小
     *
     * @return
     */
    public static long getInternalSDFreeSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long freeBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            freeBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            // 空闲的数据块的数量
            freeBlocks = stat.getAvailableBlocks();
        }
        return (freeBlocks * blockSize) / 1048576;
    }

    /**
     * 创建目录
     *
     * @param fullDir
     * @return
     */
    public static boolean createDirectory(String fullDir) {
        File f = new File(fullDir);
        if (f.exists()) {
            if (!f.isDirectory()) {
                f.delete();
            } else {
                return true;
            }
        }
        return f.mkdirs();
    }

    /**
     * 获取精确到当前包名的路径
     *
     * @param ct
     * @return
     */
    public static String getCurrentDataPathInSDCard(Context ct) {
        if (ct != null) {
            return Environment.getExternalStorageDirectory().toString() + File.separator + ct.getPackageName().toString()
                    + File.separator;
        }
        return null;
    }

    /**
     * 内存卡中创建目录
     *
     * @param ct
     * @param strDir
     * @return
     */
    public static boolean createDirectoryInSDCard(Context ct, String strDir) {
        String fullDir = getCurrentDataPathInSDCard(ct) + strDir;
        File f = new File(fullDir);
        if (f.exists()) {
            if (!f.isDirectory()) {
                f.delete();
            } else {
                return true;
            }
        }
        return f.mkdirs();
    }

    /**
     * 得到根目录路径
     *
     * @param ct
     * @return
     */
    public static String createRootDir(Context ct) {
        String strTasksPath = "";
        if (checkSDCardAvailability()) {
            strTasksPath = getCurrentDataPathInSDCard(ct);
        } else {
            strTasksPath = getCurrentDataPath(ct);
        }
        return strTasksPath;
    }

    public static String addSeparator(String strDir) {
        if (!strDir.endsWith(File.separator)) {
            return strDir + File.separator;
        }
        return strDir;
    }
}
