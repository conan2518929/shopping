package com.car.shopping.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.activity.ContactsListActivity;
import com.car.shopping.activity.ContactsListOneActivity;
import com.car.shopping.activity.LoginActivity;
import com.car.shopping.activity.MainActivity;
import com.car.shopping.activity.ShopDetailActivity;
import com.car.shopping.adapter.AddressBook2Adapter;
import com.car.shopping.adapter.AddressBook2Adapter.MyClickListener;
import com.car.shopping.adapter.AddressBookAdapter;
import com.car.shopping.adapter.AddressBookAdapter.MyClickListenerOne;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseInterfaces;
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

@SuppressLint({ "CutPasteId", "ViewHolder", "InflateParams" })
public class AddressBookFragment extends BaseFragment implements BaseInterfaces, OnItemClickListener, OnClickListener {

	private final String TAG = "AddressBookFragment";
	private MyBroadcastReciver myBroadcast;
	private View layoutView;
	private AddressBookAdapter adapter;// 通话记录
	private AddressBook2Adapter contactsAdapter;// 联系人
	private List<AddressBookInfo> list;// 通话记录
	private List<AddressBookInfo> contactsList;// 联系人

	private SilderListView mListView;
	private SilderListView contactsLv;
	private Dialog dialog;
	private Display display;

	private TextView tv_one, tv_two;
	private MyAdapter myAdapter;
	private ListView lv;
	private String[] letter = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private String[] letter1;
	private List<String> letterToCity = new ArrayList<String>();
	private Map<String, AddressBookInfo> maps = new HashMap<String, AddressBookInfo>();
	private RelativeLayout rl_list;

	private BaseContactsListInfo baseInfo;
	private List<TelsInfo> tels;
	private String numPhone = "";
	private String callId = "";
	private Button show_talk_num;
	
	private LinearLayout ll_talk;
//	private Handler h = new Handler();
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo>data;
	private Map<String, String> map = UtilMap.getInstance().init();
	
