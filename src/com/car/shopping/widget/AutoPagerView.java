package com.car.shopping.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.car.shopping.R;
import com.car.shopping.adapter.UstorePagerAdpater;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AutoPagerView extends FrameLayout implements OnPageChangeListener{

	private Context mContent;
	private ViewPager mViewPager;
	private List<View> picPagelist;
	private int curItemPic = 0;
	private LinearLayout lineary;
	private List<ImageView> pointImg;
	public static int num;
	
	/**
	 * 轮播类型 0为首页轮播；1为信息分享轮播（两种轮播图显示的点不同）
	 */
	private int type = 0;
	
	private Timer timer = new Timer();
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			mViewPager.setCurrentItem(curItemPic);
		};
	};
	
	public AutoPagerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context,attrs);
	}

	/*加载布局文件时执行*/
	public AutoPagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context,attrs);
	}

	public AutoPagerView(Context context) {
		super(context);
		init(context,null);
	}

	public void init(Context context, AttributeSet attrs){
		Log.e("ustore", "intit");
		this.mContent = context;
		
		pointImg = new ArrayList<ImageView>();
		
		FrameLayout frameContainer = new FrameLayout(mContent);
		LayoutParams containerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		addView(frameContainer, containerParams); 
		
		mViewPager = new ViewPager(mContent);	
		frameContainer.addView(mViewPager);
		
		
		lineary = new LinearLayout(mContent);
		
		LayoutParams LinearyParams = new LayoutParams(LayoutParams.MATCH_PARENT, 	
		LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
		lineary.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
		lineary.setPadding(0, 0, 0, 15);
		frameContainer.addView(lineary,LinearyParams);
	}

	public void changeCurPontImg(int curPos){
		for(int i = 0; i < picPagelist.size(); i++){
			
				if(i == curPos){
					pointImg.get(i).setBackgroundResource(R.drawable.home_tyd);
				}else{
					pointImg.get(i).setBackgroundResource(R.drawable.home_yd);
				}	
//			else if(type == 1){
//				if (i == curPos) {
//					pointImg.get(i).setBackgroundResource(R.drawable.select4_y);
//				} else {
//					pointImg.get(i).setBackgroundResource(R.drawable.select4_n);
//				}
//			}
	}
}

	public void setPageViewPics(List<View> picPageList,int type){
		this.type = type;
		timer.cancel();
		timer.purge();
		timer = new Timer();
		curItemPic = 0;
		this.picPagelist = picPageList;
		mViewPager.setAdapter(new UstorePagerAdpater(this.picPagelist));
		mViewPager.setOnPageChangeListener(this);
		lineary.removeAllViews();
		pointImg.clear();
		for(int i = 0; i < picPageList.size(); i++){
			ImageView imageview= new ImageView(mContent);
			imageview.setBackgroundResource(R.drawable.home_yd);
			pointImg.add(imageview);
//			imageview.setPadding(20, 0, 20, 0);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 10, 20, 0);
			imageview.setLayoutParams(params);
			lineary.addView(imageview);
		}
		mViewPager.setCurrentItem(0);
		pointImg.get(0).setBackgroundResource(R.drawable.home_tyd);
		
		if(type == 1){
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(picPagelist.size() == 0){
					return;
				}else{
					synchronized (mViewPager) {
						curItemPic = (curItemPic + 1)%picPagelist.size();
						handler.obtainMessage().sendToTarget();
					}
				}
				
			}
		}, 3000,3000);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		num = arg0;
	}

	@Override
	public void onPageSelected(int position) {

		this.curItemPic = position;
		changeCurPontImg(position);
	}

	public Timer getTimer(){
		return timer;
	}

}
