package com.android.baosteel.lan.basebusiness.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片处理
 * 
 */
public class BitmapAction {
	public static Bitmap getBitmapFromFile(String sFile) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeFile(sFile);
		} catch (OutOfMemoryError e) {
		}
		return bm;
	}

	public static Bitmap getBitmapFromFile(String sFile, int w) {
		Bitmap bm = null;
		BitmapFactory.Options opts = null;
		if (w > 0) {
			opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(sFile, opts);

			int width = opts.outWidth;
			int height = opts.outHeight;
			if (width > 0 && height > 0) {
				if (width < w) {
					opts.inSampleSize = computeSampleSize(opts, -1, width * height);
					opts.inJustDecodeBounds = false;
					opts.inInputShareable = true;
					opts.inPurgeable = true;
					try {
						bm = BitmapFactory.decodeFile(sFile, opts);
					} catch (OutOfMemoryError e) {
					}
				} else {
					int newWidth = w;
					int newHeight = height * w / width;

					final int minSideLength = Math.min(newWidth, newHeight);
					opts.inSampleSize = computeSampleSize(opts, minSideLength, newWidth * newHeight);
					opts.inJustDecodeBounds = false;
					opts.inInputShareable = true;
					opts.inPurgeable = true;
					try {
						bm = BitmapFactory.decodeFile(sFile, opts);
					} catch (OutOfMemoryError e) {
					}
				}
			}
		}
		return bm;
	}

	// 质量压缩
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 质量压缩方法，把压缩后的数据存放到baos中
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 80;
		// 循环判断如果压缩后图片是否大于300kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 300) {
			baos.reset();// 重置baos即清空baos
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;// 每次都减少10
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(isBm, null, null);
		} catch (OutOfMemoryError e) {
			bitmap = image;
		}
		// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 质量压缩
	public static Bitmap compressBmpToFile(Bitmap bmp, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int options = 80;// 个人喜欢从80开始,
		bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		while (baos.toByteArray().length / 1024 > 100) {
			baos.reset();
			options -= 10;
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w) {
		Bitmap bm = bitmap;
		if (bitmap != null && w > 0) {
			if (bitmap.getWidth() > w) {
				Bitmap BitmapOrg = bitmap;
				int width = BitmapOrg.getWidth();
				int height = BitmapOrg.getHeight();
				int newWidth = w;
				int newHeight = height * w / width;
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				Matrix matrix = new Matrix();
				matrix.postScale(scaleWidth, scaleHeight);
				try {
					bm = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
				} catch (OutOfMemoryError e) {
				}
			}
		}
		return bm;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static void saveBitmap(String bitName, Bitmap bmp) {
		File f = new File(bitName);

		try {
			f.createNewFile();
			FileOutputStream fOut = null;
			fOut = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
		}
	}

	public static String [] SaveImage(Bitmap bm, String name, String[] arrayList,int p) {
	   File f = new File(name);
	   try {
		  FileOutputStream out = new FileOutputStream(f);
		  bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
		  out.flush();
		  out.close();
		  arrayList[p]=f.getAbsolutePath();
		  return arrayList;
	   } catch (FileNotFoundException e) {
		  e.printStackTrace();
	   } catch (IOException e) {
		  e.printStackTrace();
	   }
	   return arrayList;
    }
}
