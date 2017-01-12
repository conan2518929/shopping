package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.CarTypeOneInfo;
import com.car.shopping.entity.DXInfo;
import com.car.shopping.entity.FindByTypeOneInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Àà×¢ÊÍ
 * 
 * @author Yg
 * @date 2016-4-12
 */
public class FindByTypeOneAdapter extends CommonAdapter<DXInfo> {
	
	
	public FindByTypeOneAdapter(Context context, List<DXInfo> datas, int layoutId) {
		super(context, datas, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, final DXInfo bean, final int position) {
		
		holder.setText(R.id.tv, bean.getPart_type_name());
		RelativeLayout ll = holder.getView(R.id.rl);
		ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		if(position == mposition){
			ll.setBackgroundColor(mContext.getResources().getColor(R.color.gray_five));
		}
	}

}
