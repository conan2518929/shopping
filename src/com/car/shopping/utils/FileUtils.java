package com.car.shopping.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtils {

	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();
		} else {
			return "/mnt/sdcard";
		}
		return sdDir.getAbsolutePath().toString();
	}

	@SuppressLint("SimpleDateFormat")
	public static String getFileName(Context context) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return "IMG_" + format.format(date);
	}

	public static void createPath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	public static File getImageFile(Context context) {
		Log.i("MSG??", "SD---" + getSDPath());
		if (getSDPath() == null) {
			return null;
		}
//		createPath(getSDPath() + "/com.car.shopping");
		createPath(getSDPath() + "/pictures");
		createPath(getSDPath() + "/pictures"+ "/shopping");
//		createPath(getSDPath() + "/com.car.shopping" + "/pictures");
//		return new File(getSDPath() + "/com.car.shopping" + "/pictures", getFileName(context) + ".jpg");
		return new File(getSDPath() +"/pictures"+ "/shopping", getFileName(context) + ".jpg");
	}
	public static File getImageFile1(Context context) {
		Log.i("MSG??", "SD---" + getSDPath());
		if (getSDPath() == null) {
			return null;
		}
		createPath(getSDPath() + "/com.car.shopping");
		createPath(getSDPath() + "/com.car.shopping" + "/image");
		return new File(getSDPath() + "/com.car.shopping" + "/image", "head_image.jpg");
	}
	public static File getImageFileList(Context context) {
		Log.i("MSG??", "SD---" + getSDPath());
		if (getSDPath() == null) {
			return null;
		}
		createPath(getSDPath() + "/com.car.shopping");
		createPath(getSDPath() + "/com.car.shopping" + "/image");
		return new File(getSDPath() + "/com.car.shopping" + "/image");
	}
}
