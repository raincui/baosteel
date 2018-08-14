package com.android.baosteel.lan.basebusiness.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Title: AppUtil.java<br>
 * Description: APP工具类<br>
 * Create DateTime: 2014年6月27日 下午2:56:20<br>
 *
 * @author ln
 */
public class AppUtil {
    private static int iNotifyID = 0;
    private static int goodsNotify = 100000;
    /**
     * Network type is unknown
     */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /**
     * Current network is GPRS
     */
    public static final int NETWORK_TYPE_GPRS = 1;
    /**
     * Current network is EDGE
     */
    public static final int NETWORK_TYPE_EDGE = 2;
    /**
     * Current network is UMTS
     */
    public static final int NETWORK_TYPE_UMTS = 3;
    /**
     * Current network is CDMA: Either IS95A or IS95B
     */
    public static final int NETWORK_TYPE_CDMA = 4;
    /**
     * Current network is EVDO revision 0
     */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /**
     * Current network is EVDO revision A
     */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /**
     * Current network is 1xRTT
     */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /**
     * Current network is HSDPA
     */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /**
     * Current network is HSUPA
     */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /**
     * Current network is HSPA
     */
    public static final int NETWORK_TYPE_HSPA = 10;
    /**
     * Current network is iDen
     */
    public static final int NETWORK_TYPE_IDEN = 11;
    /**
     * Current network is EVDO revision B
     */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /**
     * Current network is LTE
     */
    public static final int NETWORK_TYPE_LTE = 13;
    /**
     * Current network is eHRPD
     */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /**
     * Current network is HSPA+
     */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /**
     * Current network is GSM {@hide}
     */
    public static final int NETWORK_TYPE_GSM = 16;


