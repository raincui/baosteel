package com.android.baosteel.lan.basebusiness.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Title: UnZipFileUtil.java<br>
 * Description: ZIP解压<br>
 * Create DateTime: 2015年8月12日 下午3:12:58<br>
 */
public class UnZipFileUtil {
	public static void unZipFile(String zipFilePath, String unzipPath) {
		long startTime = System.currentTimeMillis();
		try {
			ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFilePath));// 输入源zip路径
			BufferedInputStream bin = new BufferedInputStream(zin);
			File Fout = null;
			ZipEntry entry;
			try {
				while ((entry = zin.getNextEntry()) != null && !entry.isDirectory()) {
					Fout = new File(unzipPath, entry.getName());
					if (!Fout.exists()) {
						(new File(Fout.getParent())).mkdirs();
					}
					FileOutputStream out = new FileOutputStream(Fout);
					BufferedOutputStream Bout = new BufferedOutputStream(out);
					int b;
					while ((b = bin.read()) != -1) {
						Bout.write(b);
					}
					Bout.close();
					out.close();
				}
				LogUtil.e("解压成功");
				//sendMessage();
				bin.close();
				zin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		LogUtil.e("耗费时间： " + (endTime - startTime) + " ms");
	}

//	private static void sendMessage() {
//		Message msg = Message.obtain();
//		msg.arg1 = Global.UN_ZIP_COMPLETE;
//		Global.updateHandler.sendMessage(msg);
//		// 设置HTML5版本号显示
//		SaveDataGlobal.putString(SaveDataGlobal.HTML5_VERSION,
//				SaveDataGlobal.getString(SaveDataGlobal.HTML5_VERSION_TEMP, GlobalConfig.HTML5_VERSION_VALUE));
//	}
}
