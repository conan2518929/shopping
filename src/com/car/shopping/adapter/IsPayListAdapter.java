package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.HomeLvItemInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 类注释
 * 
 * @author Yg
 * @date 2016-4-8
 */
public class IsPayListAdapter extends CommonAdapter<HomeLvItemInfo> {
	
	private MyClickListener mListener;
	private int type;
	
	public IsPayListAdapter(Context context, List<HomeLvItemInfo> datas, int layoutId,int type,MyClickListener mListener) {
		super(context, datas, layoutId);
		this.mListener = mListener;
		this.type = type;
	}
	
	@Override
	public void convert(ViewHolder holder, final HomeLvItemInfo bean, final int position) {
		
		RelativeLayout rl_one = holder.getView(R.id.rl_one);
		RelativeLayout rl_two = holder.getView(R.id.rl_two);
		RelativeLayout rl_three = holder.getView(R.id.rl_three);
		
		LinearLayout ll_detail = holder.getView(R.id.ll_detail);
		ll_detail.setOnClickListener(mListener);
		ll_detail.setTag(position);
		
		TextView qxdd = holder.getView(R.id.qxdd);//取消订单
		qxdd.setOnClickListener(mListener);
		qxdd.setTag(position);
		TextView scdd = holder.getView(R.id.scdd);//删除订单
		scdd.setOnClickListener(mListener);
		scdd.setTag(position);
		
		holder.setText(R.id.item_title,bean.getShop_name());
		holder.setText(R.id.tv_address,bean.getAddress());
		holder.setText(R.id.tv_money,bean.getPay_amount());
		holder.setText(R.id.tv_time,bean.getCreate_time());
		
		ImageView img = holder.getView(R.id.img);
		ImageLoader.getInstance().displayImage(Constant.URL_TEST + bean.getImage_url(),img);
		if(bean.getBrands().size() > 0){
			ImageView img_one = holder.getView(R.id.img_one);
			img_one.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + bean.getBrands().get(0).getBrand(),img_one);
		}
		if(bean.getBrands().size() > 1){
			ImageView img_two = holder.getView(R.id.img_two);
			img_two.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + bean.getBrands().get(1).getBrand(),img_two);
		}
		if(bean.getBrands().size() > 2){
			ImageView img_three = holder.getView(R.id.img_three);
			img_three.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + bean.getBrands().get(1).getBrand(),img_three);
		}
		
		TextView tv = holder.getView(R.id.tv_tel);
		tv.setOnClickListener(mListener);
		tv.setTag(position);
		
		TextView tv_talk = holder.getView(R.id.tv_talk);
		tv_talk.setOnClickListener(mListener);
		tv_talk.setTag(position);
		
		if(type == 1){
			rl_one.setVisibility(View.VISIBLE);
			rl_two.setVisibility(View.GONE);
			rl_three.setVisibility(View.GONE);
		}else if(type == 2){
			TextView qrfk = holder.getView(R.id.qrfk);//确认付款
			qrfk.setOnClickListener(mListener);
			qrfk.setTag(position);
			
			rl_one.setVisibility(View.GONE);
			rl_two.setVisibility(View.VISIBLE);
			rl_three.setVisibility(View.GONE);
		}else if(type == 3){
			rl_one.setVisibility(View.GONE);
			rl_two.setVisibility(View.GONE);
			rl_three.setVisibility(View.VISIBLE);
		}
		
		final ImageView img_top = (ImageView) (holder.getView(R.id.img_top));
		if (position == 0) {
			img_top.setVisibility(View.INVISIBLE);
		} else {
			img_top.setVisibility(View.VISIBLE);
		}
	}

	public static abstract class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
}
