package com.car.shopping.adapter;

import java.util.List;

import com.car.shopping.R;
import com.car.shopping.constant.Constant;
import com.car.shopping.entity.SearchListInfo;
import com.car.shopping.utils.CommonAdapter;
import com.car.shopping.utils.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.widget.ImageView;

/** 
 * Àà×¢ÊÍ
 * @author Yg
 * @date 2016-4-8
 */
public class CarTypeLvAdapter extends CommonAdapter<SearchListInfo>
{
	
	public CarTypeLvAdapter(Context context, List<SearchListInfo> datas, int layoutId)
	{
		super(context, datas, layoutId);
	}
	
	@Override
	public void convert(ViewHolder holder, final SearchListInfo bean,final int position)
	{
		holder.setText(R.id.tv_car, bean.getName());
		ImageView img = holder.getView(R.id.img_car);
		ImageLoader.getInstance().displayImage(Constant.URL_TEST + bean.getImage_url(),img);
	}
}
