package com.android.baosteel.lan.basebusiness.util;

import android.util.Log;

public class LogUtil {

    private static final String TAG = "baosteel";
    private static boolean DEBUG = false;

    public static final String NULL_TIPS = "Log with null object";
    public static final String PARAM = "Param";
    public static final String NULL = "null";

    public static final int V = 0x1;
    public static final int D = 0x2;
    public static final int I = 0x3;
    public static final int W = 0x4;
    public static final int E = 0x5;

    public static void setDebugMode(boolean isDebug) {
        DEBUG = isDebug;
    }

    public static void LOGE(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(E, null, tag);
        } else {
            printLog(E, tag, objects);
        }
    }

    public static void LOGW(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(W, null, tag);
        } else {
            printLog(W, tag, objects);
        }
    }

    public static void LOGI(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(I, null, tag);
        } else {
            printLog(I, tag, objects);
        }
    }

    public static void LOGD(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(D, null, tag);
        } else {
            printLog(D, tag, objects);
        }
    }

    public static void LOGV(String tag, Object... objects) {
        if (objects == null || objects.length == 0) {
            printLog(V, null, tag);
        } else {
            printLog(V, tag, objects);
        }
    }


    private static void printLog(int type, String tagStr, Object... objects) {

        if (!DEBUG) {
            return;
        }

        String[] contents = wrapperContent(tagStr, objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        switch (type) {
            case V:
            case D:
            case I:
            case W:
            case E:
        }
        printDefault(type, tag, headString + msg);
    }

    private static String[] wrapperContent(String tagStr, Object... objects) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int index = 5;
        String className = stackTrace[index].getFileName();
        String methodName = stackTrace[index].getMethodName();
        int lineNumber = stackTrace[index].getLineNumber();
        String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ (").append(className).append(":").append(lineNumber).append(")#").append(methodNameShort).append(" ] ");

        String tag = (tagStr == null ? className : tagStr);
        String msg = (objects == null) ? NULL_TIPS : getObjectsString(objects);
        String headString = stringBuilder.toString();

        return new String[]{tag, msg, headString};
    }


    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(NULL).append("\n");
                } else {
                    stringBuilder.append(PARAM).append("[").append(i).append("]").append(" = ").append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? NULL : object.toString();
        }
    }


    private static void printDefault(int type, String tag, String msg) {

        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                printSub(type, tag, sub);
                index += maxLength;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case V:
                Log.v(tag, sub);
                break;
            case D:
                Log.d(tag, sub);
                break;
            case I:
                Log.i(tag, sub);
                break;
            case W:
                Log.w(tag, sub);
                break;
            case E:
                Log.e(tag, sub);
                break;
        }
    }


    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg + "");
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg + "");
        }
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, msg + "", throwable);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg + "");
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg + "");
        }
    }

    public static void i(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.i(tag, msg + "", throwable);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg + "");
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg + "");
        }
    }
}
