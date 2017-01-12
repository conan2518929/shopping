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
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs()
                .build();

        return config;
    }
    
    public static DisplayImageOptions initDisplayOptions(boolean isShowDefault) {
		DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
		// ����ͼƬ���ŷ�ʽ
		// EXACTLY: ͼ����ȫ��������С��Ŀ���С
		// EXACTLY_STRETCHED: ͼƬ�����ŵ�Ŀ���С
		// IN_SAMPLE_INT: ͼ�񽫱����β�����������
		// IN_SAMPLE_POWER_OF_2: ͼƬ������2����ֱ����һ���ٲ��裬ʹͼ���С��Ŀ���С
		// NONE: ͼƬ�������
		displayImageOptionsBuilder.imageScaleType(ImageScaleType.EXACTLY);
		if (isShowDefault) {
			// Ĭ����ʾ��ͼƬ
			displayImageOptionsBuilder.showStubImage(R.drawable.failure);
			// ��ַΪ�յ�Ĭ����ʾͼƬ
			displayImageOptionsBuilder.showImageForEmptyUri(R.drawable.failure);
			// ����ʧ�ܵ���ʾͼƬ
			displayImageOptionsBuilder.showImageOnFail(R.drawable.failure);
		}
		// �����ڴ滺��
		displayImageOptionsBuilder.cacheInMemory(true);
		// ����SDCard����
		displayImageOptionsBuilder.cacheOnDisc(true);
		// ����ͼƬ�ı����ʽΪRGB_565���˸�ʽ��ARGB_8888��
		displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);

		return displayImageOptionsBuilder.build();
	}

}
