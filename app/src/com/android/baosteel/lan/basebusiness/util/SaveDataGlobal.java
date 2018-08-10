package com.android.baosteel.lan.basebusiness.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Map;


/**
 * SharedPreferences类封装
 *
 */
public class SaveDataGlobal {
    private static SharedPrefAction Obj = new SharedPrefAction();
    public static final String saveDataGrobalFileName = "saveDataGrobal";

    public static final String APK_VERSION = "apkVersion"; // 更新版本
    public static final String UPDATE_APK_VERSION="UPDATE_APK_VERSION";//是否版本升级，决定是否进入引导广告页面

    public static final String MAC_ADDRESS = "macAddress"; // MAC地址
    // REALNAME
    public static final String REALNAME = "realname"; // 真实姓名
    public static final String PartyName = "partyname"; // 会员名

    public static final String LONGIN_TIME = "loginTime"; // 记录上一次登录时间

    public static final String IS_REGISTER = "isRegister";
    public static final String TRANSFAR_COMMINDEX = "transfar_commindex";
    public static final String CIRCLE_NEWBIE = "circle_newbie";
    public static final String UUID = "uuid"; // 当前APP唯一值
    public static final String DELAY_POINT_ARRAY = "DELAY_POINT_ARRAY";

    private static int userId = -1;


    public static void open(Context ct) {
        Obj.open(ct, saveDataGrobalFileName);
    }

    public static void open(Context ct, String packageName) {
        Obj.open(ct, packageName, saveDataGrobalFileName);
    }

    // GRSFZSHBZ 个人身份证审核标志
    // JSZSHBZ 驾驶证审核标志
    // XSZSHBZ 行驶证审核标志
    // 更新标志

    // 清除基础数据
    public static void clearData() {
    }

    public static boolean getBoolean(String key, boolean value) {
        if (Obj != null) {
            return Obj.getBoolean(key, value);
        }
        return false;
    }

    public static boolean putBoolean(String key, boolean value) {
        if (Obj != null) {
            return Obj.putBoolean(key, value);
        }
        return false;
    }

    public static int getInt(String key, int def) {
        if (Obj != null) {
            return Obj.getInt(key, def);
        }
        return 0;
    }

    public static boolean putInt(String key, int value) {
        if (Obj != null) {
            return Obj.putInt(key, value);
        }
        return false;
    }

    public static String getString(String key, String value) {
        if (Obj != null) {
            return Obj.getString(key, value);
        }
        return null;
    }

    public static boolean putString(String key, String value) {
        if (Obj != null) {
            return Obj.putString(key, value);
        }
        return false;
    }

    public static long getLong(String key, Long value) {
        if (Obj != null) {
            return Obj.getLong(key, value);
        }
        return 0;
    }

    public static boolean putLong(String key, Long value) {
        if (Obj != null) {
            return Obj.putLong(key, value);
        }
        return false;
    }

    public static boolean putMap(Map<String, Object> map) {
        if (Obj != null) {
            return Obj.putMap(map);
        }
        return false;
    }

    /**
     * desc:获取保存的Object对象
     *
     * @param key
     * @return modified:
     */
    public static Object readObject(String key) {
        try {
            if (Obj.contains(key)) {
                String string = Obj.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    // 将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    // 返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data
     * @return modified:
     */
    private static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch; // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); // //两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16; // // 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; // // A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); // /两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); // // 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; // // A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;// 将转化后的数放入Byte里
        }
        return retData;
    }

    /**
     * desc:保存对象
     *
     * @param key
     * @param obj 要保存的对象，只能保存实现了serializable的对象 modified:
     */
    public static boolean saveObject(String key, Object obj) {
        try {
            // 先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            // 将对象序列化写入byte缓存
            os.writeObject(obj);
            // 将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            // 保存该16进制数组
            return Obj.putString(key, bytesToHexString);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("", "保存obj失败");
        }
        return false;
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static void setUserId(int userId) {
        SaveDataGlobal.userId = userId;
    }

    public static int getUserId(){
        return SaveDataGlobal.userId;
    }
}
