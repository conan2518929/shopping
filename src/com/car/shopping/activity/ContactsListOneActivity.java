package com.car.shopping.activity;

import java.util.ArrayList;
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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.AddressBook2Adapter;
import com.car.shopping.adapter.AddressBook2Adapter.MyClickListener;
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

@SuppressLint("ViewHolder")
public class ContactsListOneActivity extends BaseActivity {

	private LinearLayout back;
	private AddressBook2Adapter contactsAdapter;// 联系人
	private List<AddressBookInfo> contactsList;// 联系人
	private SilderListView contactsLv;
	private Dialog dialog;
	private Display display;

	private MyAdapter myAdapter;
	private ListView lv;
	private String[] letter = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private String[] letter1;
	private List<String> letterToCity = new ArrayList<String>();
	private Map<String, AddressBookInfo> maps = new HashMap<String, AddressBookInfo>();

	private BaseContactsListInfo baseInfo;
	private List<TelsInfo> tels;
	private String numPhone = "";
	private String callId = "";
	private Button show_talk_num;
	private Handler h = new Handler();
	private LinearLayout ll_talk;
	private String shopId = "";
	private String input = "";
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data;
	private Map<String, String> map = UtilMap.getInstance().init();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_one);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		lv = getView(R.id.lv_zimu);
		contactsLv = getView(R.id.lv);
		show_talk_num = getView(R.id.show_talk_num);
		ll_talk = getView(R.id.ll_talk_list);
	}

	@Override
	public void initData() {
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
					Utils.showText(ContactsListOneActivity.this, "请您登录后进行聊天...");
				}
			}
		});
		tels = new ArrayList<>();
		contactsList = new ArrayList<>();
		contactsAdapter = new AddressBook2Adapter(ContactsListOneActivity.this, letterToCity, maps, mListener);
		contactsLv.setAdapter(contactsAdapter);

		myAdapter = new MyAdapter();
		lv.setAdapter(myAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String str = letter[position];
				for (int n = 0; n < letterToCity.size(); n++) {
					if (str.equals(letterToCity.get(n))) {
						contactsAdapter.setPosition(n + 1);
						contactsAdapter.changeView();
						contactsLv.setSelection(n);
					}
				}
			}
		});
		contactsLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				boolean isGo = true;
				String strLetter = letterToCity.get(position);
				for (int i = 0; i < letter.length; i++) {
					if (letter[i].equals(strLetter)) {
						isGo = false;
						return;
					}
				}
				if (isGo) {
					getDetail(position, strLetter);
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
		show_talk_num.setText("" + unreadNum);
	}

	/**
	 * 初始化通讯录数据
	 * */
	private void getData() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_SHOP_SAVED, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("获取收藏人列表数据="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ContactsListOneActivity.this, "网络访问失败");
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
		dismissLoadingDialog();
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
				Utils.showText(ContactsListOneActivity.this, "网络访问失败");
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
			Intent i = new Intent(ContactsListOneActivity.this, LoginActivity.class);
			startActivity(i);
			Utils.showText(ContactsListOneActivity.this, "由于您长时间未操作....请您重新登录账号...");
		}
	}

	private void getDetail(int position, String id) {
		Bundle data = new Bundle();
		data.putString("shop_id", id);
		Utils.goOtherWithDataActivity(ContactsListOneActivity.this, ShopDetailActivity.class, data);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return letter.length;
		}

		@Override
		public Object getItem(int position) {
			return letter[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = LayoutInflater.from(ContactsListOneActivity.this).inflate(R.layout.item_blue_letter, null);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			tv.setText(letter[position]);
			return view;
		}
	}

	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:
				AddressBookInfo item_click = maps.get(letterToCity.get(position));
				showDialog(item_click);
				break;
			case R.id.tv_talk:
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					AddressBookInfo item = maps.get(letterToCity.get(position));
					String name = item.getIm_username();
					if (name != null && name.length() != 0) {
						updateAlias(name, position);
					}
				} else {
					Utils.showText(ContactsListOneActivity.this, "请您登录后进行聊天...");
				}
				break;
			case R.id.holder:
				AddressBookInfo item = maps.get(letterToCity.get(position));
				deleteContactsList(item);
				break;
			case R.id.tv_beizhu:
				shopId = letterToCity.get(position);
				final EditText et = new EditText(ContactsListOneActivity.this);  
				new Builder(ContactsListOneActivity.this)
					.setTitle("请填写备注呢名称")  
					.setView(et)  
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
				    public void onClick(DialogInterface dialog, int which) {  
				    input = et.getText().toString().trim();  
				    if (input.equals("")) {  
				        Toast.makeText(getApplicationContext(), "请输入备注呢名称！" + input, Toast.LENGTH_LONG).show();  
				    }else{  
				        	if(input.length() <= 10){
				        		changeNickName(input,shopId);
				        	}else{
				        		Toast.makeText(getApplicationContext(), "请输入10个字以内的昵称" + input, Toast.LENGTH_LONG).show();  
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

	private void updateAlias(String name, int position) {
		if (baseInfo != null) {
			Intent intent = new Intent(ContactsListOneActivity.this, ChatActivity.class);
			intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, baseInfo.getData().get(position).getIm_username());
			intent.putExtra("user_nickname", baseInfo.getData().get(position).getShop_name());
			startActivity(intent);
		}
	}

	/**
	 * 显示电话列表
	 * */
	@SuppressLint({ "InflateParams", "RtlHardcoded" })
	@SuppressWarnings("deprecation")
	private void showDialog(final AddressBookInfo item_click) {
		View view = LayoutInflater.from(ContactsListOneActivity.this).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		tels = item_click.getTels();
		callId = item_click.getId();
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
		dialog = new Dialog(ContactsListOneActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	private void getCallShop(final String id, final String num) {
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
				Utils.showText(ContactsListOneActivity.this, "网络访问失败");
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

	/**
	 * 取消收藏
	 * */
	private void deleteContactsList(final AddressBookInfo item) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.DELETE_SHOP_SAVED, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealData(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ContactsListOneActivity.this, "网络访问失败");
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

	@Override
	protected void onStop() {
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
				dealReturnNickName(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ContactsListOneActivity.this, "网络访问失败");
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
				data = baseTalkNickName.getData();
				if (data.size() > 0) {
					for (int i = 0; i < data.size(); i++) {
						map.put(data.get(i).getIm_username(), data.get(i).getNickname());
					}
				}
				Intent i = new Intent(ContactsListOneActivity.this, LianXiRenListActivity.class);
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
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("修改备注="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				showChangeResult(result);
				
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ContactsListOneActivity.this, "网络访问失败");
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
			getData() ;
//			contactsAdapter.refresh(letterToCity, maps);
		}else{
			Utils.showText(ContactsListOneActivity.this, "修改备注失败...请您重新尝试...");
		}
	}
}
