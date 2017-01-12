package com.car.shopping.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.car.shopping.app.AppContext;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.activity.CancelActivity;
import com.car.shopping.activity.ContactsListActivity;
import com.car.shopping.activity.ContactsListOneActivity;
import com.car.shopping.activity.IntegralMallActivity;
import com.car.shopping.activity.InteractiveActivity;
import com.car.shopping.activity.LoginActivity;
import com.car.shopping.activity.MainActivity;
import com.car.shopping.activity.RecommendActivity;
import com.car.shopping.activity.RuleActivity;
import com.car.shopping.activity.UserNumberActivity;
import com.car.shopping.activity.VinActivity;
import com.car.shopping.app.BaseInterfaces;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.talk.ui.UserProfileActivity;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

public class MyFragment extends BaseFragment implements BaseInterfaces {

	private final String TAG = "MyFragment";
	private MyBroadcastReciver myBroadcast;
	private View layoutView;
	private RelativeLayout rl_ziliao, rl_dfk, rl_yfk, rl_yqx, rl_thjl, rl_txl, rl_guize, rl_ywc,rl_tuijian,rl_shangcheng,rl_vin,rl_hudong,usernum;
	private LinearLayout ll_ziliao, add_ziliao;
	private Button btnOut, btn_ok;
	private ImageView img_login;
	private TextView bj, tv_login, kefu_tel;

	private EditText et_name, et_tel, et_address;
	private TextView tv_name, tv_address, tv_tel;
	private String str1 = "", str2 = "", str3 = "", str4 = "";
	private Button show_talk_num;

	private LinearLayout ll_talk;
	
