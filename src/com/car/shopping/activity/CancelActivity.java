package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.PayStatusListAdapter;
import com.car.shopping.adapter.PayStatusListAdapter.PayStatusClickListenerOne;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.AlipayInfo;
import com.car.shopping.entity.BaseHomeLvItemInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.HomeLvItemInfo;
import com.car.shopping.entity.IsPayInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.entity.TelsInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.ChatActivity;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.utils.PayResult;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.PullToRefreshBase;
import com.car.shopping.widget.PullToRefreshListView;
import com.car.shopping.widget.PullToRefreshBase.OnRefreshListener;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

@SuppressLint("RtlHardcoded")
public class CancelActivity extends BaseActivity {
	private final String TAG = "CancelActivity";
	private PullToRefreshListView mPullListView;
	private ListView listView;
	private LinearLayout back;
	private Button show_talk_num;
	private Handler h = new Handler();
	private PayStatusListAdapter adapter;
	private List<HomeLvItemInfo> lists;
	private LinearLayout ll_talk;
	private BaseHomeLvItemInfo baseInfo;
	private TextView title;
	private Dialog dialog;
	private Display display;
	private List<TelsInfo> tels;
	private String numPhone = "";
	private int type = 0;
	private AlipayInfo alipayInfo;
	private IsPayInfo isPay;
	private String mIsPay = "";
	private static final int SDK_PAY_FLAG = 1;
	private int numButton = 0;//区别已付款 未付款中的 取消订单按钮
	private String order_no = "";
	private int weizhi = 0;//待付款中取消订单 标记改变的位置
	private int ok = 0;//待付款中 确认收货 标记改变的位置
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data;
	private Map<String, String> map = UtilMap.getInstance().init();
	
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
					showLoadingDialog();
					getPayResut();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(CancelActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
						finish();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(CancelActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
				Utils.showText(CancelActivity.this, "网络访问失败");
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
			if (mIsPay != null && mIsPay.equals("yes")) {
				Toast.makeText(CancelActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				getDetail(2);				
				finish();
			} else {
				Toast.makeText(CancelActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
			}
		} else if (status == 300) {
			logout();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ispaylv);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		mPullListView = getView(R.id.listview);
		ll_talk = getView(R.id.ll_talk_list);
		show_talk_num = getView(R.id.show_talk_num);
		title = getView(R.id.title);
	}

	@Override
	public void initData() {
		type = getIntent().getExtras().getInt("type");
		data = new ArrayList<>();
		if (type == 1) {
			title.setText("待付款");
		} else if (type == 2) {
			title.setText("已付款");
		} else if (type == 3) {
			title.setText("已取消");
		} else if (type == 4) {
			title.setText("已完成");
		}
		lists = new ArrayList<>();
		tels = new ArrayList<>();
		mPullListView.setPullLoadEnabled(true);
		mPullListView.setScrollLoadEnabled(false);
		listView = mPullListView.getRefreshableView();
		adapter = new PayStatusListAdapter(CancelActivity.this, lists, type, mListener);
		listView.setAdapter(adapter);
		listView.setVerticalScrollBarEnabled(false);
		
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				initCancelListData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullUpRefreshComplete();
			}
		});

