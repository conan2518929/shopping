package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.UserNumInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserNumberAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater inflater;
	private List<UserNumInfo> list;

	public UserNumberAdapter(Context mContext, List<UserNumInfo> list) {
		this.list = list;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	public void refresh(List<UserNumInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHelp vp = null;
		if (convertView == null) {
			vp = new ViewHelp();
			convertView = inflater.inflate(R.layout.item_usernumber, null);
			vp.name = (TextView) convertView.findViewById(R.id.name);
			vp.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(vp);
		} else {
			vp = (ViewHelp) convertView.getTag();
		}
		vp.name.setText(list.get(position).getUser_name());
		vp.time.setText(list.get(position).getMobile());
		return convertView;
	}

	class ViewHelp {
		TextView name;
		TextView time;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}