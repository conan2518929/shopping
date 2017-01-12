package com.car.shopping.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class UstorePagerAdpater extends PagerAdapter{

	private List<View> pagerView;
	
	public UstorePagerAdpater(List<View> pagerView){
		this.pagerView = new ArrayList<View>();
		this.pagerView = pagerView;
	}
	
	@Override
	public int getCount() {
		return pagerView==null?0:pagerView.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View v, int position, Object object) {
		((ViewPager)v).removeView(pagerView.get(position));
	}
	
	@Override
	public void finishUpdate(View container) {
	}

	@Override
	public Object instantiateItem(View v, int position) {
		((ViewPager)v).addView(pagerView.get(position));
		return pagerView.get(position);
	}
}
