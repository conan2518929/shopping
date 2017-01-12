package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.FindByTypeOneAdapter;
import com.car.shopping.adapter.FindByTypeTwoAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseDXInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.DXInfo;
import com.car.shopping.entity.PartListInfo;
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

public class FindByTypeActivity extends BaseActivity implements OnItemClickListener{
	
	private int typeNum = 0;
	private TextView tv_title;
	private LinearLayout back;
	private EditText search_et;
	private ImageView btn_search;
	private ListView lv_one,lv_two;
	private FindByTypeOneAdapter oneAdapter;
	private FindByTypeTwoAdapter twoAdapter;
	
	private BaseDXInfo baseInfo;
	private List<DXInfo> data;
	private List<PartListInfo> part_list;
	private Button show_talk_num;
	private Handler h = new Handler();
	private LinearLayout ll_talk;
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data1;
	private Map<String, String> map = UtilMap.getInstance().init();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findbytype);
		initViews();
		initData();
		
	}

	@Override
	public void initViews() {
		show_talk_num = getView(R.id.show_talk_num);
		tv_title = getView(R.id.tv_title);
		back = getView(R.id.back);
		search_et = getView(R.id.search_et);
		lv_one = getView(R.id.lv_one);
		lv_two = getView(R.id.lv_two);
		btn_search = getView(R.id.btn_search);
		ll_talk = getView(R.id.ll_talk_list);
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
	public void initData() {
		data = new  ArrayList<>();
		part_list = new  ArrayList<>();
		data1 = new  ArrayList<>();
		
		typeNum = getIntent().getExtras().getInt("type", 0);
		if(typeNum == 7){
			tv_title.setText("单项产品");
			search_et.setHint("搜索品牌、部件名称...");
			getData(Urls.SEARCH_AM_PART);
		}else if(typeNum == 8){
			tv_title.setText("汽车用品");
			search_et.setHint("搜索品牌、商品名称...");
			getData(Urls.SEARCH_AM_ACCESSORY);
		}
		
		oneAdapter = new FindByTypeOneAdapter(FindByTypeActivity.this, data, R.layout.findbytype_item_one);
		twoAdapter = new FindByTypeTwoAdapter(FindByTypeActivity.this, part_list, R.layout.findbytype_item_two);
		lv_one.setAdapter(oneAdapter);
		lv_two.setAdapter(twoAdapter);
		lv_one.setOnItemClickListener(this);
		lv_two.setOnItemClickListener(this);
		oneAdapter.setPosition(0);
		oneAdapter.changeView();
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
				if(isShow){
					String allname = "";
					List<EMConversation> list = loadConversationList();
					if(list.size() > 0){
						for(int i=0;i<list.size();i++){
							allname = allname+list.get(i).getUserName()+",";
						}
						allname = allname.substring(0, allname.length()-1);
					}
					getNickNameResut(allname);
				}else{
					Utils.showText(FindByTypeActivity.this,"请您登录后进行聊天...");
				}
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = search_et.getText().toString().trim();
				if(str != null && !str.equals("")){
					getResultTitle(str);
				}else{
					Utils.showShortToast(FindByTypeActivity.this, "请您输入要搜索的商品...");
				}
			}
		});
	}

	@SuppressLint("ShowToast")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.lv_one:
			DXInfo dxInf = data.get(position);
			oneAdapter.setPosition(position);
			oneAdapter.changeView();
			part_list = dxInf.getPart_list();
			twoAdapter.refresh(part_list);
			break;
		case R.id.lv_two:
			String name = part_list.get(position).getPart_name();
			if(name.equals("全部")){
				if(part_list.size() < 2){
					getResult("abc");
				}else{
					String allName = "";
					for(int i=1;i<part_list.size();i++){
						allName = allName+part_list.get(i).getPart_name()+" ";
					}
					getResult(allName.trim());
				}
			}else{
				getResult(name);
			}
			break;
		default:
			break;
		}
	}

	private void getResult(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		data.putInt("type", typeNum);
		Utils.goOtherWithDataActivity(FindByTypeActivity.this, ResultShopLisOnetActivity.class, data);
	}
	private void getResultTitle(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		data.putInt("type", typeNum);
		Utils.goOtherWithDataActivity(FindByTypeActivity.this, ResultShopListActivity.class, data);
	}
	private void getData(String url) {
		showLoadingDialog();
		VolleyRequest.getInstance().RequestGet(FindByTypeActivity.this, 
				Constant.URL_TEST+url, TAG, new VolleyInterface(this, VolleyInterface.mListener, VolleyInterface.mErrorListtener) {

			@Override
			public void onMySuccess(String result) {
				dealData(result);
				dismissLoadingDialog();
			}

			@Override
			public void onMyError(VolleyError error) {
				// TODO Auto-generated method stub
				dismissLoadingDialog();
				Utils.showText(FindByTypeActivity.this, "网络访问失败");
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
			baseInfo = gson.fromJson(result, BaseDXInfo.class);
			if(baseInfo != null){
				data = baseInfo.getData();
				part_list = data.get(0).getPart_list();
				if(data.size() != 0){
					oneAdapter.refresh(data);
					oneAdapter.setPosition(0);
					oneAdapter.changeView();
					twoAdapter.refresh(part_list);
					twoAdapter.setPosition(0);
					twoAdapter.changeView();
				}
			}
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
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
				Utils.showText(FindByTypeActivity.this, "网络访问失败");
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
				Intent i = new Intent(FindByTypeActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
