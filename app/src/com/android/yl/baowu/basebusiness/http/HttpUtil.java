package com.android.yl.baowu.basebusiness.http;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ruisong.li 2015/11/16
 * @Title: HttpUtil
 *               2015/11/16
 */
public class HttpUtil {
	public static  final  int 	RESULT_SUCCSESS = 200; //成功
	public static final int 	RESULT_SERVER_ERROR = 500;//服务器错误
	public static final int 	RESULT_JSON_EXCEPTION = 501;//json 异常
	public static final int 	RESULT_NET_UNREACHABLE = 502;//网络不可达(注意：非网络不可用)

	public static void resultToEntity(BaseResponse entity, String result) {
		try {
			HttpSerializer
					.deserializeJSONObject(entity, new JSONObject(result));
		} catch (JSONException e) {
			Log.w(HttpUtil.class.getSimpleName(), e.getMessage());
			entity.setHttpStatus(RESULT_JSON_EXCEPTION);
			entity.setMsg("服务器异常");
		}
	}
}
