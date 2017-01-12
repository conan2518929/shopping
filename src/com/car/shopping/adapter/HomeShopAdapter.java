package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.activity.ShopDetailActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.RecommendShopsInfo;
import com.car.shopping.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeShopAdapter extends BaseAdapter {

	private MyClickListener mListener;
	private Context context;
	private LayoutInflater inflater;
	private List<RecommendShopsInfo> lists;

	public HomeShopAdapter(Context context, List<RecommendShopsInfo> lists, MyClickListener mListener) {
		this.context = context;
		this.lists = lists;
		this.mListener = mListener;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists == null ? 0 : lists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void refresh(List<RecommendShopsInfo> lists) {
		this.lists = lists;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp holder = null;
		if (convertView == null) {
			holder = new ViewHelp();
			convertView = inflater.inflate(R.layout.home_item, parent, false);
			holder.ll_detail = (LinearLayout) convertView.findViewById(R.id.ll_detail);
			holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
			holder.shop_address = (TextView) convertView.findViewById(R.id.shop_address);
			holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
			holder.tv_talk = (TextView) convertView.findViewById(R.id.tv_talk);
			holder.img_top = (ImageView) convertView.findViewById(R.id.img_top);
			holder.shop_tx = (ImageView) convertView.findViewById(R.id.shop_tx);
			holder.img_one = (ImageView) convertView.findViewById(R.id.img_one);
			holder.img_two = (ImageView) convertView.findViewById(R.id.img_two);

			holder.biaoqian_one = (TextView) convertView.findViewById(R.id.biaoqian_one);
			holder.biaoqian_two = (TextView) convertView.findViewById(R.id.biaoqian_two);
			holder.biaoqian_three = (View) convertView.findViewById(R.id.biaoqian_three);
			holder.biaoqian_four = (TextView) convertView.findViewById(R.id.biaoqian_four);

			convertView.setTag(holder);
		} else {
			holder = (ViewHelp) convertView.getTag();
		}
		final RecommendShopsInfo info = lists.get(position);
		ImageLoader.getInstance().displayImage(Constant.URL_TEST + info.getImage_url(), holder.shop_tx);
		holder.img_one.setVisibility(View.GONE);
		holder.img_two.setVisibility(View.GONE);
		holder.biaoqian_one.setVisibility(View.GONE);
		holder.biaoqian_two.setVisibility(View.GONE);
		holder.biaoqian_three.setVisibility(View.GONE);
		holder.biaoqian_four.setVisibility(View.GONE);

		if (info.getHas_activity().length() != 0) {
			if (info.getHas_activity().equals("0")) {// 是否有减免送
				holder.biaoqian_one.setVisibility(View.GONE);
			} else if (info.getHas_activity().equals("1")) {
				if (info.getActivity().length() != 0) {
					holder.biaoqian_one.setVisibility(View.VISIBLE);
					holder.biaoqian_one.setText(info.getActivity());
				}
			}
		}
		
		if(info.getHas_tag().length() != 0){
			if (info.getHas_tag().equals("0")) {// 是否有认证
				holder.biaoqian_four.setVisibility(View.GONE);
			} else if (info.getHas_tag().equals("1")) {
				holder.biaoqian_four.setVisibility(View.VISIBLE);
			}
		}
		
		if(info.getHas_video().length() != 0){
			if (info.getHas_video().equals("0")) {// 是否有视频
				holder.biaoqian_two.setVisibility(View.GONE);
				holder.biaoqian_three.setVisibility(View.GONE);
			} else if (info.getHas_video().equals("1")) {
				holder.biaoqian_three.setVisibility(View.VISIBLE);
				holder.biaoqian_two.setVisibility(View.VISIBLE);
			}
		}
		
		if (info.getBrands().size() > 0) {
			String urlOne = info.getBrands().get(0).getBrand();
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + urlOne, holder.img_one);
			holder.img_one.setVisibility(View.VISIBLE);
		}
		if (info.getBrands().size() > 1) {
			String urlTwo = info.getBrands().get(1).getBrand();
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + urlTwo, holder.img_two);
			holder.img_two.setVisibility(View.VISIBLE);
		}
		
		holder.item_title.setText(info.getShop_name());
		holder.shop_address.setText(info.getAddress());
		
		holder.tv_tel.setOnClickListener(mListener);
		holder.tv_tel.setTag(position);
		holder.tv_talk.setOnClickListener(mListener);
		holder.tv_talk.setTag(position);
		if (position == 0) {
			holder.img_top.setVisibility(View.INVISIBLE);
		} else {
			holder.img_top.setVisibility(View.VISIBLE);
		}
		holder.ll_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String id = lists.get(position).getId();
				if (id.length() > 0) {
					Bundle data = new Bundle();
					data.putString("shop_id", id);
					Utils.goOtherWithDataActivity(context, ShopDetailActivity.class, data);
				}
			}
		});
		return convertView;
	}

	private class ViewHelp {
		TextView tv_tel;
		TextView item_title;
		TextView shop_address;
		TextView tv_talk;
		LinearLayout ll_detail;
		ImageView img_top;
		ImageView shop_tx;
		ImageView img_one;
		ImageView img_two;

		TextView biaoqian_one;
		TextView biaoqian_two;
		View biaoqian_three;
		TextView biaoqian_four;
	}

	public static abstract class MyClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
}
