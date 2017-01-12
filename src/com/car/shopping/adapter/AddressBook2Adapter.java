package com.car.shopping.adapter;

import java.util.List;
import java.util.Map;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddressBook2Adapter extends BaseAdapter{
	
	private MyClickListener mListener;
	private String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private Context context;
	private LayoutInflater inflater;
	private List<String> letterToCity;
	private final static int TYPE_1 = 1;
	private final static int TYPE_2 = 2;
	private int mposition = 1;
	private Map<String,AddressBookInfo>maps;
	
	public AddressBook2Adapter(Context context,List<String> letterToCity,Map<String,AddressBookInfo>maps,MyClickListener mListener){
		this.context = context;
		this.maps = maps;
		this.letterToCity = letterToCity;
		this.mListener = mListener;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return letterToCity==null?0:letterToCity.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return letterToCity.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void refresh(List<String> letterToCity,Map<String,AddressBookInfo>maps) {
		this.letterToCity = letterToCity;
		this.maps = maps;
		notifyDataSetChanged();
	}
	
	@Override
	public int getItemViewType(int position) {
		for (int i = 0; i < letter.length; i++) {
			if (letterToCity.get(position).equals(letter[i])) {
				return TYPE_1;
			}
		}
		return TYPE_2;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	public void setPosition(int position){
		this.mposition = position;
	}
	
	public void changeView(){
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ViewHolder_letter holderLetter = null;
		SliderView slideView = (SliderView) convertView;
		View itemView = null;
		int type = getItemViewType(position);
		if (slideView == null) {
			switch (type) {
			case TYPE_1:
				itemView = inflater.inflate(R.layout.letter_txl, null);
				slideView = new SliderView(context,1);
				slideView.setContentView(itemView);
				holderLetter = new ViewHolder_letter(slideView);
				slideView.setTag(holderLetter);
				break;
			case TYPE_2:
				itemView = inflater.inflate(R.layout.addressbook_txl, null);
				slideView = new SliderView(context);
				slideView.setContentView(itemView);
				holder = new ViewHolder(slideView);
				slideView.setTag(holder);
				break;
			default:
				break;
			}
		} else {
			switch (type) {
			case TYPE_1:
				holderLetter = (ViewHolder_letter) slideView.getTag();
				break;
			case TYPE_2:
				holder = (ViewHolder) slideView.getTag();
				break;
			default:
				break;
			}
		}
		switch (type) {
		case TYPE_1:
			holderLetter.tv_zimu.setText(letterToCity.get(position));
			break;
		case TYPE_2:
			final AddressBookInfo item = maps.get(letterToCity.get(position));
			slideView.shrink();
			if(item != null){
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
			holder.title.setText(item.getUnify_name());
			holder.tv_address.setText(item.getAddress());
			holder.deleteHolder.setOnClickListener(mListener);
			holder.deleteHolder.setTag(position);
			
			holder.tv_tel.setOnClickListener(mListener);
			holder.tv_tel.setTag(position);
			
			holder.tv_talk.setOnClickListener(mListener);
			holder.tv_talk.setTag(position);
			
			holder.tv_beizhu.setOnClickListener(mListener);
			holder.tv_beizhu.setTag(position);
			}
			break;
		default:
			break;
		}
		
		return slideView;
	}
	
	private class ViewHolder{
		ViewGroup deleteHolder;
		TextView title;
		TextView tv_tel;
		TextView tv_address;
		TextView tv_four;
		TextView tv_talk;
		ImageView img_one;
		ImageView img_two;
		ImageView img_three;
		TextView tv_beizhu;
		ViewHolder(View view) {
			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
			title = (TextView) view.findViewById(R.id.item_title);
			tv_tel = (TextView) view.findViewById(R.id.tv_tel);
			tv_address = (TextView) view.findViewById(R.id.tv_address);
			tv_four = (TextView) view.findViewById(R.id.tv_four);
			tv_talk = (TextView) view.findViewById(R.id.tv_talk);
			img_one = (ImageView) view.findViewById(R.id.img_one);
			img_two = (ImageView) view.findViewById(R.id.img_two);
			img_three = (ImageView) view.findViewById(R.id.img_three);
			tv_beizhu =  (TextView) view.findViewById(R.id.tv_beizhu);
			
		}
	}
	private class ViewHolder_letter{
		TextView tv_zimu;
		RelativeLayout rl_letter;
		ViewHolder_letter(View view) {
			tv_zimu = (TextView) view.findViewById(R.id.letterTextView);
			rl_letter = (RelativeLayout) view.findViewById(R.id.rl_letter);
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







