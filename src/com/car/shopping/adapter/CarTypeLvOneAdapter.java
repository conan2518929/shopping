package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.CarTypeOneInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * Àà×¢ÊÍ
 * 
 * @author Yg
 * @date 2016-4-12
 */
public class CarTypeLvOneAdapter extends CommonAdapter<CarTypeOneInfo> {
	
	
	public CarTypeLvOneAdapter(Context context, List<CarTypeOneInfo> datas, int layoutId) {
		super(context, datas, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, final CarTypeOneInfo bean, final int position) {
		
		holder.setText(R.id.carName, bean.getType_name());
		LinearLayout ll = holder.getView(R.id.ll_one);
		ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		if(position == mposition){
			ll.setBackgroundColor(mContext.getResources().getColor(R.color.gray_seven));
		}
	}

}
