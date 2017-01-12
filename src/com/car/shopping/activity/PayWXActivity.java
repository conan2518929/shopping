package com.car.shopping.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
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
import com.car.shopping.entity.AlipayInfo;
import com.car.shopping.entity.IsPayInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.PayResult;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PayWXActivity extends BaseActivity {

	private Button btn_pay;
	private RelativeLayout rl_wx, rl_zfb;
	private ImageView img_one, img_two;
	// private WXPayInfo baseInfo;
	private MyBroadcastReciver myBroadcast;
	// private BackPayValueInfo backInfo;
	private LinearLayout back;
	private int payType = 0;
	private EditText et_money;
	private String mMoney = "";
	private String id = "";
	private AlipayInfo alipayInfo;
	private static final int SDK_PAY_FLAG = 1;
	private IsPayInfo isPay;
	private String mIsPay = "";
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					getPayResut();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayWXActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayWXActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	private void getPayResut() {
		AppContext.getInstance().cancelPendingRequests(TAG);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.QUERY_ORDER_STATUS, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealReturnReuslt(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(PayWXActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("order_no", alipayInfo.getOrder_no());
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealReturnReuslt(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			isPay = gson.fromJson(result, IsPayInfo.class);
			mIsPay = isPay.getIsPaid();
			if(mIsPay != null && mIsPay.equals("yes")){
				Toast.makeText(PayWXActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				finish();
			}else{
				Toast.makeText(PayWXActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if (status == 300) {
			logout();
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_wx);
		initViews();
		initData();
		initBroadcast();
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		btn_pay = getView(R.id.btn_pay);
		rl_wx = getView(R.id.rl_wx);
		rl_zfb = getView(R.id.rl_zfb);
		img_one = getView(R.id.img_one);
		img_two = getView(R.id.img_two);
		back = getView(R.id.back);
		et_money = getView(R.id.et_input_money);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}

	@Override
	public void initData() {
		id = getIntent().getExtras().getString("shop_id");
		rl_wx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageResource(R.drawable.choose_ok);
				img_two.setImageResource(R.drawable.no_choose);
				payType = 0;
			}
		});
		rl_zfb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				img_one.setImageResource(R.drawable.no_choose);
				img_two.setImageResource(R.drawable.choose_ok);
				payType = 1;
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btn_pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					mMoney = et_money.getText().toString().trim();
					if (mMoney == null || mMoney.length() == 0 || id == null || id.length() == 0) {
						Utils.showText(PayWXActivity.this, "请您填写金额...");
						return;
					} else {
						if (payType == 1) {// 支付宝
							zfb_pay();

						} else {// 微信
							// boolean isPaySupported =
							// AppContext.wxApi.getWXAppSupportAPI() >=
							// Build.PAY_SUPPORTED_SDK_INT;
							// if (isPaySupported) {
							// getValue();
							// } else {
							// Utils.showShortToast(PayWXActivity.this,
							// "请您在应用商店安装微信客户端...");
							// }
							Utils.showText(PayWXActivity.this, "请选择支付宝进行支付...");
						}
					}
				} else {
					Utils.showText(PayWXActivity.this, "请登录后查看...");
				}

			}
		});
	}

	private void zfb_pay() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.CREATE_ORDER_ALIPAY, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealAlipayData(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(PayWXActivity.this, "网络访问失败");
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", id);
				params.put("total_fee", mMoney);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealAlipayData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			alipayInfo = gson.fromJson(result, AlipayInfo.class);
			if (alipayInfo != null) {
				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						// 构造PayTask 对象
						PayTask alipay = new PayTask(PayWXActivity.this);
						// 调用支付接口，获取支付结果
						String result = alipay.pay(alipayInfo.getPayInfo(), true);
						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};
				// 必须异步调用
				Thread payThread = new Thread(payRunnable);
				payThread.start();
				Utils.showText(PayWXActivity.this, "创建订单成功...");
			}
		}else if (status == 300) {
			logout();
		} else {
			Utils.showText(PayWXActivity.this, "创建订单失败...");
		}
	}

	private void getValue() {
		// RequestParams rs = new RequestParams();
		// rs.put("guid",
		// AppContext.mSharePref_tem.getSharePrefString(SharedPrefConstant.REN_GUID));
		// rs.put("username",
		// AppContext.mSharePref_tem.getSharePrefString(SharedPrefConstant.REN_NAME));
		// rs.put("body", "金箍棒");
		// rs.put("fee", "0.01");
		// HttpUtil.getInstance().post(WDUrl.WX_PAY, rs, new
		// AsyncHttpResponseHandler() {
		//
		// @Override
		// public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable
		// arg3) {
		// dismissLoadingDialog();
		// Utils.showText(PayWXActivity.this, "访问失败");
		// }
		//
		// @Override
		// public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		// String json = new String(arg2);
		// System.out.println("订单详情josn=" + json);
		// dealData(json);
		// }
		//
		// @Override
		// public void onStart() {
		// showLoadingDialog();
		// super.onStart();
		// }
		//
		// @Override
		// public void onFinish() {
		// dismissLoadingDialog();
		// super.onFinish();
		// }
		// });
	}

	private void dealData(String result) {
		// Gson gson = new Gson();
		// StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		// int status = statusInfo.getStatus();
		// if (status == 0) {
		// baseInfo = gson.fromJson(result, WXPayInfo.class);
		// if (baseInfo != null) {
		// PayReq req = new PayReq();
		// req.appId = baseInfo.getAppid();
		// req.partnerId = baseInfo.getPartnerid();
		// req.prepayId = baseInfo.getPrepayid();
		// req.nonceStr = baseInfo.getNoncestr();
		// req.timeStamp = baseInfo.getTimestamp();
		// req.packageValue = "Sign=WXPay";
		// req.sign = baseInfo.getSign();
		// req.extData = "app data"; // optional
		// AppContext.wxApi.sendReq(req);
		// }
		// }else if(status == -1){
		// Utils.showShortToast(PayWXActivity.this, "请您先登录...");
		// }
	}

	/**
	 * 查询订单是否成功
	 * 
	 * @param result
	 */
	private void dealData1(String result) {
		// Gson gson = new Gson();
		// StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		// int status = statusInfo.getStatus();
		// if (status == 0) {
		// backInfo = gson.fromJson(result, BackPayValueInfo.class);
		// if (backInfo != null) {
		// Intent i = new Intent(PayWXActivity.this, ShowResultActivity.class);
		// i.putExtra("value", backInfo);
		// startActivity(i);
		// }
		// }
	}

	public void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast4");
		registerReceiver(myBroadcast, intentFilter);
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals("com.app.action.broadcast4")) {
				// RequestParams rs = new RequestParams();
				// rs.put("transaction_id", "");
				// rs.put("out_trade_no", baseInfo.getOut_trade_no());
				// HttpUtil.getInstance().post(WDUrl.CHECK_PAY, rs, new
				// AsyncHttpResponseHandler() {
				//
				// @Override
				// public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				// Throwable arg3) {
				// dismissLoadingDialog();
				// Utils.showText(PayWXActivity.this, "访问失败");
				// }
				//
				// @Override
				// public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// String json = new String(arg2);
				// System.out.println("查询订单是否成功josn=" + json);
				// dealData1(json);
				// }
				//
				// @Override
				// public void onStart() {
				// showLoadingDialog();
				// super.onStart();
				// }
				//
				// @Override
				// public void onFinish() {
				// dismissLoadingDialog();
				// super.onFinish();
				// }
				// });
			}
		}
	}

	private void unregisterReceiver() {
		unregisterReceiver(myBroadcast);
	}

	@Override
	protected void onStop() {
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
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
				Utils.showText(PayWXActivity.this, "网络访问失败");
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
			Intent i = new Intent(PayWXActivity.this, LoginActivity.class);
			startActivity(i);
			Utils.showText(PayWXActivity.this, "由于您长时间未操作....请您重新登录账号...");
		}
	}
}
