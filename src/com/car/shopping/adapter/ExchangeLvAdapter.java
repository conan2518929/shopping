package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.ExchangeRecordInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExchangeLvAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private List<ExchangeRecordInfo> imgList;

	public ExchangeLvAdapter(Context mContext, List<ExchangeRecordInfo> imgList) {
		this.imgList = imgList;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	public void refresh(List<ExchangeRecordInfo> imgList) {
		this.imgList = imgList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return imgList == null ? 0 : imgList.size();
	}

	@Override
	public Object getItem(int position) {
		return imgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_duihuajilu, null);
			vp.img = (ImageView) convertView.findViewById(R.id.img);
			vp.name = (TextView) convertView.findViewById(R.id.name);
			vp.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		if(imgList.get(position) != null){
			vp.name.setText(imgList.get(position).getItem_name());
			vp.time.setText(imgList.get(position).getRecord_time());
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + imgList.get(position).getItem_pic(), vp.img);
		}
		return convertView;
	}

	class ViewHelp {
		ImageView img;
		TextView name;
		TextView time;
	}
}