    public static boolean isRunning(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> list = am.getRunningTasks(100);
            for (RunningTaskInfo info : list) {
                if (info.topActivity.getPackageName().equals(context.getPackageName()) && info.baseActivity.getPackageName()
                        .equals(context.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            LogUtil.e("appUtil", e.getMessage());
        }
        return false;
    }

    // IMEI号
    public static String getPhoneIMEI(Context context) {
        try {
            if (context != null) {
                TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (mTelephonyMgr != null) {
                    return mTelephonyMgr.getDeviceId();
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        String uuid = SaveDataGlobal.getString(SaveDataGlobal.UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            SaveDataGlobal.putString(SaveDataGlobal.UUID, UUID.randomUUID().toString());
        }
        return SaveDataGlobal.getString(SaveDataGlobal.UUID, UUID.randomUUID().toString());
    }

    // 手机网络制式号
    public static String getNetworkType(Context context) {
        String strNetworkType = "";
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                LogUtil.e("cocos2d-x", "Network getSubtypeName : " + _strSubTypeName);
                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case NETWORK_TYPE_GPRS:
                    case NETWORK_TYPE_EDGE:
                    case NETWORK_TYPE_CDMA:
                    case NETWORK_TYPE_1xRTT:
                    case NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case NETWORK_TYPE_UMTS:
                    case NETWORK_TYPE_EVDO_0:
                    case NETWORK_TYPE_EVDO_A:
                    case NETWORK_TYPE_HSDPA:
                    case NETWORK_TYPE_HSUPA:
                    case NETWORK_TYPE_HSPA:
                    case NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:                    // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (_strSubTypeName == null) {
                            strNetworkType = "null";
                        } else if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") ||
                                _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
                LogUtil.e("cocos2d-x", "Network getSubtype : " + Integer.valueOf(networkType).toString());
            }
        }
        LogUtil.e("cocos2d-x", "Network Type : " + strNetworkType);
        return strNetworkType;
    }

    // 手机型号
    public static String getPhoneModel() {
        // 品牌
        String phoneType = Build.BRAND;
        StringBuilder sbStr = new StringBuilder();
        // 型号
        String phoneModel = Build.MODEL;
        if (!TextUtils.isEmpty(phoneType)) {
            sbStr.append(phoneType);
        }
        if (!TextUtils.isEmpty(phoneModel)) {
            sbStr.append(phoneModel);
        }
        return sbStr.toString();
    }

    // 操作系统
    public static String getPhoneSystem() {
        String phoneSystem = Build.VERSION.RELEASE;
        if (!TextUtils.isEmpty(phoneSystem)) {
            return phoneSystem;
        }
        return "";
    }

    /**
     * 检测内部存储空间是否有，内存是否足够
     *
     * @return
     */
    public static boolean checkSdCardAndUseApp() {
        // 先注释SD卡
        boolean falg = PathAction.checkSDCardAvailability();
        if (falg) {
            if (PathAction.getSDFreeSize() > 10) {
                return true;
            }
        } else {
            if (PathAction.getInternalSDFreeSize() > 10) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为平板
     *
     * @return
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration
                .SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * final Activity activity ：调用该方法的Activity实例 long milliseconds ：震动的时长，单位是毫秒
     * long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */

    public static void vibrate(Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static void vibrate(Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * 获取渠道号
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager
                        .GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        if (applicationInfo.metaData.get(key) != null) {
                            resultData = applicationInfo.metaData.get(key).toString();
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    public static String getSerialNumber() {
        String str;
        try {
            str = Build.SERIAL;
        } catch (Exception e) {
            e.printStackTrace();
            str = "";
        }
        return str;
    }


    /**
     * ipv4 new
     *
     * @return
     */
    public static String getLocalIpAddress() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.e("WifiPreferenceIpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 获取IP
     */
    public static String getIpAddress(Context activity) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        }
        return null;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    // /^(170[0|5|9])\\d{7}$
    // 校验手机号码正则表达式
    public static boolean isPhoneNum(String str) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0,0-9])|17[0,0-9])\\d{8}$");
        Pattern p1 = Pattern.compile("^(170[0,5-9])\\d{7}$");
        Matcher m = p.matcher(str);
        Matcher m1 = p1.matcher(str);
        if (m.matches()) {
            return true;
        } else if (m1.matches()) {
            return true;
        }
        return false;
    }


    // 验证码检验
    public static boolean isCaptcha(String str) {
        Pattern p = Pattern.compile("^[0-9]*[0-9][0-9]*$");
        if (str.length() == 4) {
            Matcher m = p.matcher(str);
            return m.matches();
        }
        return false;
    }

    // 验证码检验
    public static boolean isCaptcha(CharSequence str) {
        Pattern p = Pattern.compile("^[0-9]*[0-9][0-9]*$");
        if (str.length() == 4) {
            Matcher m = p.matcher(str);
            return m.matches();
        }
        return false;
    }

    /*
     * ^$分别匹配字符串的开始和结束
     * (?=.*\d)表示字符串中有数字，(?=.*[a-z])(?=.*[A-Z])则分别表示字符串中有小写字母和大写字母
     * [a-zA-Z\d]{6,20}表示字母和数字有6到20位
     */
    // * @描述： 校验密码（6-20个字母、数字、下划线）
    public static boolean isPasswd(String pwd) {
        Pattern p = Pattern.compile("^[0-9_a-zA-Z]{6,20}$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    /*
     * ^$分别匹配字符串的开始和结束
     * (?=.*\d)表示字符串中有数字，(?=.*[a-z])(?=.*[A-Z])则分别表示字符串中有小写字母和大写字母
     * [a-zA-Z\d]{6,20}表示字母和数字有6到20位
     */
    // * @描述： 校验密码（6-20个字母、数字、下划线）
    public static boolean isPasswd(CharSequence str) {
        Pattern p = Pattern.compile("^[0-9_a-zA-Z]{6,20}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }


    // 自动隐藏软键盘
    public static void showInput(Context cxt, View view) {
        InputMethodManager imm = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
    }

    // 常用字符表正则
    public static boolean isPairedContent(String str) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9\u4e00-\u9fa5\\ \\,\\.\\?\\!\\:\\\\/\\@\\...\\'\\;\\；\\！\\&\\？\\~\\#\\(\\)" +
                "\\<\\（\\）\\【\\】\\《\\》\\…\\……\\‘\\’\\〝\\〞\\'\\>\\*\\&\\[\\]\\%\\^\\_\\-\\+\\=\\{\\}\\|\\，\\。\\？\\！\\：\\r\\n" +
                "\\～\\＠\\＃\\＜\\＞\\『\\』\\〖\\〗\\“\\”\\«\\»\\‹\\›\\︵\\︶\\︷\\︸\\︹\\︺\\︿\\﹀\\︽\\︾\\﹁\\﹂\\﹃\\﹄\\︻\\︼\\︗\\︘\\/\\／\\|\\｜\\＿\\﹏\\﹍\\﹎\\`\\¦\\¡\\^\\­\\¨\\ˊ\\¯\\￣\\﹉\\︴\\¿\\ˇ\\　\\＋\\－\\×\\÷\\＝\\﹢\\﹣\\±\\∶\\=\\/\\·\\﹣\\﹢]+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static final long INTERVAL = 1000L; //防止连续点击的时间间隔
    private static long lastClickTime = 0L; //上一次点击的时间

    /**
     * 防止按钮过快点击
     *
     * @return
     */
    public static boolean clickFilter() {
        long curr_time = System.currentTimeMillis();

        if ((curr_time - lastClickTime) > INTERVAL) {
            lastClickTime = curr_time;
            return false;
        } else {
            lastClickTime = curr_time;
            return true;
        }

    }

    public static int checkOp(Context context, String op) {
        int opindex = strOpToOp(op);
        if (opindex == -1) {
            return -1;
        }
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            Class c = object.getClass();
            try {
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, opindex, Binder.getCallingUid(), context.getPackageName());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * This provides a simple name for each operation to be used
     * in debug output.
     */
    private static String[] sOpNames = new String[]{"COARSE_LOCATION", "FINE_LOCATION", "GPS", "VIBRATE", "READ_CONTACTS",
            "WRITE_CONTACTS", "READ_CALL_LOG", "WRITE_CALL_LOG", "READ_CALENDAR", "WRITE_CALENDAR", "WIFI_SCAN",
            "POST_NOTIFICATION", "NEIGHBORING_CELLS", "CALL_PHONE", "READ_SMS", "WRITE_SMS", "RECEIVE_SMS",
            "RECEIVE_EMERGECY_SMS", "RECEIVE_MMS", "RECEIVE_WAP_PUSH", "SEND_SMS", "READ_ICC_SMS", "WRITE_ICC_SMS",
            "WRITE_SETTINGS", "SYSTEM_ALERT_WINDOW", "ACCESS_NOTIFICATIONS", "CAMERA", "RECORD_AUDIO", "PLAY_AUDIO",
            "READ_CLIPBOARD", "WRITE_CLIPBOARD", "TAKE_MEDIA_BUTTONS", "TAKE_AUDIO_FOCUS", "AUDIO_MASTER_VOLUME",
            "AUDIO_VOICE_VOLUME", "AUDIO_RING_VOLUME", "AUDIO_MEDIA_VOLUME", "AUDIO_ALARM_VOLUME", "AUDIO_NOTIFICATION_VOLUME",
            "AUDIO_BLUETOOTH_VOLUME", "WAKE_LOCK", "MONITOR_LOCATION", "MONITOR_HIGH_POWER_LOCATION", "GET_USAGE_STATS",
            "MUTE_MICROPHONE", "TOAST_WINDOW", "PROJECT_MEDIA", "ACTIVATE_VPN",};

    public static int strOpToOp(String op) {
        int index = -1;
        for (int i = 0; i < sOpNames.length; i++) {
            if (op.equals(sOpNames[i])) {
                index = i;
                break;
            }
        }

        return index;
    }

    public static int strToInt(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static float strToFloat(String str) {
        try {
            return Float.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1.0f;
    }

    public static double strToDouble(String str) {
        try {
            return Double.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1.00d;
    }

    public static long strToLong(String str) {
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return -1l;
    }

    /**
     * 获取APK VersionCode版本
     *
     * @param ct
     * @return
     */
    public static Integer getApkVersionCode(Context ct) {
        Integer apkVer = 0;
        try {
            PackageManager pm = ct.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ct.getPackageName().toString(), 0);
            apkVer = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return apkVer;
    }

    /**
     * 获取APK Version版本
     *
     * @param ct
     * @return
     */
    public static String getApkVerion(Context ct) {
        String apkVer = null;
        try {
            PackageManager pm = ct.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ct.getPackageName().toString(), 0);
            apkVer = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return apkVer;
    }

    public static String getPackagesName(Context ct) {
        PackageInfo info;
        try {
            info = ct.getPackageManager().getPackageInfo(ct.getPackageName(), 0);
            // 当前版本的包名
            String packageNames = info.packageName;
            return packageNames;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String textRemoveAllSpace(String key) {
        if (key == null) {
            return null;
        }
        return key.replaceAll(" +", "");
    }

    /**
     * password level check
     */
    public static int checkPasswordLevel(String pwd) {
        //-------------------
        //------纯--------
        Pattern all_num = Pattern.compile("^[0-9]+$");
        //纯字母 6-10
        Pattern all_abc = Pattern.compile("^[a-zA-Z]+$");

        Matcher all_num_mc = all_num.matcher(pwd);
        Matcher all_abc_mc = all_abc.matcher(pwd);
        //纯特殊字符   6-10
        Pattern all_zifu = Pattern.compile("^[`!#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]+$");
        Matcher all_zifu_mc = all_zifu.matcher(pwd);
        //------------------组合------------------
        //   数字 字母 11-20
        Pattern num_abc = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher num_abc_mc = num_abc.matcher(pwd);
        //字母  特殊字符 11-20
        Pattern abc_zifu = Pattern.compile("^[A-Za-z`!#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]+$");
        Matcher abc_zifu_mc = abc_zifu.matcher(pwd);
        //数字  特殊字符  11-20
        Pattern num_zifu = Pattern.compile("^[0-9`!#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]+$");
        Matcher num_zifu_mc = num_zifu.matcher(pwd);

        //字符字母数字
        Pattern num_abc_zifu = Pattern.compile("^[A-Za-z0-9`!#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]+$");
        Matcher num_abc_zifu_mc = num_abc_zifu.matcher(pwd);

        if (pwd.length() >= 6 && pwd.length() <= 20) {
            //纯
            if (all_num_mc.matches() || all_abc_mc.matches() || all_zifu_mc.matches()) {
                if (all_num_mc.matches()) {
                    //纯数字
                    //判断字符长度
                    if (pwd.length() <= 10) {
                        //弱
                        //System.out.println("纯数字-弱");
                        return 1;
                    } else {
                        //中
                        //System.out.println("纯数字-中");
                        return 2;
                    }
                } else if (all_abc_mc.matches()) {
                    //纯字母
                    if (pwd.length() <= 10) {
                        //弱
                        //System.out.println("纯字母-弱");
                        return 1;
                    } else {
                        //中
                        //System.out.println("纯字母-中");
                        return 2;
                    }
                } else if (all_zifu_mc.matches()) {
                    //纯字符
                    if (pwd.length() <= 10) {
                        //弱
                        //System.out.println("纯字符-弱");
                        return 1;
                    } else {
                        //中
                        //System.out.println("纯字符-中");
                        return 2;
                    }
                }
            } else {
                //组合
                if (num_abc_mc.matches()) {
                    //数字 字母
                    if (pwd.length() <= 10) {
                        //System.out.println("数字字母 -中");
                        return 2;
                    } else {
                        //System.out.println("数字字母 -强");
                        return 3;
                    }
                } else if (abc_zifu_mc.matches()) {
                    if (pwd.length() <= 10) {
                        //System.out.println("字母字符-中");
                        return 2;
                    } else {
                        //System.out.println("字母字符-强");
                        return 3;
                    }
                } else if (num_zifu_mc.matches()) {
                    if (pwd.length() <= 10) {
                        //System.out.println("数字字符-中");
                        return 2;
                    } else {
                        //System.out.println("数字字符-强");
                        return 3;
                    }

                } else if (num_abc_zifu_mc.matches()) {
                    //System.err.println("三种组合 -强");
                    return 3;
                }
            }
        } else {
            //不符合条件
            return 0;
        }
        return 0;
    }

    public static String getPasswordLevel(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return "";
        }
        String level = "";
        switch (checkPasswordLevel(pwd)) {
            case 0:
                level = "";
                break;
            case 1:
                level = "弱";
                break;
            case 2:
                level = "中";
                break;
            case 3:
                level = "强";
                break;
        }
        return level;
    }

    public static String getPasswordLevelNum(String level) {
        if (TextUtils.isEmpty(level)) {
            return "";
        }
        if ("弱".equals(level)) {
            return "1";
        } else if ("中".equals(level)) {
            return "2";
        } else if ("强".equals(level)) {
            return "3";
        } else {
            return "";
        }

    }

    //校验一卡通卡号
    public static boolean isOneCardNmber(String str) {
        Pattern p = Pattern.compile("^5656\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches() && str.length() == 12;
    }

    //改变文字颜色
    public static SpannableStringBuilder setWarningColor(Context context, String str, int index, int resource) {
        if (str != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(str);
            ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(resource));
            builder.setSpan(redSpan, index, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }
        return null;
    }

    //改变文字颜色2
    public static SpannableStringBuilder setWarningColor(Context context, String str, int index, int end, int resource) {
        if (str != null) {
            SpannableStringBuilder builder = new SpannableStringBuilder(str);
            ForegroundColorSpan redSpan = new ForegroundColorSpan(context.getResources().getColor(resource));
            builder.setSpan(redSpan, index, str.length() - end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }
        return null;
    }

    //正则表达式判断输入是否为中文
    public static boolean isChinese(String a) {

        String regex = "^[a-zA-Z\u4e00-\u9fa5]+$";
        Matcher matcher = Pattern.compile(regex).matcher(a);
        return matcher.matches();
    }

    // 座机号
    public static boolean isHomeNumber(String str) {
        Pattern p = Pattern.compile("^0\\d{2,3}-?\\d{7,8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 获取当前时间
     */
    public static String getCurrentTimes() {
        String currentTime = "";
        SimpleDateFormat shijian = new SimpleDateFormat("HH:mm:ss");
        currentTime = shijian.format(new java.util.Date());
        return currentTime;
    }

    //单独为了传信推送做的一个时间比较器，仅在xmpp和BaseActivity中使用，firstTime为获取的系统时间，必不为空，第二个有可能为空
    public static boolean timeCompare(String firstTime, String secondTime) {
        String[] saveTimes, nowTimes;
        if (!TextUtils.isEmpty(firstTime)) {
            nowTimes = firstTime.split(":");
            if (!TextUtils.isEmpty(secondTime)) {
                saveTimes = secondTime.split(":");
                LogUtil.e("Hour", new Boolean(saveTimes[0].equals(nowTimes[0])).toString());
                LogUtil.e("Second", new Boolean(saveTimes[1].equals(nowTimes[1])).toString());
                if (saveTimes[0].equals(nowTimes[0]) && saveTimes[1].equals(nowTimes[1])) {
                    int result = Math.abs(strToInt(saveTimes[2]) - strToInt(nowTimes[2]));
                    LogUtil.e("CompareTime", Integer.toString(result));
                    if (result >= 30) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;

    }

    public static boolean isBillNumber(String str) {
        String regex = "^\\d{19}";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        return matcher.matches();
    }

    /**
     * 车牌号码带省份简称
     *
     * @param carnum
     * @return
     */
    public static boolean isCarNumWithProvince(String carnum) {
        Pattern p = Pattern.compile("^[京津冀晋蒙辽吉黑沪苏浙皖闽赣鲁豫鄂湘粤桂琼渝川贵云藏陕甘青宁新]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警军港澳]{1}$");
        Matcher m = p.matcher(carnum);
        return m.matches();
    }

    /**
     * 不带省份简称的车牌号
     *
     * @param carnum
     * @return
     */
    public static boolean isCarNumNoProvince(String carnum) {
        Pattern p = Pattern.compile("^[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警军港澳]{1}$");
        Matcher m = p.matcher(carnum);
        return m.matches();
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo
                    .IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
    /**
     * H5页面是否在webView Layer层面禁用硬件加速
     *
     * @return
     */
    private static boolean shouldDisableHardwareRenderInLayer() {

        // case : samsung on android 4.3 is know to cause crashes at libPowerStretch.so:0x2d4c,目前发现机型有SM-G7106,GT-I9507V,
        // GT-I9500,SM-G3518,SM-N9009
        final boolean isSamsung = (Build.MANUFACTURER != null) && (Build.MANUFACTURER.equalsIgnoreCase
                ("samsung"));
        final boolean isJbMr2 = Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2;
        if (isSamsung && isJbMr2) {
            return true;
        }
        return false;
    }

    /**
     * 在webView Layer层面禁用硬件加速 兼容三星机器
     *
     * @param webView
     */
    public static void disableHardwareRenderInLayer(WebView webView) {
        if (shouldDisableHardwareRenderInLayer()) {
            try {
                if (webView != null) {
                    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
