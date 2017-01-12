package com.car.shopping.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends BaseActivity implements OnClickListener{

	private final String TAG = "LoginActivity";
	private LinearLayout back;
	private Button btn;
	private TextView register,find;
	private EditText account,pwd;
	private String mAccount = "",mPWD = "";
	private BaseUserInfo baseInfo;
	private UserInfo userInfo;
	private boolean progressShow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		btn = getView(R.id.btn);
		register = getView(R.id.register);
		find = getView(R.id.find);
		account = getView(R.id.account);
		pwd = getView(R.id.pwd);
	}
	
	@Override
	public void initData() {
		back.setOnClickListener(this);
		btn.setOnClickListener(this);
		register.setOnClickListener(this);
		find.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.register:
			Utils.gotoOtherActivity(LoginActivity.this, RegisterActivity.class);
			break;
		case R.id.find:
			Utils.gotoOtherActivity(LoginActivity.this, FindPwdActivity.class);
			break;
		case R.id.back:
			finish();
			break;
		case R.id.btn:
			mAccount = account.getText().toString().trim();
			mPWD = pwd.getText().toString().trim();
			if (mAccount.length() != 11) {
				Utils.showToast(LoginActivity.this, "请正确填写您的手机号码...", 1000);
				return;
			}
			
			if (mPWD.length() < 6 || mPWD.length() > 10) {
				Utils.showToast(LoginActivity.this, "请输入6-10位密码...", 1000);
				return;
			}
			mPWD = Utils.MD5STR(mPWD);
			System.out.println("mPWD="+mPWD);
			if (!EaseCommonUtils.isNetWorkConnected(this)) {
				Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
				return;
			}
			showLoadingDialog();
			AppContext.getInstance().cancelPendingRequests(TAG);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.LOGIN, new Response.Listener<String>() {
				
				@Override
				public void onResponse(String result) {
//					try {
//						JSONObject json = new JSONObject(result);
//						System.out.println("登录返回结果="+json.toString());
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
					dealData(result);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError volleyError) {
					dismissLoadingDialog();
					Utils.showText(LoginActivity.this, "网络访问失败");
				}
			}) {

				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> params = new HashMap<String, String>();
					params.put("mobile", mAccount);
					params.put("password", mPWD);
					return params;
				}
			};
			AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
			break;

		default:
			break;
		}
	}

	private void dealData(String result) {
		
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseUserInfo.class);
			userInfo = baseInfo.getUser_info();
			if(userInfo != null){
				String name = userInfo.getIm_username();
				String pwd = userInfo.getIm_password();
				login(name,pwd);
			}
		}else if(status == 500){
			Utils.showText(LoginActivity.this, "用户名或者密码错误...");
		}
		dismissLoadingDialog();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
	}
	
	public void login(String userName,String pwd) {
		if (!EaseCommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "EMClient.getInstance().onCancel");
				progressShow = false;
			}
		});
		pd.setMessage(getString(R.string.Is_landing));
		pd.show();

		// After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
		// close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(userName);
        
		final long start = System.currentTimeMillis();
		// 调用sdk登陆方法登陆聊天服务器
		Log.d(TAG, "EMClient.getInstance().login");
		EMClient.getInstance().login(userName, pwd, new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "login: onSuccess");

				if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
					pd.dismiss();
				}
				// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
			    EMClient.getInstance().chatManager().loadAllConversations();
			    EMClient.getInstance().groupManager().loadAllGroups();
				// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
				boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
						userInfo.getName());
				if (!updatenick) {
					Log.e("LoginActivity", "update current user nick fail");
				}
				//异步获取当前用户的昵称和头像(从自己服务器获取，demo使用的一个第三方服务)
				DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
				EMClient.getInstance().updateCurrentUserNick(userInfo.getName());//设置ios推送名称显示
				// 进入主页面
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.USERID, userInfo.getId());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.USERID_PWD, mPWD);
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_ADDRESS, userInfo.getAddress());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.IM_PWD, userInfo.getIm_password());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.IM_NAME, userInfo.getIm_username());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.PHONETWO, userInfo.getMobile_in_use());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.PHONEONE, userInfo.getMobile_register());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_NAME, userInfo.getName());
				System.out.println(" userInfo.getMy_code()="+ userInfo.getMy_code());
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.MY_CODE, userInfo.getMy_code());
				
				AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, true);
				Intent intent = new Intent();
				intent.setAction("com.app.action.broadcast");
				sendBroadcast(intent);
				
				finish();
			}

			@Override
			public void onProgress(int progress, String status) {
				Log.d(TAG, "login: onProgress");
				System.out.println("=====status======="+status);
			}

			@Override
			public void onError(final int code, final String message) {
				Log.d(TAG, "login: onError: " + code);
				System.out.println("=====code======="+code+",message=="+message);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

}
