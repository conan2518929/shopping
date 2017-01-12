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
import com.car.shopping.adapter.EndGVAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseTJYJInfo;
import com.car.shopping.entity.DataInfo;
import com.car.shopping.entity.JiFenInfo;
import com.car.shopping.entity.ListInfo;
import com.car.shopping.entity.LunBoTuInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.MyGridView;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.AutoPagerView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
/*
 * 积分商城
 * */
public class IntegralMallActivity extends BaseActivity {
	
	private LinearLayout back, jfgz,ll_jfcj,ll_jfqd;
	private ScrollView sl;
	private int num = 0;
	private AutoPagerView pagerView;
	private List<LunBoTuInfo> lunbotu;
	private TextView jifen,dhjl;
	private BaseTJYJInfo baseInfo;
	private DataInfo dataInfo;
	private EndGVAdapter endGvAdapter;
	private MyGridView gv;
	private List<ListInfo> item_list;
	private int has_checked_in = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mall);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		sl = getView(R.id.sl);
		jfgz = getView(R.id.jfgz);
		pagerView = getView(R.id.auto_pagerview);
		jifen = getView(R.id.jifen);
		dhjl = getView(R.id.dhjl);
		gv = getView(R.id.gv);
		ll_jfcj = getView(R.id.ll_jfcj);
		ll_jfqd = getView(R.id.ll_jfqd);
	}

	@Override
	protected void onResume() {
		super.onResume();
		jifen.setText(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.JIFEN, "当前积分"+0));
	}

	@Override
	public void initData() {
		
		lunbotu = new ArrayList<>();
		item_list = new ArrayList<>();
		ListInfo info = new ListInfo();
		endGvAdapter = new EndGVAdapter(IntegralMallActivity.this,item_list);
		gv.setAdapter(endGvAdapter);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppContext.getInstance().cancelPendingRequests(TAG);
				finish();
			}
		});
		
		ll_jfqd.setOnClickListener(new OnClickListener() {//积分未签到
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(has_checked_in == 0){
					qiandao();
				}else if(has_checked_in == 1){
					Utils.showText(IntegralMallActivity.this, "今天已经签到过,请您明天再来...");
				}
			}
		});
		
		jfgz.setOnClickListener(new OnClickListener() {//积分规则

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("detail", dataInfo.getPoint_rule());
				 Utils.goOtherWithDataActivity(IntegralMallActivity.this, JiFenRuleActivity.class, bundle);
			}
		});
		
		dhjl.setOnClickListener(new OnClickListener() {//兑换记录
			
			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString("jilu", "1");
				 Utils.goOtherWithDataActivity(IntegralMallActivity.this, ExchangeRecordActivity.class, bundle);
			}
		});
		
		ll_jfcj.setOnClickListener(new OnClickListener() {//积分抽奖
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("jifen", AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.JIFEN, "当前积分"+0));
				bundle.putString("rule", dataInfo.getChoujiang_rule());
				Utils.goOtherWithDataActivity(IntegralMallActivity.this, ChouJiangActivity.class, bundle);
			}
		});
		initActivityData();
		sl.smoothScrollTo(0, 20);
	}

	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		if (num == 0) {
			final int[] imgs = new int[] { R.drawable.failure_lunbotu, R.drawable.failure_lunbotu };
			for (int i = 0; i < imgs.length; i++) {
				ImageView imageview1 = new ImageView(IntegralMallActivity.this);
				imageview1.setScaleType(ScaleType.FIT_XY);
				imageview1.setImageResource(imgs[i]);
				pagePic.add(imageview1);
			}
		} else if(num > 0) {
			for (int i = 0; i < num; i++) {
				ImageView imageview = new ImageView(IntegralMallActivity.this);
				imageview.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + lunbotu.get(i).getImage(), imageview);
				
				imageview.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String shopId = "";
						shopId = lunbotu.get(AutoPagerView.num).getShop_id();
						Bundle data = new Bundle();
						data.putString("shop_id", shopId);
						Utils.goOtherWithDataActivity(IntegralMallActivity.this,ShopDetailActivity.class,data);	
					}
				});
				pagePic.add(imageview);
			}
		}
		pagerView.setPageViewPics(pagePic, 1);
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
					System.out.println("积分商城初始化列表"+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(IntegralMallActivity.this, "网络访问失败");
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
				lunbotu = dataInfo.getPoint_lunbotu();
				if(lunbotu != null){
					num = lunbotu.size();
				}
				item_list = dataInfo.getItem_list();
				endGvAdapter.refresh(item_list);
				initGallery();
				has_checked_in = dataInfo.getHas_checked_in();
				jifen.setText("我的积分" + dataInfo.getPoint());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.JIFEN, "当前积分"+dataInfo.getPoint());
				sl.smoothScrollTo(0, 20);
			}
		} else if(status == 300){
			logout();
		}
		dismissLoadingDialog();
	}
	
	private void logout() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGOUT, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealLogoutShop(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(IntegralMallActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealLogoutShop(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			Intent intent = new Intent();
			intent.setAction("com.app.action.broadcast");
			sendBroadcast(intent);
			Intent i = new Intent(IntegralMallActivity.this, LoginActivity.class);
			startActivity(i);
			Utils.showText(IntegralMallActivity.this, "由于您长时间未操作....请您重新登录账号...");
			finish();
		}
	}
	
	private void qiandao() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_GET_POINT_CHECK_IN, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("签到结果==="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealQD(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(IntegralMallActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealQD(String result) {
		Gson gson = new Gson();
		JiFenInfo statusInfo = gson.fromJson(result, JiFenInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			has_checked_in = 1;
			Utils.showText(IntegralMallActivity.this, "签到成功...");
			jifen.setText("我的积分" + statusInfo.getPoint());
			AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.JIFEN, "当前积分"+statusInfo.getPoint());
		}
	}
	
}
