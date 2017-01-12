package com.car.shopping.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.activity.CarBrandActivity;
import com.car.shopping.activity.CarTypeActivity;
import com.car.shopping.activity.FindByTypeActivity;
import com.car.shopping.activity.MainActivity;
import com.car.shopping.activity.ResultShopListActivity;
import com.car.shopping.adapter.CarTypeLvAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseInterfaces;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseSearchListInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.SearchListInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.http.VolleyInterface;
import com.car.shopping.http.VolleyRequest;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.utils.NetworkReceiver;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.NetworkReceiver.INetwork;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

public class SearchFragment extends BaseFragment implements BaseInterfaces,INetwork{
	
	private final String TAG = "SearchFragment";
	private View layoutView;
	private ListView lv;
	private CarTypeLvAdapter carTypeLvAdapter;
	private ImageView btn_search;
	private EditText search_et;
	
	private BaseSearchListInfo baseInfo;
	private List<SearchListInfo> data;
	
	private boolean isHasNet = false;
	private boolean isHasNet1 = false;
	private Button show_talk_num;
	private LinearLayout ll_talk;
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo>data1;
	private Map<String, String> map = UtilMap.getInstance().init();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.fragment_search, container, false);
		initViews();
		initData();
		registerReceiver();
		return layoutView;
	}

	@Override
	public void initViews() {
		show_talk_num = (Button) layoutView.findViewById(R.id.show_talk_num);
		lv = (ListView) layoutView.findViewById(R.id.lv);
		ll_talk = (LinearLayout) layoutView.findViewById(R.id.ll_talk_list);
		btn_search = (ImageView) layoutView.findViewById(R.id.btn_search);
		search_et = (EditText) layoutView.findViewById(R.id.search_et);
	}

	
	@Override
	public void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = AppContext.mSharedPref.getSharePrefInteger(SharedPrefConstant.TALK_NUMBER);
		show_talk_num.setText(""+unreadNum);
	}

	@Override
	public void initData() {
		data = new ArrayList<>();
		data1 = new ArrayList<>();
		carTypeLvAdapter = new CarTypeLvAdapter(getActivity(), data, R.layout.car_type_item);
		lv.setAdapter(carTypeLvAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String num = data.get(position).getType();
				switch (num) {
				case "1":
					getDate(position);
					break;
				case "2":
					Utils.gotoOtherActivity(getActivity(), CarBrandActivity.class);
					break;
				case "3":
					gotoByType(7);
					break;
				case "4":
					gotoByType(8);
					break;

				default:
					break;
				}
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String mResult = search_et.getText().toString().trim();
				if(mResult != null && !mResult.equals("")){
					getResult(mResult);
				}else{
					Utils.showShortToast(getActivity(), "请您输入要搜索的商品...");
				}
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
					Utils.showText(getActivity(),"请您登录后进行聊天...");
				}
			}
		});
		
		getData();
	}
	
	private void getData() {
		((MainActivity)getActivity()).showLoadingDialog();
		VolleyRequest.getInstance().RequestGet(getActivity(), 
				Constant.URL_TEST+Urls.SEARCH_LIST, TAG, new VolleyInterface(getActivity(), VolleyInterface.mListener, VolleyInterface.mErrorListtener) {

			@Override
			public void onMySuccess(String result) {
				dealData(result);
				((MainActivity)getActivity()).dismissLoadingDialog();
			}

			@Override
			public void onMyError(VolleyError error) {
				((MainActivity)getActivity()).dismissLoadingDialog();
				Utils.showText(getActivity(), "网络访问失败");
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
			baseInfo = gson.fromJson(result, BaseSearchListInfo.class);
			data = baseInfo.getData();
			carTypeLvAdapter.refresh(data);
		}
	}
	
	private void getResult(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		Utils.goOtherWithDataActivity(getActivity(), ResultShopListActivity.class, data);
	}
	
	
	private void getDate(int position){
		Bundle data = new Bundle();
		data.putInt("data", position);
		Utils.goOtherWithDataActivity(getActivity(),CarTypeActivity.class,data);
	}
	
	private void gotoByType(int position){
		Bundle data = new Bundle();
		data.putInt("type", position);
		Utils.goOtherWithDataActivity(getActivity(),FindByTypeActivity.class,data);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	public NetworkReceiver receiver;

	private void unregisterReceiver() {
		if (null != receiver)
			getActivity().unregisterReceiver(receiver);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new NetworkReceiver();
		getActivity().registerReceiver(receiver, filter);
		receiver.setNetWorkListener(this);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}
	
	@Override
	public void hasNet() {
		if (isHasNet1 && !isHasNet) {
			getData();	
		}
		isHasNet = true;
	}

	@Override
	public void notNet() {
		isHasNet1 = true;
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
