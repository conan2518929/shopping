package com.car.shopping.adapter;

import java.util.HashMap;
import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.BrandsInfo;
import com.car.shopping.entity.RecommendShopsInfo;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultShopListAdapter extends BaseAdapter{
	
	private ResultListClickListener mListener;
	private Context context;
	private LayoutInflater inflater;
	private List<RecommendShopsInfo> lists;
	// 用来控制CheckBox的选中状况  
    private static HashMap<Integer,Boolean> isSelected; 
    
	@SuppressLint("UseSparseArrays")
	public ResultShopListAdapter(Context context,List<RecommendShopsInfo> lists,ResultListClickListener mListener){
		this.context = context;
		this.mListener = mListener;
		this.lists = lists;
		isSelected = new HashMap<Integer, Boolean>(); 
		initDate();
		inflater = LayoutInflater.from(context);
	}
	
	// 初始化isSelected的数据  
    private void initDate(){  
        for(int i=0; i<lists.size();i++) {  
            getIsSelected().put(i,false);  
        }  
    }  
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists==null?0:lists.size();
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
	

	public void changeView(){
		notifyDataSetChanged();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_result, parent, false);
			viewHolder.item_title = (TextView) convertView.findViewById(R.id.item_title);
			viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
			viewHolder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
			viewHolder.tv_talk = (TextView) convertView.findViewById(R.id.tv_talk);
//			viewHolder.img_choose = (ImageView) convertView.findViewById(R.id.img_choose);
			viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
			viewHolder.img_one = (ImageView) convertView.findViewById(R.id.img_one);
			viewHolder.img_two = (ImageView) convertView.findViewById(R.id.img_two);
			viewHolder.img_top = (ImageView) convertView.findViewById(R.id.img_top);
			viewHolder.ll_detail = (LinearLayout) convertView.findViewById(R.id.ll_detail);
			viewHolder.ll_choose = (LinearLayout) convertView.findViewById(R.id.ll_choose);
			viewHolder.radioButton1 = (CheckBox) convertView.findViewById(R.id.radioButton1);
			
			viewHolder.biaoqian_one = (TextView) convertView.findViewById(R.id.biaoqian_one);
			viewHolder.biaoqian_two = (TextView) convertView.findViewById(R.id.biaoqian_two);
			viewHolder.biaoqian_three = (View) convertView.findViewById(R.id.biaoqian_three);
			viewHolder.biaoqian_four = (TextView) convertView.findViewById(R.id.biaoqian_four);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(lists.size() > 0){
			
			RecommendShopsInfo bean = lists.get(position);
			
			if(bean != null){
				viewHolder.biaoqian_one.setVisibility(View.GONE);
				viewHolder.biaoqian_two.setVisibility(View.GONE);
				viewHolder.biaoqian_three.setVisibility(View.GONE);
				viewHolder.biaoqian_four.setVisibility(View.GONE);
				
				if(bean.getHas_activity().length() != 0){
					if(bean.getHas_activity().equals("0")){//是否有减免送
						viewHolder.biaoqian_one.setVisibility(View.GONE);
					}else if( bean.getHas_activity().equals("1")){
						if(bean.getActivity().length() != 0){
							viewHolder.biaoqian_one.setVisibility(View.VISIBLE);
							viewHolder.biaoqian_one.setText(bean.getActivity());
						}
					}
				}
				
				if(bean.getHas_tag().length() != 0){
					if(bean.getHas_tag().equals("0")){//是否有认证
						viewHolder.biaoqian_four.setVisibility(View.GONE);
					}else if(bean.getHas_tag().equals("1")){
						viewHolder.biaoqian_four.setVisibility(View.VISIBLE);
					}
				}
				
				if(bean.getHas_video().length() != 0){
					if(bean.getHas_video().equals("0")){//是否有视频
						viewHolder.biaoqian_two.setVisibility(View.GONE);
						viewHolder.biaoqian_three.setVisibility(View.GONE);
					}else if(bean.getHas_video().equals("1")){
						viewHolder.biaoqian_three.setVisibility(View.VISIBLE);
						viewHolder.biaoqian_two.setVisibility(View.VISIBLE);
					}
				}
				
				viewHolder.radioButton1.setChecked(getIsSelected().get(position)); 
				
				viewHolder.tv_tel.setTag(position);
				viewHolder.tv_tel.setOnClickListener(mListener);
				viewHolder.item_title.setText(bean.getShop_name());
				viewHolder.tv_address.setText(bean.getAddress());
				
//				viewHolder.img_choose.setTag(position);
//				viewHolder.img_choose.setOnClickListener(mListener);
				viewHolder.radioButton1.setTag(position);
				viewHolder.radioButton1.setOnClickListener(mListener);
				viewHolder.tv_talk.setTag(position);
				viewHolder.tv_talk.setOnClickListener(mListener);
				
				viewHolder.ll_detail.setTag(position);
				viewHolder.ll_detail.setOnClickListener(mListener);
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + bean.getImage_url(),viewHolder.img);
				
				List<BrandsInfo> brands = bean.getBrands();
				if(brands != null){
					if(brands.size() > 0){
						viewHolder.img_one.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(0).getBrand(),viewHolder.img_one);
					}
					if(brands.size() > 1){
						viewHolder.img_two.setVisibility(View.VISIBLE);
						ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(1).getBrand(),viewHolder.img_two);
					}
				}
			}
			
			if (position == 0) {
				viewHolder.img_top.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.img_top.setVisibility(View.VISIBLE);
			}
		}
		
		return convertView;
	}
	
	class ViewHolder{
		TextView item_title;
		TextView tv_address;
		TextView tv_tel;
		TextView tv_talk;
//		ImageView img_choose;
		ImageView img;
		ImageView img_one;
		ImageView img_two;
		ImageView img_top;
		CheckBox radioButton1;
		LinearLayout ll_detail;
		LinearLayout ll_choose;
		
		TextView biaoqian_one;
		TextView biaoqian_two;
		View biaoqian_three;
		TextView biaoqian_four;
	}
	
	
	public static abstract class ResultListClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}
	
	public static HashMap<Integer,Boolean> getIsSelected() {  
        return isSelected;  
    }  
  
    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {  
    	ResultShopListAdapter.isSelected = isSelected;  
    }  
    public static boolean getIsSelected(HashMap<Integer,Boolean> isSelected,int position) {
    	isSelected = getIsSelected();
    	return isSelected.get(position);
    }  
  
}







