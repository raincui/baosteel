package com.android.yl.baowu.basebusiness.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences封装
 */
public class SharedPrefAction {
    private static SharedPreferences Obj = null;

    public static void open(Context ct, String fileName) {

        Obj = ct.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void open(Context ct, String packageName, String fileName) {
        Context appContext = null;
        try {
            appContext = ct.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
            Obj = appContext.getSharedPreferences(fileName, Context.MODE_WORLD_READABLE);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static boolean contains(String key) {
        if (Obj != null) {
            return Obj.contains(key);
        }
        return false;
    }

    public static boolean getBoolean(String key, boolean value) {
        if (Obj != null) {
            return Obj.getBoolean(key, value);
        }
        return false;
    }

    public static boolean putBoolean(String key, boolean value) {
        if (Obj != null) {
            SharedPreferences.Editor editor = Obj.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    public static int getInt(String key, int value) {
        if (Obj != null) {
            return Obj.getInt(key, value);
        }
        return 0;
    }

    public static boolean putInt(String key, int value) {
        if (Obj != null) {
            SharedPreferences.Editor editor = Obj.edit();
            editor.putInt(key, value);
            return editor.commit();
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
            SharedPreferences.Editor editor = Obj.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    public static long getLong(String key, Long value) {
        if (Obj != null) {
            return Obj.getLong(key, value);
        }
        return (Long) null;
    }

    public static boolean putLong(String key, Long value) {
        if (Obj != null) {
            SharedPreferences.Editor editor = Obj.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    public static boolean putMap(Map<String, Object> map) {
        if (Obj != null && map != null) {
            SharedPreferences.Editor editor = Obj.edit();
            for (String key : map.keySet()) {
                Object obj = map.get(key);
                if (obj instanceof String) {
                    editor.putString(key, obj.toString());
                } else if (obj instanceof Integer) {
                    editor.putInt(key, (Integer) obj);
                } else if (obj instanceof Long) {
                    editor.putLong(key, (Long) obj);
                } else if (obj instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) obj);
                } else if (obj instanceof Float) {
                    editor.putFloat(key, (Float) obj);
                } else {
                    if (null == obj) {
                        editor.putString(key, null);
                    } else {
                        throw new IllegalArgumentException("illegal object type:" + obj);
                    }
                }
            }
            return editor.commit();
        }
        return false;
    }

    public static void putKeyword(String key, String keyword) {
        if (TextUtils.isEmpty(keyword)) return;
        Set<String> set = Obj.getStringSet(key, new HashSet<String>());
        set.add(keyword);
        SharedPreferences.Editor editor = Obj.edit();
        editor.putStringSet(key, set).commit();
    }

    public static void cleanKeyWord(String key, boolean isCleanAll, String keyword) {
        SharedPreferences.Editor editor = Obj.edit();
        if (isCleanAll) {
            editor.remove(key).commit();
            return;
        }
        if (TextUtils.isEmpty(keyword)) return;
        Set<String> set = Obj.getStringSet(key, new HashSet<String>());
        set.remove(keyword);
        editor.putStringSet(key, set).commit();
    }

    public static List<String> getKeywords(String key) {
        Set<String> set = Obj.getStringSet(key, new HashSet<String>());
        return new ArrayList<>(set);
    }

    public static void remove(String key) {
        SharedPreferences.Editor editor = Obj.edit();
        editor.remove(key).apply();
    }
}
