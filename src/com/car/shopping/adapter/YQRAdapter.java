package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.Recommend_RecordInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class YQRAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private Context mContext;
	private List<Recommend_RecordInfo>data;
	
	public YQRAdapter(Context mContext, List<Recommend_RecordInfo> data) {
		this.mContext = mContext;
		this.data = data;
		inflater = LayoutInflater.from(mContext);
	}

	public void refresh(List<Recommend_RecordInfo> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_yqr, parent, false);
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.num = (TextView) convertView.findViewById(R.id.num);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(data.get(position) !=null){
			viewHolder.name.setText(data.get(position).getUser_name());
			viewHolder.num.setText(data.get(position).getPoint());
			viewHolder.time.setText(data.get(position).getRegister_time());
		}
	
		return convertView;
	}
	
	private class ViewHolder {
		TextView name;
		TextView time;
		TextView num;
	}
}
