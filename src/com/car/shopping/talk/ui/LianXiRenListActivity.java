package com.car.shopping.talk.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.talk.Constant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

public class LianXiRenListActivity extends BaseActivity {

	protected static final String TAG = "LianXiRenListActivity";

	private ConversationListFragment conversationListFragment;
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo>data;
	private Map<String, String> map = UtilMap.getInstance().init();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			DemoHelper.getInstance().logout(false, null);
			finish();
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			finish();
			return;
		}

		setContentView(R.layout.activity_list);
		conversationListFragment = new ConversationListFragment();
		data = new ArrayList<>();
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, conversationListFragment).show(conversationListFragment).commit();

		EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
		System.out.println("width:" + getScreenWidth(this) + "  height:" + getScreenHeight(this));
		
	}

	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	// 获取屏幕的高度
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public class MyContactListener implements EMContactListener {
		@Override
		public void onContactAdded(String username) {
		}

		@Override
		public void onContactDeleted(final String username) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (ChatActivity.activityInstance != null && ChatActivity.activityInstance.toChatUsername != null && username.equals(ChatActivity.activityInstance.toChatUsername)) {
						String st10 = getResources().getString(R.string.have_you_removed);
						Toast.makeText(LianXiRenListActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, 1).show();
						ChatActivity.activityInstance.finish();
					}
				}
			});
		}

		@Override
		public void onContactInvited(String username, String reason) {
		}

		@Override
		public void onContactAgreed(String username) {
		}

		@Override
		public void onContactRefused(String username) {
		}
	}

	EMMessageListener messageListener = new EMMessageListener() {

		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			
			// 提示新消息
			String allname = "";
			for (EMMessage message : messages) {
//				String str = messages.get(0).getUserName();
				String str1 = message.getFrom();
				allname = allname + message.getFrom() + ",";
				DemoHelper.getInstance().getNotifier().onNewMsg(message);
			}
			allname = allname.substring(0, allname.length()-1);
			getNickNameResut(allname);
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
				// 当前页面如果为聊天历史页面，刷新此页面
				if (conversationListFragment != null) {
					conversationListFragment.refresh();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.pushActivity(this);
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		DemoHelper sdkHelper = DemoHelper.getInstance();
		sdkHelper.popActivity(this);
	}

	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
	
	
	private void getNickNameResut(final String allname) {
		AppContext.getInstance().cancelPendingRequests(TAG);

		StringRequest stringRequest = new StringRequest(Request.Method.POST, com.car.shopping.constant.Constant.URL_TEST + Urls.api_get_ALL_nickname, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
//				try {
//					JSONObject json = new JSONObject(result);
//					System.out.println("昵称=="+json.toString());
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
				dealReturnReuslt(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(LianXiRenListActivity.this, "网络访问失败");
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
				data = baseTalkNickName.getData();
				for(int i=0;i<data.size();i++){
					map.put(data.get(i).getIm_username(), data.get(i).getNickname());
				}
				refreshUIWithMessage();
			}
		}
	}
}
