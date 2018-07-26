package com.android.baosteel.lan.basebusiness.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Title: InputUtil.java Description: 输入框工具类 <br/>
 * Create DateTime: 2014年6月23日 上午9:52:02 <br/>
 * 
 */
public class InputUtil {
	/**
	 * 判断输入框弹出
	 * 
	 * @param window
	 * @param content
	 */
	public static void getInput(Window window, Context content) {
		// 判断隐藏软键盘是否弹出
		if (window.getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED) {
			// 隐藏软键盘
			// InputType.TYPE_CLASS_NUMBER
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		} else {
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		}
	}

	// 自动调用软键盘
	public static void showInput(final View textView) {
		textView.setFocusable(true);
		textView.setFocusableInTouchMode(true);
		textView.requestFocus();
		// mSearchEdit.setInputType(InputType.TYPE_CLASS_NUMBER); //弹出数字键盘
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) textView.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				if (inputManager != null) {
					inputManager.showSoftInput(textView, 0);
				}
			}
		}, 100);
	}


	public static void hideSoftInput(IBinder token,Context context) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
