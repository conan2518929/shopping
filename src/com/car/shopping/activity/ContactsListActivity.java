package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.AddressBookAdapter;
import com.car.shopping.adapter.AddressBookAdapter.MyClickListenerOne;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.AddressBookInfo;
import com.car.shopping.entity.BaseContactsListInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.entity.TelsInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.ChatActivity;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.SilderListView;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

public class ContactsListActivity extends BaseActivity {

	private LinearLayout back;
	private AddressBookAdapter adapter;// 通话记录
	private List<AddressBookInfo> data;// 通话记录
	private Dialog dialog;
	private Display display;
	private SilderListView mListView;
	private BaseContactsListInfo baseInfo;
	private List<TelsInfo> tels;
	private String numPhone = "";
	private Button show_talk_num;
	private LinearLayout ll_talk;

	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> datas;
	private Map<String, String> map = UtilMap.getInstance().init();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		mListView = getView(R.id.list);
		show_talk_num = getView(R.id.show_talk_num);
		ll_talk = getView(R.id.ll_talk_list);
	}

	@Override
	public void initData() {
		data = new ArrayList<>();
		datas = new ArrayList<>();
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ll_talk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					String allname = "";
					List<EMConversation> list = loadConversationList();
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							allname = allname + list.get(i).getUserName() + ",";
						}
						allname = allname.substring(0, allname.length() - 1);
					}
					getNickNameResut(allname);
				} else {
					Utils.showText(ContactsListActivity.this, "请您登录后进行聊天...");
				}
			}
		});
		data = new ArrayList<>();
		tels = new ArrayList<>();
		adapter = new AddressBookAdapter(ContactsListActivity.this, data, mListenerOne);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String idList = data.get(position).getId();
				getDetail(position, idList);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getCallRecordData();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
		show_talk_num.setText("" + unreadNum);
	}

	private void getCallRecordData() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_SHOP_CALLED, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealData(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ContactsListActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
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
				Utils.showText(ContactsListActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", data.get(position).getId());
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

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseContactsListInfo.class);
			if (baseInfo != null) {
				data = baseInfo.getData();
				adapter.refresh(data);
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
				Utils.showText(ContactsListActivity.this, "网络访问失败");
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
			Intent i = new Intent(ContactsListActivity.this, LoginActivity.class);
			startActivity(i);
		}
	}

	private MyClickListenerOne mListenerOne = new MyClickListenerOne() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:
				showDialog(position, v);
				break;
			case R.id.tv_talk:
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					String name = data.get(position).getIm_username();
					if (name != null && name.length() != 0) {
						updateAlias(name, position);
					}
				} else {
					Utils.showText(ContactsListActivity.this, "请您登录后进行聊天...");
				}
				break;
			case R.id.holder:
				deleteContactsList(position);
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void showDialog(final int position, View v) {
		View view = LayoutInflater.from(ContactsListActivity.this).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		tels = data.get(position).getTels();
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
		dialog = new Dialog(ContactsListActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	private void getDetail(int position, String id) {
		Bundle data = new Bundle();
		data.putString("shop_id", id);
		Utils.goOtherWithDataActivity(ContactsListActivity.this, ShopDetailActivity.class, data);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	/**
	 * 删除通话记录
	 * */
	private void deleteContactsList(final int position) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.DELETE_SHOP_CALLED, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealData(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ContactsListActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("record_id", data.get(position).getRecord_id());
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void updateAlias(String name, int position) {
		if (baseInfo != null) {
			Intent intent = new Intent(ContactsListActivity.this, ChatActivity.class);
			intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, baseInfo.getData().get(position).getIm_username());
			intent.putExtra("user_nickname", baseInfo.getData().get(position).getShop_name());
			startActivity(intent);
		}
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
				Utils.showText(ContactsListActivity.this, "网络访问失败");
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
			if (baseTalkNickName != null) {
				datas = baseTalkNickName.getData();
				if (datas.size() > 0) {
					for (int i = 0; i < datas.size(); i++) {
						map.put(datas.get(i).getIm_username(), datas.get(i).getNickname());
					}
				}
				Intent i = new Intent(ContactsListActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
