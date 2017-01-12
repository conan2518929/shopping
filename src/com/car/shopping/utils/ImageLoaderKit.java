package com.car.shopping.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.car.shopping.R;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ImageLoaderKit {

    private static final String TAG = ImageLoaderKit.class.getSimpleName();

    private static final int M = 1024 * 1024;

    private Context context;

    public ImageLoaderKit(Context context, ImageLoaderConfiguration config) {
        this.context = context;
        init(config);
    }

    private void init(ImageLoaderConfiguration config) {
        try {
            ImageLoader.getInstance().init(config == null ? getDefaultConfig() : config);
        } catch (IOException e) {
        	Log.e(TAG, "init ImageLoaderKit error, e=" + e.getMessage().toString());
        }

        Log.i(TAG, "init ImageLoaderKit completed");
    }

    public void clear() {
        ImageLoader.getInstance().clearMemoryCache();
    }

    @SuppressWarnings("deprecation")
	private ImageLoaderConfiguration getDefaultConfig() throws IOException {
        int MAX_CACHE_MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, context.getPackageName() + "/cache/image/");

        Log.i(TAG, "ImageLoader memory cache size = " + MAX_CACHE_MEMORY_SIZE / M + "M");
        Log.i(TAG, "ImageLoader disk cache directory = " + cacheDir.getAbsolutePath());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(3) 
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAX_CACHE_MEMORY_SIZE))
                .discCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 0))
//               .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .defaultDisplayImageOptions(initDisplayOptions(true))
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)瓒呮椂鏃堕棿
                .writeDebugLogs()
                .build();

        return config;
    }
    
    public static DisplayImageOptions initDisplayOptions(boolean isShowDefault) {
		DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
		// 设置图片缩放方式
		// EXACTLY: 图像将完全按比例缩小的目标大小
		// EXACTLY_STRETCHED: 图片会缩放到目标大小
		// IN_SAMPLE_INT: 图像将被二次采样的整数倍
		// IN_SAMPLE_POWER_OF_2: 图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
		// NONE: 图片不会调整
		displayImageOptionsBuilder.imageScaleType(ImageScaleType.EXACTLY);
		if (isShowDefault) {
			// 默认显示的图片
			displayImageOptionsBuilder.showStubImage(R.drawable.failure);
			// 地址为空的默认显示图片
			displayImageOptionsBuilder.showImageForEmptyUri(R.drawable.failure);
			// 加载失败的显示图片
			displayImageOptionsBuilder.showImageOnFail(R.drawable.failure);
		}
		// 开启内存缓存
		displayImageOptionsBuilder.cacheInMemory(true);
		// 开启SDCard缓存
		displayImageOptionsBuilder.cacheOnDisc(true);
		// 设置图片的编码格式为RGB_565，此格式比ARGB_8888快
		displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);

		return displayImageOptionsBuilder.build();
	}

}
