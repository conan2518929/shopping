package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

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
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.IdentificationInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.http.VolleyInterface;
import com.car.shopping.http.VolleyRequest;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 2016-4-11 易戈
 * 这个类是搜索结果页面页面
 * initViews()实现控件初始化
 * initData()实现数据初始化
 * initTitle()实现标题栏初始化
 */
@SuppressLint("ShowToast")
public class SearchTotalActivity extends BaseActivity implements OnClickListener{
	
	private EditText search_et;
	private LinearLayout back;
	private ImageView btn_search;
	private IdentificationInfo baseInfo;
	private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10;
	private Button show_talk_num;
	private LinearLayout ll_talk;
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data;
	private Map<String, String> map = UtilMap.getInstance().init();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_total);
		initViews();
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
		show_talk_num.setText("" + unreadNum);
	}
	
	@Override
	public void initViews() {
		show_talk_num = getView(R.id.show_talk_num);
		back = getView(R.id.back);
		search_et = getView(R.id.search_et);
		btn_search = getView(R.id.btn_search);
		tv1 = getView(R.id.tv1);
		tv2 = getView(R.id.tv2);
		tv3 = getView(R.id.tv3);
		tv4 = getView(R.id.tv4);
		tv5 = getView(R.id.tv5);
		tv6 = getView(R.id.tv6);
		tv7 = getView(R.id.tv7);
		tv8 = getView(R.id.tv8);
		tv9 = getView(R.id.tv9);
		tv10 = getView(R.id.tv10);
		ll_talk = getView(R.id.ll_talk_list);
	}

	@Override
	public void initData() {
		data = new ArrayList<>();
		getData();
		search_et.setOnKeyListener(onKeyListener);  
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		tv4.setOnClickListener(this);
		tv5.setOnClickListener(this);
		tv6.setOnClickListener(this);
		tv7.setOnClickListener(this);
		tv8.setOnClickListener(this);
		tv9.setOnClickListener(this);
		tv10.setOnClickListener(this);
		ll_talk.setOnClickListener(this);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showResultList();
			}
		});
	}
	
	private void getData() {
		showLoadingDialog();
		VolleyRequest.getInstance().RequestGet(SearchTotalActivity.this, 
				Constant.URL_TEST+Urls.HOT_SEARCH, TAG, new VolleyInterface(this, VolleyInterface.mListener, VolleyInterface.mErrorListtener) {

			@Override
			public void onMySuccess(String result) {
				dealData(result);
				dismissLoadingDialog();
			}

			@Override
			public void onMyError(VolleyError error) {
				dismissLoadingDialog();
				Utils.showText(SearchTotalActivity.this, "网络访问失败");
			}
		});
	}

	/**
	 * 解析josn数据
	 * */
	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, IdentificationInfo.class);
			if(baseInfo != null){
				int num = baseInfo.getData().size();
				List<TextView>list = new ArrayList<>();
				list.add(tv1);
				list.add(tv2);
				list.add(tv3);
				list.add(tv4);
				list.add(tv5);
				list.add(tv6);
				list.add(tv7);
				list.add(tv8);
				list.add(tv9);
				list.add(tv10);
				for(int i =0;i<num;i++){
					list.get(i).setText(baseInfo.getData().get(i).getSearch_content());
					list.get(i).setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	private OnKeyListener onKeyListener = new OnKeyListener() {  
        
        @Override  
        public boolean onKey(View v, int keyCode, KeyEvent event) {  
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){  
                /*隐藏软键盘*/  
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
                if(inputMethodManager.isActive()){  
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);  
                }  
                showResultList();
                return true;  
            }  
            return false;  
        }  
    };
    
    private void showResultList(){
    	String mStr = search_et.getText().toString();
        if(mStr == null || mStr.equals("")){
        	Toast.makeText(getBaseContext(), "请输入查询的商品名称...", 1000).show();
        }else{
        	getResult(mStr);
        }
    }
    
    private void getResult(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		Utils.goOtherWithDataActivity(SearchTotalActivity.this, ResultShopListActivity.class, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv1:
			getResult(tv1.getText().toString().trim());
			break;
		case R.id.tv2:
			getResult(tv2.getText().toString().trim());
			break;
		case R.id.tv3:
			getResult(tv3.getText().toString().trim());
			break;
		case R.id.tv4:
			getResult(tv4.getText().toString().trim());
			break;
		case R.id.tv5:
			getResult(tv5.getText().toString().trim());
			break;
		case R.id.tv6:
			getResult(tv5.getText().toString().trim());
			break;
		case R.id.tv7:
			getResult(tv7.getText().toString().trim());
			break;
		case R.id.tv8:
			getResult(tv8.getText().toString().trim());
			break;
		case R.id.tv9:
			getResult(tv9.getText().toString().trim());
			break;
		case R.id.tv10:
			getResult(tv10.getText().toString().trim());
			break;
		case R.id.ll_talk_list:
			boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			if(isShow){
				String allname = "";
				List<EMConversation> list = loadConversationList();
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						allname = allname + list.get(i).getUserName() + ",";
					}
					allname = allname.substring(0, allname.length() - 1);
				}
				getNickNameResut(allname);
			}else{
				Utils.showText(SearchTotalActivity.this,"请您登录后进行聊天...");
			}
			break;

		default:
			
			break;
		}
		
	}
    
	@Override
	protected void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
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
				dealReturnReuslt(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(SearchTotalActivity.this, "网络访问失败");
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
			if (baseTalkNickName != null) {
				data = baseTalkNickName.getData();
				if (data.size() > 0) {
					for (int i = 0; i < data.size(); i++) {
						map.put(data.get(i).getIm_username(), data.get(i).getNickname());
					}
				}
				Intent i = new Intent(SearchTotalActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
