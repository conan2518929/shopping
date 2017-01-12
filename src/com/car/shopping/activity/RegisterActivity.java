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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.StatusMsgInfo;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class RegisterActivity extends BaseActivity implements OnClickListener{

	public String TAG = "RegisterActivity";

	private LinearLayout back;
	private ImageView img_choose;
	private boolean isChoose;
	private TextView hqyzm;
	private Button btn;
	private EditText account,et_yzm,et_pwd,nickname,yaoqingma;
	private String mMobile = "",mYZM = "",mPwd = "",mYaoqingma = "",mNickname = "";
	public String cookieFromResponse;
	private Handler h;
	private int i = 120;
	private int typeNum = 0;
	private StatusMsgInfo statusMsgInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		img_choose = getView(R.id.img_choose);
		hqyzm = getView(R.id.hqyzm);
		btn = getView(R.id.btn);
		account = getView(R.id.account);
		et_yzm = getView(R.id.et_yzm);
		et_pwd = getView(R.id.et_pwd);
		nickname = getView(R.id.nickname);
		yaoqingma = getView(R.id.yaoqingma);
	}
	
	@Override
	public void initData() {
		h = new Handler();
		btn.setOnClickListener(this);
		back.setOnClickListener(this);
		hqyzm.setOnClickListener(this);
		img_choose.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	/**
	 * 解析josn数据
	 * */
	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		statusMsgInfo = gson.fromJson(result, StatusMsgInfo.class);
		if (status == 200) {
			if(typeNum == 0){
				Utils.showText(RegisterActivity.this, "请注意查收验证码...");
			}
			if(typeNum == 1){
				finish();
				Utils.showText(RegisterActivity.this, "注册成功...");
			}
		}else if(status == 500){
			if(statusMsgInfo != null && statusMsgInfo.getStatusMsg().length() != 0){
				Utils.showText(RegisterActivity.this, statusMsgInfo.getStatusMsg());
			}
		}
		dismissLoadingDialog();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hqyzm:
			mMobile = account.getText().toString().trim();
			if (mMobile == null || mMobile.length() != 11) {
				Utils.showToast(RegisterActivity.this, "请正确填写您的手机号码...", 1000);
				return;
			}else{
				
				hqyzm.setClickable(false);
				h.postDelayed(runnable1, 1);
				
				AppContext.getInstance().cancelPendingRequests(TAG);
				StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.YZM, new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
							typeNum = 0;
							dealData(result);
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Utils.showText(RegisterActivity.this, "访问网络失败...");
					}
				}) {

					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> map = new HashMap<>();
						map.put("mobile", mMobile);
						System.out.println("mMobile="+mMobile);
						return map;
					}
				};
				AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
			}
			break;
		case R.id.back:
			finish();
			break;
		case R.id.img_choose:
			if (!isChoose) {
				isChoose = true;
				img_choose.setImageResource(R.drawable.my_wgx);
			} else {
				isChoose = false;
				img_choose.setImageResource(R.drawable.my_gx);
			}
			break;
		case R.id.btn:
			mMobile = account.getText().toString().trim();
			mYZM = et_yzm.getText().toString().trim();
			mPwd = et_pwd.getText().toString().trim();
			mYaoqingma = yaoqingma.getText().toString().trim();
			mNickname = nickname.getText().toString().trim();
			if (mNickname == null || mNickname.length()  > 10 || mNickname.length()  ==0) {
				Utils.showToast(RegisterActivity.this, "请设置10个字以内的昵称...", 1000);
				return;
			}
			if (mMobile == null || mMobile.length() != 11) {
				Utils.showToast(RegisterActivity.this, "请正确填写您的手机号码...", 1000);
				return;
			}
			if (mYZM == null || mYZM.length() != 4) {
				Utils.showToast(RegisterActivity.this, "请您填写验证码...", 1000);
				return;
			}
			if (mPwd == null || mPwd.length() < 6 || mPwd.length() > 10) {
				Utils.showToast(RegisterActivity.this, "密码由6-10位数字或者字母组成...", 1000);
				return;
			}
			
			mPwd = Utils.MD5STR(mPwd);
			
			if(isChoose){
				Utils.showToast(RegisterActivity.this, "请您同意并且阅读配配侠隐私协议", 1000);
				return;
			}
			showLoadingDialog();
			AppContext.getInstance().cancelPendingRequests(TAG);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.REGISTER, new Response.Listener<String>() {

				@Override
				public void onResponse(String result) {
					typeNum = 1;
					dealData(result);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError volleyError) {
					dismissLoadingDialog();
					Utils.showText(RegisterActivity.this, "网络访问失败");
				}
			}) {

				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<>();
					map.put("mobile", mMobile);
					map.put("password", mPwd);
					map.put("verify_code", mYZM);
					map.put("nickname", mNickname);
					map.put("from_code", mYaoqingma);
					return map;
				}
			};
			AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
			break;

		default:
			break;
		}
		
	}
}