	// private Handler h = new Handler();
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo>data1;
	private Map<String, String> map = UtilMap.getInstance().init();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.fragment_my, container, false);
		initViews();
		initData();
		return layoutView;
	}

	@Override
	public void initViews() {
		show_talk_num = (Button) layoutView.findViewById(R.id.show_talk_num);
		btnOut = (Button) layoutView.findViewById(R.id.btn);
		btn_ok = (Button) layoutView.findViewById(R.id.btn_ok);
		rl_ziliao = (RelativeLayout) layoutView.findViewById(R.id.rl_ziliao);
		rl_dfk = (RelativeLayout) layoutView.findViewById(R.id.rl_dfk);
		rl_yfk = (RelativeLayout) layoutView.findViewById(R.id.rl_yfk);
		rl_yqx = (RelativeLayout) layoutView.findViewById(R.id.rl_yqx);
		rl_thjl = (RelativeLayout) layoutView.findViewById(R.id.rl_thjl);
		rl_txl = (RelativeLayout) layoutView.findViewById(R.id.rl_txl);
		rl_guize = (RelativeLayout) layoutView.findViewById(R.id.rl_guize);
		rl_ywc = (RelativeLayout) layoutView.findViewById(R.id.rl_ywc);
		rl_tuijian = (RelativeLayout) layoutView.findViewById(R.id.rl_tuijian);
		rl_vin = (RelativeLayout) layoutView.findViewById(R.id.rl_vin);
		rl_shangcheng = (RelativeLayout) layoutView.findViewById(R.id.rl_shangcheng);
		rl_hudong = (RelativeLayout) layoutView.findViewById(R.id.rl_hudong);
		usernum = (RelativeLayout) layoutView.findViewById(R.id.usernum);
		img_login = (ImageView) layoutView.findViewById(R.id.login);
		ll_ziliao = (LinearLayout) layoutView.findViewById(R.id.ll_ziliao);
		add_ziliao = (LinearLayout) layoutView.findViewById(R.id.add_ziliao);
		bj = (TextView) layoutView.findViewById(R.id.bj);
		tv_login = (TextView) layoutView.findViewById(R.id.tv_login);
		tv_name = (TextView) layoutView.findViewById(R.id.tv_name);
		tv_address = (TextView) layoutView.findViewById(R.id.tv_address);
		tv_tel = (TextView) layoutView.findViewById(R.id.tv_tel);
		kefu_tel = (TextView) layoutView.findViewById(R.id.kefu_tel);
		et_name = (EditText) layoutView.findViewById(R.id.et_name);
		et_tel = (EditText) layoutView.findViewById(R.id.et_tel);
		et_address = (EditText) layoutView.findViewById(R.id.et_address);
		ll_talk = (LinearLayout) layoutView.findViewById(R.id.ll_talk);
		
	}

	private void initUserInfo() {
		str1 = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.REN_NAME);
		str2 = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.PHONETWO);
		str3 = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.REN_ADDRESS);
		tv_name.setText(str1);
		tv_tel.setText(str2);
		tv_address.setText(str3);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		data1 = new ArrayList<>();
		boolean isGo0 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
		if (isGo0) {
			ll_ziliao.setVisibility(View.VISIBLE);
			add_ziliao.setVisibility(View.GONE);
			rl_ziliao.setVisibility(View.GONE);
			btnOut.setVisibility(View.VISIBLE);
			initUserInfo();
		} else {
			ll_ziliao.setVisibility(View.GONE);
			add_ziliao.setVisibility(View.GONE);
			rl_ziliao.setVisibility(View.VISIBLE);
			btnOut.setVisibility(View.GONE);
			AppContext.mSharedPref.clear();
		}
		initBroadcast();
		btnOut.setOnClickListener(new OnClickListener() {// 退出

			@Override
			public void onClick(View v) {
				logout();
			}
		});
		rl_shangcheng.setOnClickListener(new OnClickListener() {// 积分商城
			
			@Override
			public void onClick(View v) {
				boolean isGo3 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo3) {
					Utils.gotoOtherActivity(getActivity(), IntegralMallActivity.class);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		
		ll_talk.setOnClickListener(new OnClickListener() {// 聊天列表

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
					Utils.showText(getActivity(), "请您登录后进行聊天...");
				}
			}
		});
		img_login.setOnClickListener(new OnClickListener() {// 登陆

					@Override
					public void onClick(View v) {
						Utils.gotoOtherActivity(getActivity(), LoginActivity.class);

					}
				});
		tv_login.setOnClickListener(new OnClickListener() {// 登陆

			@Override
			public void onClick(View v) {
				Utils.gotoOtherActivity(getActivity(), LoginActivity.class);
			}
		});
		bj.setOnClickListener(new OnClickListener() {// 编辑资料

			@Override
			public void onClick(View v) {
				add_ziliao.setVisibility(View.VISIBLE);
				ll_ziliao.setVisibility(View.GONE);

				et_name.setText(str1);
				et_tel.setText(str2);
				et_address.setText(str3);
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {// 保存资料

			@Override
			public void onClick(View v) {
				str1 = et_name.getText().toString().trim();
				str2 = et_tel.getText().toString().trim();
				str3 = et_address.getText().toString().trim();
				str4 = AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.USERID);
				if (str1.equals("") || str2.equals("") || str3.equals("") || str4.equals("")) {
					Utils.showShortToast(getActivity(), "资料不能为空...");
				} else {
					((MainActivity) getActivity()).showLoadingDialog();
					AppContext.getInstance().cancelPendingRequests(TAG);
					StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.EDIT_INFO, new Response.Listener<String>() {

						@Override
						public void onResponse(String result) {
							dealData(result);
							((MainActivity) getActivity()).dismissLoadingDialog();
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError volleyError) {
							((MainActivity) getActivity()).dismissLoadingDialog();
							Utils.showText(getActivity(), "网络访问失败");
						}
					}) {

						@Override
						protected Map<String, String> getParams() throws AuthFailureError {
							Map<String, String> map = new HashMap<>();
							map.put("user_id", str4);
							map.put("mobile_in_use", str2);
							map.put("name", str1);
							map.put("address", str3);
							return map;
						}
					};
					AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
				}
			}
		});
		rl_dfk.setOnClickListener(new OnClickListener() {// 待付款

			@Override
			public void onClick(View v) {
				boolean isGo1 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo1) {
					getDetail(1);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_yfk.setOnClickListener(new OnClickListener() {// 已付款

			@Override
			public void onClick(View v) {

				boolean isGo2 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo2) {
					getDetail(2);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_yqx.setOnClickListener(new OnClickListener() {// 已取消

			@Override
			public void onClick(View v) {

				boolean isGo3 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo3) {
					getDetail(3);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_ywc.setOnClickListener(new OnClickListener() {// 已完成

			@Override
			public void onClick(View v) {

				boolean isGo3 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo3) {
					getDetail(4);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_tuijian.setOnClickListener(new OnClickListener() {// 邀请
			
			@Override
			public void onClick(View v) {
				
				boolean isGo3 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo3) {
					Utils.gotoOtherActivity(getActivity(), RecommendActivity.class);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_thjl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isGo4 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo4) {
					int id = AppContext.mSharedPref.getSharePrefInteger("id", 0);
					getContacts(id, ContactsListActivity.class);

				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_txl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean isGo3 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo3) {
					int id = AppContext.mSharedPref.getSharePrefInteger("id", 0);
					getContacts(id, ContactsListOneActivity.class);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		rl_guize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.gotoOtherActivity(getActivity(), RuleActivity.class);
			}
		});
		rl_vin.setOnClickListener(new OnClickListener() {//vin
			
			@Override
			public void onClick(View v) {
				Utils.gotoOtherActivity(getActivity(), VinActivity.class);
			}
		});
		rl_hudong.setOnClickListener(new OnClickListener() {//互动
			
			@Override
			public void onClick(View v) {
				boolean isGo1 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo1) {
					Utils.gotoOtherActivity(getActivity(), InteractiveActivity.class);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});
		usernum.setOnClickListener(new OnClickListener() {//用户量
			
			@Override
			public void onClick(View v) {
				boolean isGo1 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isGo1) {
					Utils.gotoOtherActivity(getActivity(), UserNumberActivity.class);
				} else {
					Utils.showToast(getActivity(), "请您登录后查看...", 1000);
				}
			}
		});

	}

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			add_ziliao.setVisibility(View.GONE);
			ll_ziliao.setVisibility(View.VISIBLE);
			tv_name.setText(str1);
			tv_tel.setText(str2);
			tv_address.setText(str3);
			AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_NAME, str1);
			AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.PHONETWO, str2);
			AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.REN_ADDRESS, str3);

			updateRemoteNick(str1);
		} else if (status == 500) {
			Utils.showText(getActivity(), "保存资料失败...");
		} else if (status == 300) {
			logout();
		}
	}

	private void logout() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGOUT, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealLogoutShop(result);
				((MainActivity) getActivity()).dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				((MainActivity) getActivity()).dismissLoadingDialog();
				Utils.showText(getActivity(), "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealLogoutShop(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			rl_ziliao.setVisibility(View.VISIBLE);
			ll_ziliao.setVisibility(View.GONE);
			btnOut.setVisibility(View.GONE);
			add_ziliao.setVisibility(View.GONE);
			AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			AppContext.mSharedPref.clear();
			AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, 0);
			show_talk_num.setText("0");
			onLogout();
		}
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		AppContext.getInstance().cancelPendingRequests(TAG);
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		initUserInfo();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = AppContext.mSharedPref.getSharePrefInteger(SharedPrefConstant.TALK_NUMBER, 0);
		kefu_tel.setText(AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.KEFU_TEL, ""));
		show_talk_num.setText("" + unreadNum);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}

	private void getDetail(int type) {
		Bundle data = new Bundle();
		data.putInt("type", type);
		Utils.goOtherWithDataActivity(getActivity(), CancelActivity.class, data);
	}

	private void getContacts(int id, Class<?> cls) {
		Bundle data = new Bundle();
		data.putInt("id", id);
		Utils.goOtherWithDataActivity(getActivity(), cls, data);
	}

	public void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast");
		getActivity().registerReceiver(myBroadcast, intentFilter);
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals("com.app.action.broadcast")) {
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					rl_ziliao.setVisibility(View.GONE);
					ll_ziliao.setVisibility(View.VISIBLE);
					add_ziliao.setVisibility(View.GONE);
					btnOut.setVisibility(View.VISIBLE);
					initUserInfo();
				} else {
					rl_ziliao.setVisibility(View.VISIBLE);
					add_ziliao.setVisibility(View.GONE);
					ll_ziliao.setVisibility(View.GONE);
					btnOut.setVisibility(View.GONE);
					AppContext.mSharedPref.clear();
					onLogout();
					AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, 0);
					show_talk_num.setText("0");
				}
			}
		}
	}

	// 注销
	private void onLogout() {
		final ProgressDialog pd = new ProgressDialog(getActivity());
		String st = getActivity().getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		DemoHelper.getInstance().logout(false, new EMCallBack() {
			@Override
			public void onSuccess() {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Utils.gotoOtherActivity(getActivity(), LoginActivity.class);
					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.dismiss();
						Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void unregisterReceiver() {
		if (myBroadcast != null) {
			getActivity().unregisterReceiver(myBroadcast);
		}
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
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (((MainActivity) getActivity()).isConflict) {
			outState.putBoolean("isConflict", true);
		} else if (((MainActivity) getActivity()).getCurrentAccountRemoved()) {
			outState.putBoolean(com.car.shopping.talk.Constant.ACCOUNT_REMOVED, true);
		}
	}

	private ProgressDialog dialog;

	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(getActivity(), getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
				if (getActivity().isFinishing()) {
					return;
				}
				if (!updatenick) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(), getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
					});
				} else {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							EMClient.getInstance().updateCurrentUserNick(nickName);
							Toast.makeText(getActivity(), getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
	
	protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变 
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                        sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }
	
	private void getNickNameResut(final String allname) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.api_get_ALL_nickname, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealReturnReuslt(result);
				((MainActivity) getActivity()).dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				((MainActivity) getActivity()).dismissLoadingDialog();
				Utils.showText(getActivity(), "网络访问失败");
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
    
    private void dealReturnReuslt(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseTalkNickName = gson.fromJson(result, BaseTalkNickNameInfo.class);
			if(baseTalkNickName != null){
				data1 = baseTalkNickName.getData();
				if(data1.size() > 0){
					for(int i=0;i<data1.size();i++){
						map.put(data1.get(i).getIm_username(), data1.get(i).getNickname());
					}	
				}
				Intent i = new Intent(getActivity(), LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
