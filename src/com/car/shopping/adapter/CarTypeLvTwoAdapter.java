package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.CarNameInfo;
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
public class CarTypeLvTwoAdapter extends CommonAdapter<CarNameInfo> {
	
	public CarTypeLvTwoAdapter(Context context, List<CarNameInfo> datas, int layoutId) {
		super(context, datas, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, final CarNameInfo bean, final int position) {
		
		holder.setText(R.id.carName, bean.getBrand_name());
		LinearLayout ll = holder.getView(R.id.ll_two);
		ll.setBackgroundColor(mContext.getResources().getColor(R.color.gray_seven));
		if(position == mposition){
			ll.setBackgroundColor(mContext.getResources().getColor(R.color.gray_five));
		}
	}

}
