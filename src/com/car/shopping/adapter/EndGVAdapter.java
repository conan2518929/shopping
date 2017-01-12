package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.activity.IntegralMallActivity;
import com.car.shopping.activity.JiFenDetailActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.ListInfo;
import com.car.shopping.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
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

public class EndGVAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private List<ListInfo> imgList;

	public EndGVAdapter(Context mContext, List<ListInfo> imgList) {
		this.imgList = imgList;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	public void refresh(List<ListInfo> imgList) {
		this.imgList = imgList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return imgList == null ? 0 : imgList.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_gv_end, null);
			vp.img = (ImageView) convertView.findViewById(R.id.img);
			vp.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			vp.tv_jifen = (TextView) convertView.findViewById(R.id.tv_jifen);
			vp.detail = (LinearLayout) convertView.findViewById(R.id.detail);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		
		vp.tv_name.setText(imgList.get(position).getItem_name());
		vp.tv_jifen.setText(imgList.get(position).getPoint()+"积分");
		ImageLoader.getInstance().displayImage(Constant.URL_TEST + imgList.get(position).getItem_pic(),vp.img);
		vp.detail.setOnClickListener(new OnClickListener() {//物品详情
			
			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("data", imgList.get(position));
				Utils.goOtherWithDataActivity(mContext, JiFenDetailActivity.class, bundle);
			}
		});
		return convertView;
	}

	class ViewHelp {
		ImageView img;
		TextView tv_name;
		TextView tv_jifen;
		LinearLayout detail;
	}

	@Override
	public Object getItem(int position) {
		return imgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}