package com.car.shopping.activity;

import java.io.File;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.FileUtils;
import com.car.shopping.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RuleActivity extends BaseActivity {

	private LinearLayout back;
	private File file;
	private long mCache;
	private TextView content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule);
		
		initViews();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		content = getView(R.id.content);
		
	}

	public void clear(View v){
		file = FileUtils.getImageFileList(RuleActivity.this);
		try {
			mCache = Utils.getFolderSize(file);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String str = Utils.getValueDouble(mCache) + "M";
		System.out.println("缓存大小="+str);
		ImageLoader imageLoader = ImageLoader.getInstance();
//		Utils.deleteFilesByDirectory(file);
		imageLoader.clearMemoryCache(); // 清除内存缓存
		imageLoader.clearDiscCache();
	}
	
	@Override
	public void initData() {
		content.setText(AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.RULE,""));
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
//				Utils.deleteFilesByDirectory(file);
			}
		});

	}

}
