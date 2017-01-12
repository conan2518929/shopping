package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.CarModelInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;

import android.content.Context;

/**
 * Àà×¢ÊÍ
 * 
 * @author Yg
 * @date 2016-4-15
 */
public class CarBrandLvTwoAdapter extends CommonAdapter<CarModelInfo> {
	
	
	public CarBrandLvTwoAdapter(Context context, List<CarModelInfo> datas, int layoutId) {
		super(context, datas, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, final CarModelInfo bean, final int position) {
		
		holder.setText(R.id.tv, bean.getSeries_name());
		
	}

}