	private String shopId = "";
	private String input = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.fragment_addressbook, container, false);
		initViews();
		initData();
		return layoutView;
	}

	@Override
	public void initViews() {
		show_talk_num = (Button) layoutView.findViewById(R.id.show_talk_num);
		mListView = (SilderListView) layoutView.findViewById(R.id.list);
		contactsLv = (SilderListView) layoutView.findViewById(R.id.lv);
		tv_one = (TextView) layoutView.findViewById(R.id.tv_one);
		tv_two = (TextView) layoutView.findViewById(R.id.tv_two);
		lv = (ListView) layoutView.findViewById(R.id.lv_zimu);
		rl_list = (RelativeLayout) layoutView.findViewById(R.id.rl_list);
		ll_talk = (LinearLayout) layoutView.findViewById(R.id.ll_talk_list);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		list = new ArrayList<>();
		tels = new ArrayList<>();
		data = new ArrayList<>();
		contactsList = new ArrayList<>();
		letter1 = new String[contactsList.size()];
		adapter = new AddressBookAdapter(getActivity(), list, mListenerOne);
		mListView.setAdapter(adapter);

		contactsAdapter = new AddressBook2Adapter(getActivity(), letterToCity, maps, mListener);
		contactsLv.setAdapter(contactsAdapter);

		myAdapter = new MyAdapter();
		lv.setAdapter(myAdapter);

		mListView.setOnItemClickListener(this);
		lv.setOnItemClickListener(this);
		contactsLv.setOnItemClickListener(this);
		mListView.setOnItemClickListener(this);
		contactsLv.setOnItemClickListener(this);
		tv_one.setOnClickListener(this);
		tv_two.setOnClickListener(this);
		ll_talk.setOnClickListener(this);

		getContactsList();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = AppContext.mSharedPref.getSharePrefInteger(SharedPrefConstant.TALK_NUMBER,0);
		show_talk_num.setText(""+unreadNum);
	}
	
	
	/**
	 * 最近联系人
	 * */
	private void getContactsList() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_SHOP_CALLED, new Response.Listener<String>() {

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
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	/**
	 * 通讯录列表
	 * */
	private void getGoodFreindsList() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_SHOP_SAVED, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealFriendsData(result);
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

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseContactsListInfo.class);
			if (baseInfo != null) {
				list = baseInfo.getData();
				adapter.refresh(list);
			}
		} else if (status == 300) {
			logout();
		}
	}

	private void dealFriendsData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseContactsListInfo.class);
			if (baseInfo != null) {
				contactsList = baseInfo.getData();
				if (contactsList != null && contactsList.size() > 0) {
					maps.clear();
					for (int m = 0; m < contactsList.size(); m++) {
						maps.put(contactsList.get(m).getId(), contactsList.get(m));
					}
					letter1 = null;
					letter1 = new String[contactsList.size()];
					for (int i = 0; i < contactsList.size(); i++) {
						letter1[i] = contactsList.get(i).getPy();
					}

					String str = "";
					letterToCity.clear();
					for (int i = 0; i < letter.length; i++) {
						str = letter[i];
						boolean isAddLetter = false;
						for (int j = 0; j < letter1.length; j++) {
							if (str.equals(letter1[j])) {
								if (!isAddLetter) {
									letterToCity.add(str);
									isAddLetter = true;
								}
								letterToCity.add(contactsList.get(j).getId());
							}
						}
					}
					contactsAdapter.refresh(letterToCity, maps);
				}
			}
		} else if (status == 300) {
			logout();
		}
	}

	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:
				AddressBookInfo item_txl = maps.get(letterToCity.get(position));
				showDialog(item_txl);
				break;
			case R.id.holder:
				AddressBookInfo item = maps.get(letterToCity.get(position));
				deleteContactsList(item);
				break;
			case R.id.tv_talk:
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if(isShow){
					AddressBookInfo item1 = maps.get(letterToCity.get(position));
					String name = item1.getIm_username();
					String nickName = item1.getShop_name();
					if(name != null && name.length() != 0){
						updateAlias(name,nickName);
					}
				}else{
					Utils.showText(getActivity(),"请您登录后进行聊天...");
				}
				break;
			case R.id.tv_beizhu:
				shopId = letterToCity.get(position);
				final EditText et = new EditText(getActivity());  
				new Builder(getActivity())
					.setTitle("请填写备注呢名称")  
					.setView(et)  
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
				    public void onClick(DialogInterface dialog, int which) {  
				    input = et.getText().toString().trim();  
				    if (input.equals("")) {  
				        Toast.makeText(getActivity(), "请输入备注呢名称！" + input, Toast.LENGTH_LONG).show();  
				    }else{  
				        	if(input.length() <= 10){
				        		changeNickName(input,shopId);
				        	}else{
				        		Toast.makeText(getActivity(), "请输入10个字以内的昵称" + input, Toast.LENGTH_LONG).show();  
				        	}
				        }  
				    }  
				    })  
				.setNegativeButton("取消", null)  
				.show();  
				break;
			default:
				break;
				
				
			}
		}
	};

	/**
	 * 取消收藏
	 * */
	private void deleteContactsList(final AddressBookInfo item) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.DELETE_SHOP_SAVED, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealFriendsData(result);
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
				String id = item.getId();
				params.put("shop_id", id);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void getDetail(String id) {
		Bundle data = new Bundle();
		data.putString("shop_id", id);
		Utils.goOtherWithDataActivity(getActivity(), ShopDetailActivity.class, data);
	}

	private MyClickListenerOne mListenerOne = new MyClickListenerOne() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:
				AddressBookInfo item_lxr = list.get(position);
				showDialog(item_lxr);
				break;
			case R.id.holder:
				deleteContactsList(position);
				break;
			case R.id.tv_talk:
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if(isShow){
					String name = list.get(position).getIm_username();
					String nickName = list.get(position).getShop_name();
					if(name != null && name.length() != 0){
						updateAlias(name,nickName);
					}
				}else{
					Utils.showText(getActivity(),"请您登录后进行聊天...");
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.lv_zimu:
			String str = letter[position];
			for (int n = 0; n < letterToCity.size(); n++) {
				if (str.equals(letterToCity.get(n))) {
					contactsAdapter.setPosition(n + 1);
					contactsAdapter.changeView();
					contactsLv.setSelection(n);
				}
			}
			break;
		case R.id.list:
			String idList = list.get(position).getId();
			getDetail(idList);
			break;
		case R.id.lv:
			boolean isGo = true;
			String strLetter = letterToCity.get(position);
			for (int i = 0; i < letter.length; i++) {
				if (letter[i].equals(strLetter)) {
					isGo = false;
					return;
				}
			}
			if (isGo) {
				AddressBookInfo item = maps.get(strLetter);
				String idContactsList = item.getId();
				getDetail(idContactsList);
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		
		boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
		switch (v.getId()) {
		case R.id.tv_one:
			initColor();
			tv_one.setTextColor(getActivity().getResources().getColor(R.color.white));
			tv_one.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_title_search));
			mListView.setVisibility(View.VISIBLE);
			contactsLv.setVisibility(View.GONE);//通讯录
			lv.setVisibility(View.GONE);
			if (isShow) {
				rl_list.setVisibility(View.VISIBLE);
				getContactsList();
			}else{
				rl_list.setVisibility(View.GONE);
				Utils.showText(getActivity(), "请您登录后查看列表...");
			}
			break;
		case R.id.tv_two:
			initColor();
			tv_two.setTextColor(getActivity().getResources().getColor(R.color.white));
			tv_two.setBackgroundColor(getActivity().getResources().getColor(R.color.blue_title_search));
			mListView.setVisibility(View.GONE);
			contactsLv.setVisibility(View.VISIBLE);
			lv.setVisibility(View.VISIBLE);
			if (isShow) {
				rl_list.setVisibility(View.VISIBLE);
				getGoodFreindsList();
			}else{
				rl_list.setVisibility(View.GONE);
				Utils.showText(getActivity(), "请您登录后查看列表...");
			}
			break;
		case R.id.ll_talk_list:
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
			break;
		default:
			break;
		}
	}

	private void initColor() {
		tv_one.setTextColor(getActivity().getResources().getColor(R.color.black_two));
		tv_one.setBackgroundColor(getActivity().getResources().getColor(R.color.gray_five));
		tv_two.setTextColor(getActivity().getResources().getColor(R.color.black_two));
		tv_two.setBackgroundColor(getActivity().getResources().getColor(R.color.gray_five));

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return letter.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return letter[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_blue_letter, null);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl);
			tv.setText(letter[position]);
			return view;
		}
	}

	/**
	 * 删除通话记录
	 * */
	private void deleteContactsList(final int position) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.DELETE_SHOP_CALLED, new Response.Listener<String>() {

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
				Map<String, String> params = new HashMap<String, String>();
				params.put("record_id", list.get(position).getRecord_id());
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	@SuppressLint({ "InflateParams", "RtlHardcoded" })
	@SuppressWarnings("deprecation")
	private void showDialog(final AddressBookInfo item_lxr) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager) (getActivity().getSystemService(Context.WINDOW_SERVICE));
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		tels = item_lxr.getTels();
		callId = item_lxr.getId();
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
				numPhone = tels.get(0).getTel();
				getCallShop(callId, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tels.get(1).getTel();
				getCallShop(callId, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tels.get(2).getTel();
				getCallShop(callId, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tels.get(3).getTel();
				getCallShop(callId, numPhone);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tels.get(4).getTel();
				getCallShop(callId, numPhone);
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
		dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	public void initBroadcast() {
		IntentFilter intentFilter = new IntentFilter();
		myBroadcast = new MyBroadcastReciver();
		intentFilter.addAction("com.app.action.broadcast_address");
		getActivity().registerReceiver(myBroadcast, intentFilter);
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals("com.app.action.broadcast_address")) {
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					rl_list.setVisibility(View.VISIBLE);
				} else {
					rl_list.setVisibility(View.GONE);
				}
			}
		}
	}

	private void unregisterReceiver() {
		if(myBroadcast != null){
			getActivity().unregisterReceiver(myBroadcast);	
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver();
		super.onDestroy();
	}

	/**
	 * 拨打电话记录
	 * */
	private void getCallShop(final String id, final String num) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.CALL_SHOP, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealTelResult(result);
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
				params.put("shop_id", id);
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
			AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			Intent intent = new Intent();
			intent.setAction("com.app.action.broadcast");
			getActivity().sendBroadcast(intent);
			Intent i = new Intent(getActivity(),LoginActivity.class);
			getActivity().startActivity(i);
		}
	}
	
	private void updateAlias(String name,String nickName) {
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, name);
		intent.putExtra("user_nickname", nickName);
		startActivity(intent);
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
					data = baseTalkNickName.getData();
					if(data.size() > 0){
						for(int i=0;i<data.size();i++){
							map.put(data.get(i).getIm_username(), data.get(i).getNickname());
						}	
					}
					Intent i = new Intent(getActivity(), LianXiRenListActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}
			}
		}

	    /**
		 * 更改昵称
		 * */
		private void changeNickName(final String remarks,final String shop_id) {
			AppContext.getInstance().cancelPendingRequests(TAG);
			StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_RENAME_SAVE_SHOP, new Response.Listener<String>() {

				@Override
				public void onResponse(String result) {
					showChangeResult(result);
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
					System.out.println("===传递的值==="+remarks+","+shop_id);
					params.put("alias", remarks);
					params.put("shop_id", shop_id);
					return params;
				}
			};
			AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
		}
		
		private void showChangeResult(String result){
			Gson gson = new Gson();
			StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
			int status = statusInfo.getStatus();
			if (status == 200) {
				getGoodFreindsList();
			}else{
				Utils.showText(getActivity(), "修改备注失败...请您重新尝试...");
			}
		}
}
