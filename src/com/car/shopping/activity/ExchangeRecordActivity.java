package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.CJLvAdapter;
import com.car.shopping.adapter.ExchangeLvAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseTJYJInfo;
import com.car.shopping.entity.ChoujiangRecordInfo;
import com.car.shopping.entity.DataInfo;
import com.car.shopping.entity.ExchangeRecordInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.MyListView;
import com.google.gson.Gson;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * 兑换记录
 * */
public class ExchangeRecordActivity extends BaseActivity{
	
	private ScrollView sl;
	private LinearLayout back;
	private ExchangeLvAdapter adapter;
	private CJLvAdapter cjAdapter;
	private List<ExchangeRecordInfo> imgList;
	private List<ChoujiangRecordInfo> CjList;
	private MyListView lv;
	private BaseTJYJInfo baseInfo;
	private DataInfo dataInfo;
	private String jilu = "1";
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		lv = getView(R.id.lv);
		sl = getView(R.id.sl);
		title = getView(R.id.title);
	}

	@Override
	public void initData() {
		jilu = getIntent().getExtras().getString("jilu");
		if(jilu != null){
			if(jilu.equals("1")) {
				title.setText("兑换记录");
				imgList = new ArrayList<>();
				adapter = new ExchangeLvAdapter(ExchangeRecordActivity.this,imgList);
				lv.setAdapter(adapter);
			}else if(jilu.equals("2")){
				title.setText("抽奖记录");
				CjList = new ArrayList<>();
				cjAdapter = new CJLvAdapter(ExchangeRecordActivity.this,CjList);
				lv.setAdapter(cjAdapter);
			}
		}
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppContext.getInstance().cancelPendingRequests(TAG);
				finish();
			}
		});
		initActivityData();
	}

	/**
	 * 初始化数据
	 * */
	private void initActivityData() {
		showLoadingDialog();
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_POINT_SHOP_INITIAL, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("兑换列表初始化列表"+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ExchangeRecordActivity.this, "网络访问失败");
			}
		}) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				String cityId = AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.CHOOSE_CITY, "210100");
				System.out.println("积分商城传递的城市id="+cityId);
				params.put("city_id", cityId);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseTJYJInfo.class);
			if (baseInfo != null) {
				dataInfo = baseInfo.getData();
				if (jilu.equals("1")) {
					imgList = dataInfo.getExchange_record();
					adapter.refresh(imgList);
				}else if(jilu.equals("2")){
					CjList = dataInfo.getChoujiang_record();
					cjAdapter.refresh(CjList);
				}
				sl.smoothScrollTo(0, 20);
			}
		} 
		dismissLoadingDialog();
	}
	
}
