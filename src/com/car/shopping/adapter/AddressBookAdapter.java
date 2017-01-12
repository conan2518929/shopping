package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.AddressBookInfo;
import com.car.shopping.widget.SliderView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddressBookAdapter extends BaseAdapter{
	
	private MyClickListenerOne mListenerOne;
	private List<AddressBookInfo> list;
	private Context context;
	private LayoutInflater inflater;
	
	public AddressBookAdapter(Context context,List<AddressBookInfo> list,MyClickListenerOne mListenerOne){
		this.context = context;
		this.list = list;
		this.mListenerOne = mListenerOne;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list==null?0:list.size();
	}

	@Override
	public AddressBookInfo getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void refresh(List<AddressBookInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		SliderView slideView = (SliderView) convertView;
		if (slideView == null) {
			View itemView = inflater.inflate(R.layout.addressbook_time, null);
			slideView = new SliderView(context);
			slideView.setContentView(itemView);
			holder = new ViewHolder(slideView);
			slideView.setTag(holder);
		} else {
			holder = (ViewHolder) slideView.getTag();
		}
		
		AddressBookInfo item = list.get(position);
		slideView.shrink();
		int num = item.getBrands().size();
		holder.img_one.setVisibility(View.GONE);
		holder.img_two.setVisibility(View.GONE);
		holder.img_three.setVisibility(View.GONE);
		holder.tv_four.setVisibility(View.GONE);
		if(num > 0){
			holder.img_one.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + item.getBrands().get(0).getBrand(),holder.img_one);
		}
		if(num > 1){
			holder.img_two.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + item.getBrands().get(1).getBrand(),holder.img_two);
		}
		if(num > 2){
			holder.img_three.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(Constant.URL_TEST + item.getBrands().get(2).getBrand(),holder.img_three);
		}
		if(num > 3){
			holder.tv_four.setVisibility(View.VISIBLE);
		}
		holder.item_title.setText(item.getShop_name());
		holder.tv_time.setText(item.getCall_time());
		holder.tv_address.setText(item.getAddress());
		holder.deleteHolder.setOnClickListener(mListenerOne);
		holder.deleteHolder.setTag(position);
		holder.tv_tel.setOnClickListener(mListenerOne);
		holder.tv_tel.setTag(position);
		holder.tv_talk.setOnClickListener(mListenerOne);
		holder.tv_talk.setTag(position);
		return slideView;
	}
	
	private class ViewHolder{
		ViewGroup deleteHolder;
		TextView item_title;
		TextView tv_address;
		TextView tv_time;
		TextView tv_tel;
		TextView tv_four;
		TextView tv_talk;
		ImageView img_one;
		ImageView img_two;
		ImageView img_three;
		ViewHolder(View view) {
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
			item_title = (TextView) view.findViewById(R.id.item_title);
			tv_tel = (TextView) view.findViewById(R.id.tv_tel);
			tv_time = (TextView) view.findViewById(R.id.tv_time);
			tv_address = (TextView) view.findViewById(R.id.tv_address);
			tv_talk = (TextView) view.findViewById(R.id.tv_talk);
			tv_four = (TextView) view.findViewById(R.id.tv_four);
			img_one = (ImageView) view.findViewById(R.id.img_one);
			img_two = (ImageView) view.findViewById(R.id.img_two);
			img_three = (ImageView) view.findViewById(R.id.img_three);
		}
	}
	
	public static abstract class MyClickListenerOne implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
	
	
}