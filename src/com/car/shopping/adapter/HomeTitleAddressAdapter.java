package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.entity.CityListInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;

import android.content.Context;

/** 
 * Àà×¢ÊÍ
 * @author Yg
 * @date 2016-4-8
 */
public class HomeTitleAddressAdapter extends CommonAdapter<CityListInfo>
{
	
	public HomeTitleAddressAdapter(Context context, List<CityListInfo> datas, int layoutId)
	{
		super(context, datas, layoutId);
	}
	
	@Override
	public void convert(ViewHolder holder, final CityListInfo bean,final int position)
	{
		holder.setText(R.id.tv_address, bean.getName());
		if(mDatas.size() == position+1){
			holder.setLLBackGround(R.id.ll, R.drawable.home_title_address);
		}else{
			holder.setLLBackGround(R.id.ll, R.drawable.home_title_address_full);
		}
	}
}
