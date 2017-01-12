package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.PartListInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;

import android.content.Context;

/**
 * Àà×¢ÊÍ
 * 
 * @author Yg
 * @date 2016-4-12
 */
public class FindByTypeTwoAdapter extends CommonAdapter<PartListInfo> {
	
	
	public FindByTypeTwoAdapter(Context context, List<PartListInfo> datas, int layoutId) {
		super(context, datas, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, final PartListInfo bean, final int position) {
		
		holder.setText(R.id.tv, bean.getPart_name());
		
	}

}
