package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.activity.CancelActivity;
import com.car.shopping.activity.ShopDetailActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.BrandsInfo;
import com.car.shopping.entity.HomeLvItemInfo;
import com.car.shopping.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PayStatusListAdapter extends BaseAdapter{
	
	private PayStatusClickListenerOne mListener;
	private List<HomeLvItemInfo> list;
	private Context context;
	private LayoutInflater inflater;
	private int type;
	
	public PayStatusListAdapter(Context context,List<HomeLvItemInfo> list,int type,PayStatusClickListenerOne mListener){
		this.context = context;
		this.list = list;
		this.type = type;
		this.mListener = mListener;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list==null?0:list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void refresh(List<HomeLvItemInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewCancel holder = null;
		if (convertView == null) {
			holder = new ViewCancel();
			convertView = inflater.inflate(R.layout.item_ispay, parent,false);
			holder.rl_one = (RelativeLayout) convertView.findViewById(R.id.rl_one);
			holder.rl_two = (RelativeLayout) convertView.findViewById(R.id.rl_two);
			holder.rl_three = (RelativeLayout) convertView.findViewById(R.id.rl_three);
			holder.rl_four = (RelativeLayout) convertView.findViewById(R.id.rl_four);
			
			holder.qxdd = (TextView) convertView.findViewById(R.id.qxdd);
			holder.qqsh = (TextView) convertView.findViewById(R.id.qqsh);
			holder.qrfk = (TextView) convertView.findViewById(R.id.qrfk);
			holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
			holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
			holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
			holder.scdd = (TextView) convertView.findViewById(R.id.scdd);
			holder.tv_wait = (TextView) convertView.findViewById(R.id.tv_wait);
			
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.img_one = (ImageView) convertView.findViewById(R.id.img_one);
			holder.img_two = (ImageView) convertView.findViewById(R.id.img_two);
			holder.img_three = (ImageView) convertView.findViewById(R.id.img_three);
			holder.img_top = (ImageView) convertView.findViewById(R.id.img_top);
			
			holder.ll_detail = (LinearLayout) convertView.findViewById(R.id.ll_detail);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewCancel) convertView.getTag();
		}
		
		List<BrandsInfo>brands = list.get(position).getBrands();
		
		if(brands.size() > 0){
			holder.img_one.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(0).getBrand(),holder.img_one);
		}
		if(brands.size() > 1){
			holder.img_two.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(1).getBrand(),holder.img_two);
		}
		if(brands.size() > 2){
			holder.img_three.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(1).getBrand(),holder.img_three);
		}
		
		holder.item_title.setText(list.get(position).getShop_name());
		holder.tv_address.setText(list.get(position).getAddress());
		holder.tv_money.setText(list.get(position).getPay_amount());
		holder.tv_time.setText(list.get(position).getCreate_time());
		
		
		holder.tv_tel.setTag(position);
		holder.tv_tel.setOnClickListener(mListener);
		
		holder.ll_detail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String id = list.get(position).getShop_id();
				Bundle data = new Bundle();
				data.putString("shop_id", id);
				Utils.goOtherWithDataActivity(context, ShopDetailActivity.class, data);
			}
		});
		
		if(type == 1){
			
			holder.qrfk.setOnClickListener(mListener);
			holder.qrfk.setTag(position);
			holder.scdd.setOnClickListener(mListener);
			holder.scdd.setTag(position);
			
			holder.rl_one.setVisibility(View.VISIBLE);
			holder.rl_two.setVisibility(View.GONE);
			holder.rl_three.setVisibility(View.GONE);
			holder.rl_four.setVisibility(View.GONE);
			
		}else if(type == 2){
			holder.tv_wait.setVisibility(View.GONE);
			holder.qqsh.setVisibility(View.GONE);
			holder.qxdd.setVisibility(View.GONE);
			
			holder.rl_one.setVisibility(View.GONE);
			holder.rl_two.setVisibility(View.VISIBLE);
			holder.rl_three.setVisibility(View.GONE);
			holder.rl_four.setVisibility(View.GONE);
			
			holder.qqsh.setOnClickListener(mListener);
			holder.qqsh.setTag(position);
			
			String status = list.get(position).getStatus();
			holder.tv_wait.setVisibility(View.GONE);
			holder.qxdd.setVisibility(View.GONE);
			if(status.equals("-2")){
				holder.tv_wait.setVisibility(View.VISIBLE);
				holder.qqsh.setVisibility(View.VISIBLE);
			}else if(status.equals("1")||status.equals("2")){
				holder.qxdd.setOnClickListener(mListener);
				holder.qxdd.setTag(position);
				holder.qxdd.setVisibility(View.VISIBLE);
				holder.qqsh.setVisibility(View.VISIBLE);
			}
			
		}else if(type == 3){
			holder.rl_one.setVisibility(View.GONE);
			holder.rl_two.setVisibility(View.GONE);
			holder.rl_three.setVisibility(View.VISIBLE);
			holder.rl_four.setVisibility(View.GONE);
		}else if(type == 4){
			holder.rl_one.setVisibility(View.GONE);
			holder.rl_two.setVisibility(View.GONE);
			holder.rl_three.setVisibility(View.GONE);
			holder.rl_four.setVisibility(View.VISIBLE);
		}
		
		if (position == 0) {
			holder.img_top.setVisibility(View.INVISIBLE);
		} else {
			holder.img_top.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	
	private class ViewCancel{
		RelativeLayout rl_one;
		RelativeLayout rl_two;
		RelativeLayout rl_three;
		RelativeLayout rl_four;
		TextView qrfk;
		TextView qqsh;
		TextView qxdd;
		TextView scdd;
		TextView item_title;
		TextView tv_address;
		TextView tv_money;
		TextView tv_time;
		TextView tv_wait;
		ImageView img;
		ImageView img_one;
		ImageView img_two;
		ImageView img_three;
		ImageView img_top;
		TextView tv_tel;
		LinearLayout ll_detail;
	}
	
	public static abstract class PayStatusClickListenerOne implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
}