		mPullListView.setLastUpdatedLabel(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME));
		mPullListView.doPullRefreshing(false, 100);
		ll_talk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					String allname = "";
					List<EMConversation> list = loadConversationList();
					if(list.size() > 0){
						for(int i=0;i<list.size();i++){
							allname = allname+list.get(i).getUserName()+",";
						}
						allname = allname.substring(0, allname.length()-1);
					}
					getNickNameResut(allname);
				} else {
					Utils.showText(CancelActivity.this, "请您登录后进行聊天...");
				}
			}
		});
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	@Override
	public void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
		show_talk_num.setText("" + unreadNum);
	}

	@Override
	public void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	private PayStatusClickListenerOne mListener = new PayStatusClickListenerOne() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:
				showDialog(position, v);
				break;
			case R.id.qqsh://确认收货
				ok = position;
				order_no = lists.get(position).getOrder_no();
				String status = lists.get(position).getStatus();
				if (order_no != null && order_no.length() != 0) {
					if(status.equals("-2")||status.equals("1")||status.equals("2")){
						dialog("确认收货吗？",1);
					}else{
						Utils.showText(CancelActivity.this, "您暂时不能确认收货...");
					}
				}
				break;
			case R.id.qxdd://已付款中取消订单
				numButton = 2;
				weizhi = position;
				order_no = lists.get(position).getOrder_no();
				if (order_no != null && order_no.length() != 0) {
					dialog("确认删除该订单吗？",2);
				}
				break;
			case R.id.scdd:// 删除订单
				numButton = 1;
				order_no = lists.get(position).getOrder_no();
				if (order_no != null && order_no.length() != 0) {
					dialog("确认删除该订单吗？",2);
				}
				break;
			case R.id.qrfk:// 确认付款
				order_no = lists.get(position).getOrder_no();
				if (order_no != null && order_no.length() != 0) {
					zfb_pay(order_no);
				}
				break;
			case R.id.tv_talk:
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					String name = lists.get(position).getIm_username();
					if (name != null && name.length() != 0) {
						updateAlias(name,position);
					}
				} else {
					Utils.showText(CancelActivity.this, "请您登录后进行聊天...");
				}
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showDialog(final int position, View v) {
		View view = LayoutInflater.from(CancelActivity.this).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		tels = lists.get(position).getTels();
		int num = tels.size();
		if (num == 1) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
		} else if (num == 2) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
		} else if (num == 3) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
		} else if (num == 4) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
			tv4.setVisibility(View.VISIBLE);
			tv4.setText(tels.get(3).getTel());
		} else if (num > 4) {
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
			tv4.setVisibility(View.VISIBLE);
			tv4.setText(tels.get(3).getTel());
			tv5.setVisibility(View.VISIBLE);
			tv5.setText(tels.get(4).getTel());
		}
		tv1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv1.getText().toString();
				getCallShop(position, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				numPhone = tv2.getText().toString();
				getCallShop(position, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv3.getText().toString();
				getCallShop(position, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv4.getText().toString();
				getCallShop(position, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv5.getText().toString();
				getCallShop(position, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv_qx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog = new Dialog(CancelActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	@SuppressWarnings("deprecation")
	private void initCancelListData() {
		AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME, new Date().toLocaleString());
		mPullListView.setLastUpdatedLabel(new Date().toLocaleString());
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.SELECT_PRDER, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("初始化列表"+result.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				dealData(result);
				mPullListView.onPullDownRefreshComplete();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				mPullListView.onPullDownRefreshComplete();
				Utils.showText(CancelActivity.this, "网络访问失败");
			}
		}) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				if (type == 3) {
					params.put("status_list", "-1,-4,-8");
				} else if (type == 1) {
					params.put("status_list", "0");
				} else if (type == 4) {
					params.put("status_list", "4,3,-9");
				}else if(type == 2){
					params.put("status_list", "1,2,-2,-6,-7,-5,-3");
				}
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
			baseInfo = gson.fromJson(result, BaseHomeLvItemInfo.class);
			if (baseInfo != null) {
				lists.clear();
				lists = baseInfo.getData();
				adapter.refresh(lists);
			}
		} else if (status == 300) {
			logout();
		}
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
				Utils.showText(CancelActivity.this, "网络访问失败");
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
			Intent i = new Intent(CancelActivity.this, LoginActivity.class);
			startActivity(i);
			Utils.showText(CancelActivity.this, "由于您长时间未操作....请您重新登录账号...");
			finish();
		}
	}

	private void getCallShop(final int position, final String num) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.CALL_SHOP, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealTelResult(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(CancelActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", lists.get(position).getShop_id());
				params.put("tel", num);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealTelResult(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {

		} else if (status == 300) {
			logout();
		}
	}

	private void updateAlias(String name,int position) {
		if(baseInfo != null){
			Intent intent = new Intent(CancelActivity.this, ChatActivity.class);
			intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, baseInfo.getData().get(position).getIm_username());
			intent.putExtra("user_nickname", baseInfo.getData().get(position).getShop_name());
			startActivity(intent);
		}
	}

	/**
	 * 再次支付未完成订单
	 * */
	private void zfb_pay(final String order_no) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.PAY_OEDER_ALIPAY, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealAlipayData(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(CancelActivity.this, "网络访问失败");
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("order_no", order_no);
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
						PayTask alipay = new PayTask(CancelActivity.this);
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
				Utils.showText(CancelActivity.this, "创建订单成功...");
			}
		} else if (status == 300) {
			logout();
		} else {
			Utils.showText(CancelActivity.this, "创建订单失败...");
		}
	}

	/**
	 * 删除订单接口
	 * */
	private void deleteOrder(String url,final int type){
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				if(type == 1){
					dealOKResult(result);
				}else if(type == 2){
					dealDeleteResult(result);
				}
				
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(CancelActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("order_no", order_no);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealDeleteResult(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			if(numButton == 1){//删除订单
				Utils.showText(CancelActivity.this, "删除订单成功...");
				getDetail(3);
				finish();
			}else if(numButton == 2){//取消订单
				Utils.showText(CancelActivity.this, "取消订单成功...");
				lists.get(weizhi).setStatus("-2");
				adapter.refresh(lists);
			}
		} else if (status == 300) {
			logout();
		}
	}
	
	private void dealOKResult(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
				Utils.showText(CancelActivity.this, "确认收货成功...");
				lists.remove(ok);
				adapter.refresh(lists);
		} else if (status == 300) {
			logout();
		}
	}

	private void getDetail(int type) {
		Bundle data = new Bundle();
		data.putInt("type", type);
		Utils.goOtherWithDataActivity(CancelActivity.this, CancelActivity.class, data);
	}

	private void dialog(String title,final int type) {
		AlertDialog.Builder builder = new Builder(CancelActivity.this);
		builder.setMessage(title);
		builder.setCancelable(false); 
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if(type == 1){
					deleteOrder(Urls.FINISH_ORDER,1);
				}else if(type == 2){
					deleteOrder(Urls.CANCEL_ORDER,2);
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	EMMessageListener messageListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// 提示新消息
			for (EMMessage message : messages) {
				DemoHelper.getInstance().getNotifier().onNewMsg(message);
			}
			refreshUIWithMessage();
		}

		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
		}

		@Override
		public void onMessageReadAckReceived(List<EMMessage> messages) {
		}

		@Override
		public void onMessageDeliveryAckReceived(List<EMMessage> message) {
		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {
		}
	};

	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
			}
		});
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = Utils.getUnreadMsgCountTotal();
		if (count > 0) {
			show_talk_num.setText(String.valueOf(count));
		} else {
			show_talk_num.setText("0");
		}
	}
	
	private void getNickNameResut(final String allname) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.api_get_ALL_nickname, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealReturnNickName(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(CancelActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("im_username_list", allname);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
    
    private void dealReturnNickName(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseTalkNickName = gson.fromJson(result, BaseTalkNickNameInfo.class);
			if(baseTalkNickName != null){
				data = baseTalkNickName.getData();
				if(data.size() > 0){
					for(int i=0;i<data.size();i++){
						map.put(data.get(i).getIm_username(), data.get(i).getNickname());
					}	
				}
				Intent i = new Intent(CancelActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
