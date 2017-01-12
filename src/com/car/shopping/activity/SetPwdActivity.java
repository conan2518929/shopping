package com.car.shopping.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.AppManager;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;

public class SetPwdActivity extends BaseActivity {

	private LinearLayout back;
	private Button btn;
	private EditText pwd,repwd;
	private String mMobile = "",password = "",mPassword = "",verify_code= "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setpwd);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		btn = getView(R.id.btn);
		pwd = getView(R.id.pwd);
		repwd = getView(R.id.repwd);
	}

	@Override
	public void initData() {
		
		mMobile = getIntent().getExtras().getString("mobile");
		verify_code = getIntent().getExtras().getString("verify_code");
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				password = pwd.getText().toString().trim();
				mPassword = repwd.getText().toString().trim();
				if(!password.equals(mPassword)){
					Utils.showText(SetPwdActivity.this, "两次输入的密码不一致...");
					return;
				}
				if(mMobile.length() != 0 && verify_code.length() != 0 && password.length() != 0 && mPassword.length() != 0){
					password = Utils.MD5STR(password);
					AppContext.getInstance().cancelPendingRequests(TAG);
					showLoadingDialog();
					StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.RESET_PWD, new Response.Listener<String>() {

						@Override
						public void onResponse(String result) {
								dealData(result);
								dismissLoadingDialog();
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError volleyError) {
							dismissLoadingDialog();
							Utils.showText(SetPwdActivity.this, "网络访问失败");
						}
					}) {
						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String, String> map = new HashMap<>();
							map.put("mobile", mMobile);
							map.put("password", password);
							map.put("verify_code", verify_code);
							return map;
						}
					};
					AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
				}else{
					Utils.showText(SetPwdActivity.this, "新密码不能为空...");
				}
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
	}
	
	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			Utils.showText(SetPwdActivity.this, "设置密码成功,请您重新登录...");
			finish();
			AppManager.getAppManager().finishActivity(FindPwdActivity.class);
		}
	}
}
