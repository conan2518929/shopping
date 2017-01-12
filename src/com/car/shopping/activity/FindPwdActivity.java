package com.car.shopping.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;

public class FindPwdActivity extends BaseActivity{

	private LinearLayout back;
	private Button btn;
	private EditText account,et_yzm;
	private Handler h;
	private int i = 120;
	private TextView hqyzm;
	private String mMobile = "",mYZM = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwd);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		btn = getView(R.id.btn);
		account = getView(R.id.account);
		hqyzm = getView(R.id.hqyzm);
		et_yzm = getView(R.id.et_yzm);
	}

	@Override
	public void initData() {
		
		h = new Handler();
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMobile = account.getText().toString().trim();
				mYZM = et_yzm.getText().toString().trim();
				if (mMobile.length() != 11) {
					Utils.showToast(FindPwdActivity.this, "请正确填写您的手机号码...", 1000);
					return;
				}
				if (mYZM.length() != 4) {
					Utils.showToast(FindPwdActivity.this, "请填写您的验证码...", 1000);
					return;
				}
				Bundle data = new Bundle();
				data.putString("mobile", mMobile);
				data.putString("verify_code", mYZM);
				Utils.goOtherWithDataActivity(FindPwdActivity.this, SetPwdActivity.class, data);
			}
		});
		
		hqyzm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMobile = account.getText().toString().trim();
				if (mMobile == null || mMobile.length() != 11) {
					Utils.showToast(FindPwdActivity.this, "请正确填写您的手机号码...", 1000);
					return;
				}else{
					
					hqyzm.setClickable(false);
					h.postDelayed(runnable1, 1);
					showLoadingDialog();
					AppContext.getInstance().cancelPendingRequests(TAG);
					StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.YZM, new Response.Listener<String>() {

						@Override
						public void onResponse(String result) {
								dealData(result);
								dismissLoadingDialog();
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError volleyError) {
							dismissLoadingDialog();
							Utils.showText(FindPwdActivity.this, "网络访问失败");
						}
					}) {

						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String, String> map = new HashMap<>();
							map.put("mobile", mMobile);
							return map;
						}
					};
					AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
				}
			}
		});
	}
	
	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			Utils.showText(FindPwdActivity.this, "请注意查收验证码...");
		}
	}
	
	Runnable runnable1 = new Runnable() {

		@Override
		public void run() {

			hqyzm.setText("重新获取(" + (i - 1) + "s)");
			i--;
			if (i == -1) {
				h.removeCallbacks(runnable1);
				hqyzm.setClickable(true);
				hqyzm.setText("获取验证码");
				i = 120;
				return;
			}
			h.postDelayed(this, 1000);
		}
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
	}
}
