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
import com.car.shopping.adapter.YQRAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseTJYJInfo;
import com.car.shopping.entity.DataInfo;
import com.car.shopping.entity.Recommend_RecordInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.car.shopping.utils.WXManager;
import com.car.shopping.widget.MyListView;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RecommendActivity extends BaseActivity{
	private LinearLayout back,hdgz;
	private Button btn_tj;
	private String[] arr = new String[] { "分享到朋友圈", "分享给好友" };
	private MyListView myLV;
	private ScrollView sl;
	private YQRAdapter adapter;
	private List<Recommend_RecordInfo>data;
	private BaseTJYJInfo baseInfo;
//	private TextView my_code;
	private DataInfo dataInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommend);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		btn_tj = getView(R.id.btn_tj);
		back = getView(R.id.back);
		myLV = getView(R.id.lv);
		sl =  getView(R.id.sl);
//		my_code =  getView(R.id.my_code);
		hdgz =  getView(R.id.hdgz);
	}

	@Override
	public void initData() {
		data = new ArrayList<>();
		adapter = new YQRAdapter(RecommendActivity.this,data);
		myLV.setAdapter(adapter);
		// TODO Auto-generated method stub
		btn_tj.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					AlertDialog.Builder builder1 = new Builder(RecommendActivity.this);
					builder1.setItems(arr, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int position) {
							// 分享朋友
							if (position == 0) {
								shared(1);
								dialog.dismiss();
							} // 分享给好友
							else if (position == 1) {
								shared(0);
								dialog.dismiss();
							}
						}
					});
					builder1.create().show();
				}else {
					Utils.showText(RecommendActivity.this, "请您登录后进行聊天...");
				}
			}
		});
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppContext.getInstance().cancelPendingRequests(TAG);
				finish();
			}
		});
		hdgz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		initActivityData();
	}
	
	private void shared(int num) {
		String my_code = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.MY_CODE, "");
		if(my_code.length() == 0){
			Toast.makeText(RecommendActivity.this, "请您登录后邀请好友...", 1000).show();
			return ;
		}
		if (!WXManager.instance().isWXAppInstalled()) {
			Utils.showToast(RecommendActivity.this, "您还未安装微信客户端...", 1000);
			return;
		}
		 Bitmap thumb1 = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
		 WXManager.instance().sendReq(WXManager.instance().getReq(Constant.URL_TEST+"user_register?from_code="+my_code,null, "配配侠邀请您", thumb1, num));
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
					System.out.println("推荐有奖初始化列表"+result.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(RecommendActivity.this, "网络访问失败");
			}
		}) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				String cityId = AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.CHOOSE_CITY, "210100");
				params.put("city_id", "cityId");
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
				data.clear();
				data = dataInfo.getRecommend_record();
				adapter.refresh(data);
//				my_code.setText(dataInfo.getPoint()+"积分");
				sl.smoothScrollTo(0, 20);
			}
		} 
		dismissLoadingDialog();
	}
}
