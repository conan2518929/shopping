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
import com.car.shopping.entity.BaseUserInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.UserInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.db.DemoDBManager;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class WelcomeActivity extends BaseActivity {

	private final String TAG = "WelcomeActivity";
	private static boolean firstEnter = true; // 是否首次进入
	private BaseUserInfo baseInfo;
	private UserInfo userInfo;
	private boolean progressShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (firstEnter) {
			firstEnter = false;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					if (!AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.PHONEONE, "").equals("")) {
						if (!EaseCommonUtils.isNetWorkConnected(WelcomeActivity.this)) {
							Toast.makeText(WelcomeActivity.this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
							Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
							// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
							// Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
							finish();
						} else {
							showLoadingDialog();
							AppContext.getInstance().cancelPendingRequests(TAG);
							StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGIN, new Response.Listener<String>() {

								@Override
								public void onResponse(String result) {
//									try {
//										JSONObject json = new JSONObject(result);
//										System.out.println("登录返回结果=" + json.toString());
//									} catch (JSONException e) {
//										e.printStackTrace();
//									}
									dealData(result);
								}
							}, new Response.ErrorListener() {

								@Override
								public void onErrorResponse(VolleyError volleyError) {
									dismissLoadingDialog();
									Utils.showText(WelcomeActivity.this, "网络访问失败");
									AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
									Intent intent = new Intent();
									intent.setAction("com.app.action.broadcast");
									sendBroadcast(intent);
									Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
									startActivity(i);
									finish();
								}
							}) {

								@Override
								protected Map<String, String> getParams() throws AuthFailureError {
									Map<String, String> params = new HashMap<String, String>();
									params.put("mobile", AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.PHONEONE));
									params.put("password", AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.USERID_PWD));
									return params;
								}
							};
							AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
						}
					} else {
						Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
						// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
						// Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(i);
						finish();
					}
				}
			};
			new Handler().postDelayed(runnable, 1000);
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	private void dealData(String result) {

		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseUserInfo.class);
			userInfo = baseInfo.getUser_info();
			if (userInfo != null) {
//				String name = userInfo.getIm_username();
//				String pwd = userInfo.getIm_password();
				// 进入主页面
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.USERID, userInfo.getId());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_ADDRESS, userInfo.getAddress());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.IM_PWD, userInfo.getIm_password());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.IM_NAME, userInfo.getIm_username());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.PHONETWO, userInfo.getMobile_in_use());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.PHONEONE, userInfo.getMobile_register());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_NAME, userInfo.getName());
//				System.out.println(" 1userInfo.getMy_code()=" + userInfo.getMy_code());
//				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.MY_CODE, userInfo.getMy_code());
//
//				AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, true);
//				Intent intent = new Intent();
//				intent.setAction("com.app.action.broadcast");
//				sendBroadcast(intent);

				Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
				// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
				// Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
				finish();
			}
		} else if (status == 500) {
			Utils.showText(WelcomeActivity.this, "用户名或者密码错误...");
		}
		dismissLoadingDialog();
	}
}
