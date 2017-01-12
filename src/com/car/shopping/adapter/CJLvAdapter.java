package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.ChoujiangRecordInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CJLvAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater inflater;
	private List<ChoujiangRecordInfo> imgList;

	public CJLvAdapter(Context mContext, List<ChoujiangRecordInfo> imgList) {
		this.imgList = imgList;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	public void refresh(List<ChoujiangRecordInfo> imgList) {
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
			convertView = inflater.inflate(R.layout.item_choujiang, null);
			vp.name = (TextView) convertView.findViewById(R.id.name);
			vp.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		ChoujiangRecordInfo info = imgList.get(position);
		if( info != null){
			if(info.getItem_id().equals("0")){
				vp.name.setText(info.getItem_name());
			}else{
				vp.name.setText("�齱��ã�"+info.getItem_name());
			}
				vp.time.setText(info.getRecord_time());
		}
		return convertView;
	}

	class ViewHelp {
		TextView name;
		TextView time;
	}
}
