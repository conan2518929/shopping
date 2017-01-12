package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.CarTypeLvOneAdapter;
import com.car.shopping.adapter.CarTypeLvThreeAdapter;
import com.car.shopping.adapter.CarTypeLvTwoAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseCarTypeInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.CarModelInfo;
import com.car.shopping.entity.CarNameInfo;
import com.car.shopping.entity.CarTypeOneInfo;
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

public class CarTypeActivity extends BaseActivity implements OnItemClickListener{
	
	private LinearLayout back,ll_talk_list;
	private ListView lv_one,lv_two,lv_three; 
	private CarTypeLvOneAdapter typeOneAdapter;
	private CarTypeLvTwoAdapter typeTwoAdapter;
	private CarTypeLvThreeAdapter typeThreeAdapter;
	
	private ImageView btn_search;
	private EditText search_et;
	
	private BaseCarTypeInfo baseCarTypeInfo;
	private List<CarTypeOneInfo> datas;
	private List<CarNameInfo> brand_list;
	private List<CarModelInfo> series_list;
	
	private CarTypeOneInfo info_one;
	private CarNameInfo info_two;
	private List<CarModelInfo> info_three;
	
	private int num =0;
	private Button show_talk_num;
	private String carName = "";
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data1;
	private Map<String, String> map = UtilMap.getInstance().init();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cartype);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		show_talk_num = getView(R.id.show_talk_num);
		lv_one = getView(R.id.lv_one);
		lv_two = getView(R.id.lv_two);
		lv_three = getView(R.id.lv_three);
		back = getView(R.id.back);
		btn_search = getView(R.id.btn_search);
		search_et = getView(R.id.search_et);
		ll_talk_list = getView(R.id.ll_talk_list);
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
		num = getIntent().getExtras().getInt("data");
		datas = new ArrayList<>();
		brand_list = new ArrayList<>();
		series_list = new ArrayList<>();
		data1 = new  ArrayList<>();
		
		typeOneAdapter = new CarTypeLvOneAdapter(CarTypeActivity.this, datas, R.layout.item_car_type_one);
		typeTwoAdapter = new CarTypeLvTwoAdapter(CarTypeActivity.this, brand_list, R.layout.item_car_type_two);
		typeThreeAdapter = new CarTypeLvThreeAdapter(CarTypeActivity.this, series_list, R.layout.item_car_type_three);
		lv_one.setAdapter(typeOneAdapter);
		lv_two.setAdapter(typeTwoAdapter);
		lv_three.setAdapter(typeThreeAdapter);
		lv_one.setOnItemClickListener(this);
		lv_two.setOnItemClickListener(this);
		lv_three.setOnItemClickListener(this);
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		ll_talk_list.setOnClickListener(new OnClickListener() {
			
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
					Utils.showText(CarTypeActivity.this,"请您登录后进行聊天...");
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
					Utils.showShortToast(CarTypeActivity.this, "请您输入要搜索的商品...");
				}
			}
		});
		
		getData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		switch (parent.getId()) {
		
		case R.id.lv_one:
			info_one = datas.get(position);
			info_two = info_one.getBrand_list().get(0);
			info_three = info_two.getSeries_list();
			
			carName = info_two.getBrand_name();
			
			typeOneAdapter.setPosition(position);
			typeOneAdapter.changeView();
			
			typeTwoAdapter.setPosition(0);
			typeTwoAdapter.changeView();
			
			typeTwoAdapter.refresh(info_one.getBrand_list());
			typeThreeAdapter.refresh(info_three);
			break;
		case R.id.lv_two:
			info_two = info_one.getBrand_list().get(position);
			info_three = info_two.getSeries_list();
			carName = info_two.getBrand_name();
			typeTwoAdapter.setPosition(position);
			typeTwoAdapter.changeView();
			
			typeThreeAdapter.refresh(info_three);
			
			break;
		case R.id.lv_three:
			if((info_three.get(position).getSeries_name()).equals("全部")){
				String strAll = "";
				int num = info_three.size();
				if(num >1){
					for(int i=1;i<num;i++){
						strAll = strAll + info_three.get(i).getSeries_name() + " ";
					}
					System.out.println("====strAll===="+strAll);
//					getResult(carName);
					getResult(strAll);
				}else{
					Utils.showText(CarTypeActivity.this, "没有数据...");
				}
			}else{
				getResult(info_three.get(position).getSeries_name());
			}
			break;
		default:
			break;
		}
	}

	private void getResult(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		data.putInt("type", 2);
		Utils.goOtherWithDataActivity(CarTypeActivity.this, ResultShopLisOnetActivity.class, data);
	}
	private void getResultTitle(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		data.putInt("type", 2);
		Utils.goOtherWithDataActivity(CarTypeActivity.this, ResultShopListActivity.class, data);
	}
	
	private void getData() {
		showLoadingDialog();
		VolleyRequest.getInstance().RequestGet(CarTypeActivity.this, 
				Constant.URL_TEST+Urls.AM_TYPE, TAG, new VolleyInterface(this, VolleyInterface.mListener, VolleyInterface.mErrorListtener) {

			@Override
			public void onMySuccess(String result) {
				dealData(result);
				dismissLoadingDialog();
			}

			@Override
			public void onMyError(VolleyError error) {
				dismissLoadingDialog();
				Utils.showText(CarTypeActivity.this, "网络访问失败");
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
			baseCarTypeInfo = gson.fromJson(result, BaseCarTypeInfo.class);
			if(baseCarTypeInfo != null){
				datas = baseCarTypeInfo.getData();
				brand_list = datas.get(0).getBrand_list();
				series_list = brand_list.get(0).getSeries_list();
				
				if(datas.size() != 0){
					info_one = datas.get(num);
					info_two = info_one.getBrand_list().get(0);
					info_three = info_two.getSeries_list();
					carName = info_two.getBrand_name();
					typeOneAdapter.setPosition(num);
					typeOneAdapter.refresh(datas);
					typeOneAdapter.changeView();
					typeTwoAdapter.setPosition(0);
					typeTwoAdapter.refresh(info_one.getBrand_list());
					typeTwoAdapter.changeView();
					typeThreeAdapter.refresh(info_two.getSeries_list());
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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
				Utils.showText(CarTypeActivity.this, "网络访问失败");
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
				Intent i = new Intent(CarTypeActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
