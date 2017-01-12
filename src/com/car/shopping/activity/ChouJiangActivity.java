package com.car.shopping.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseChouJiangInfo;
import com.car.shopping.entity.CJDataInfo;
import com.car.shopping.entity.DataInfo;
import com.car.shopping.entity.ItemInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChouJiangActivity extends BaseActivity{

	private LinearLayout back,cjjl;
	private TextView btn_choujiang,now_jifen,shuoming;
	private DataInfo dataInfo;
	private ScrollView sl;
	private String jifen = "",rule = "";
	private BaseChouJiangInfo baseinfo;
	private CJDataInfo cjInfo;;
	private ItemInfo itemInfo;;
	private boolean hit = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choujiang);
		initViews();
		initData();
	}
	
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		cjjl = getView(R.id.cjjl);
		btn_choujiang = getView(R.id.btn_choujiang);
		now_jifen = getView(R.id.now_jifen);
		shuoming = getView(R.id.shuoming);
		sl = getView(R.id.sl);
	}

	@Override
	public void initData() {
		jifen = getIntent().getExtras().getString("jifen");
		rule = getIntent().getExtras().getString("rule");
		if(rule != null){
			shuoming.setText(rule);
		}
		if(jifen != null){
			now_jifen.setText(jifen);
		}
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppContext.getInstance().cancelPendingRequests(TAG);
				finish();
			}
		});
		cjjl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("jilu", "2");
				 Utils.goOtherWithDataActivity(ChouJiangActivity.this, ExchangeRecordActivity.class, bundle);
			}
		});
		btn_choujiang.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				choujiang();
			}
		});
		sl.smoothScrollTo(0, 20);
	}
	
	/**
	 * 初始化数据
	 * */
	private void choujiang() {
		showLoadingDialog();
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_GET_ITEM_RAND, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("抽奖结果="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ChouJiangActivity.this, "网络访问失败");
			}
		}) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
//				params.put("city_id", cityId);
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
			baseinfo = gson.fromJson(result, BaseChouJiangInfo.class);
			if (baseinfo != null) {
				cjInfo = baseinfo.getData();
				itemInfo = cjInfo.getItem();
				hit = cjInfo.getHit();
				jifen = cjInfo.getPoint();
				now_jifen.setText("当前积分"+jifen);
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.JIFEN, "当前积分"+jifen);
				if(hit == false){
					Utils.showToast(ChouJiangActivity.this, "您未抽中奖品....", 2000);
				}else if(hit == true){
					Bundle bundle = new Bundle();
					bundle.putString("id", cjInfo.getRecord_id());
					Utils.showToast(ChouJiangActivity.this, "恭喜您抽中奖品,请您填写收货人信息....", 3000);
					Utils.goOtherWithDataActivity(ChouJiangActivity.this,AddressActivity.class, bundle);
				}
			}
		} 
		dismissLoadingDialog();
	}
}
