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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

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
import com.car.shopping.entity.BaseShopDetail;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.BrandsInfo;
import com.car.shopping.entity.ImageUrlInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.TagsInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.entity.TelsNumInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.ChatActivity;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.AutoPagerView;
import com.car.shopping.widget.FlowLayout;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")
public class TalkShopDetailActivity extends BaseActivity implements OnClickListener{
	
	private LinearLayout back,ll_phone,ll_two,ll_talk,ll_talk_list;
	private AutoPagerView pagerView;
	private Dialog dialog;
	private Display display;
	private TextView add_txl,had_add_txl,pay;
	private TextView shopname,cartype,address,tv_phoneNum_one,tv_num,jianying,show_miaosu;
	private String username = "";
	private boolean isAdd = false;
	private BaseShopDetail baseInfo;
	private int num = 0;
	private List<ImageUrlInfo> lunbotu;
	private List<BrandsInfo> brands;
	private ImageView pic1,pic2,pic3,pic4,pic5,pic6,pic7,pic8,pic9,pic10;
	private List<TelsNumInfo> tels;
	private List<TagsInfo> tags;
	private FlowLayout flowlayout;
	private TextView tv1,tv2,tv3,tv4;
	private String numPhone = "";
	private Button show_talk_num;
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data;
	private Map<String, String> map = UtilMap.getInstance().init();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopdetail);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		show_talk_num = getView(R.id.show_talk_num);
		back = getView(R.id.back);
		ll_two = getView(R.id.ll_two);
		pay = getView(R.id.pay);
		ll_phone = getView(R.id.ll_phone);
		add_txl = getView(R.id.add_txl);
		had_add_txl = getView(R.id.had_add_txl);
		ll_phone = getView(R.id.ll_phone);
		pagerView = getView(R.id.auto_pagerview);
		shopname = getView(R.id.shopname);
		cartype = getView(R.id.cartype);
		address = getView(R.id.address);
		tv_num = getView(R.id.tv_num);
		show_miaosu = getView(R.id.show_miaosu);
		tv_phoneNum_one = getView(R.id.tv_phoneNum_one);
		jianying = getView(R.id.jianying);
		flowlayout = getView(R.id.flowlayout);
		tv1 = getView(R.id.tv1);
		tv2 = getView(R.id.tv2);
		tv3 = getView(R.id.tv3);
		tv4 = getView(R.id.tv4);
		pic1 = getView(R.id.pic1);
		pic2 = getView(R.id.pic2);
		pic3 = getView(R.id.pic3);
		pic4 = getView(R.id.pic4);
		pic5 = getView(R.id.pic5);
		pic6 = getView(R.id.pic6);
		pic7 = getView(R.id.pic7);
		pic8 = getView(R.id.pic8);
		pic9 = getView(R.id.pic9);
		pic10 = getView(R.id.pic10);
		ll_talk = getView(R.id.ll_talk);
		ll_talk_list = getView(R.id.ll_talk_list);
	}

	@Override
	public void initData() {
		username = getIntent().getExtras().getString("username");
		lunbotu = new ArrayList<>();
		brands = new ArrayList<>();
		tels = new ArrayList<>();
		tags = new ArrayList<>();
		data = new ArrayList<>();
		initGallery();
		pay.setOnClickListener(this);
		back.setOnClickListener(this);
		ll_talk.setOnClickListener(this);
		add_txl.setOnClickListener(this);
		had_add_txl.setOnClickListener(this);
		ll_talk_list.setOnClickListener(this);
		ll_phone.setOnClickListener(this);
		if(username != null && username.length() > 0){
			getShopData();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
		show_talk_num.setText("" + unreadNum);
	}

	private void getShopData() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.GET_SHOP_DETAIL_IM, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
					dealData(result);
					dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(TalkShopDetailActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<>();
				map.put("shop_im_username", username);
				return map;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if(statusInfo == null){
			return;
		}
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseShopDetail.class);
			if(baseInfo != null){
				lunbotu = baseInfo.getLunbotu();
				brands = baseInfo.getBrands();
				tels = baseInfo.getTels();
				tags = baseInfo.getTags();
				num = lunbotu.size();
				initGallery();
				show_miaosu.setText(baseInfo.getDescription());
				shopname.setText(baseInfo.getShop_name());
				
				int brandsNum = brands.size();
				if(brandsNum > 0){
					pic1.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(0).getBrand(),pic1);
				}
				if(brandsNum > 1){
					pic2.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(1).getBrand(),pic2);
				}
				if(brandsNum > 2){
					pic3.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(2).getBrand(),pic3);
				}
				if(brandsNum > 3){
					pic4.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(3).getBrand(),pic4);
				}
				if(brandsNum > 4){
					pic5.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(4).getBrand(),pic5);
				}
				if(brandsNum > 5){
					ll_two.setVisibility(View.VISIBLE);
					pic6.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(5).getBrand(),pic6);
				}
				if(brandsNum > 6){
					pic7.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(6).getBrand(),pic7);
				}
				if(brandsNum > 7){
					pic8.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(7).getBrand(),pic8);
				}
				if(brandsNum > 8){
					pic9.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(8).getBrand(),pic9);
				}
				if(brandsNum > 9){
					pic10.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(9).getBrand(),pic10);
				}
				cartype.setText(baseInfo.getAm_series());
				address.setText(baseInfo.getAddress());
				if(tels.size() > 0){
					tv_phoneNum_one.setText(tels.get(0).getTel());
					int n = 0;
					for(int i=0;i<tels.size();i++){
						n = n+Integer.parseInt(tels.get(i).getCall_count());
					}
					tv_num.setText(n+"");
				}
				jianying.setText(baseInfo.getJianying());
				
				int tagsNum = tags.size();
				if(tagsNum > 0){
					flowlayout.setVisibility(View.VISIBLE);
					tv1.setVisibility(View.VISIBLE);
					tv1.setText(tags.get(0).getTag());
				}
				if(tagsNum > 1){
					tv2.setVisibility(View.VISIBLE);
					tv2.setText(tags.get(1).getTag());
				}
				if(tagsNum > 2){
					tv3.setVisibility(View.VISIBLE);
					tv3.setText(tags.get(2).getTag());
				}
				if(tagsNum > 3){
					tv4.setVisibility(View.VISIBLE);
					tv4.setText(tags.get(3).getTag());
				}
				
				String strSaved = baseInfo.getSaved();
				if(strSaved != null && strSaved.equals("1")){
					isAdd = true;
					had_add_txl.setVisibility(View.VISIBLE);
					add_txl.setVisibility(View.GONE);
				}else if(strSaved != null && strSaved.equals("0")){
					isAdd = false;
					had_add_txl.setVisibility(View.GONE);
					add_txl.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	
	private void updateAlias(String name) {
		Intent intent = new Intent(TalkShopDetailActivity.this, ChatActivity.class);
		intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, baseInfo.getIm_username());
		intent.putExtra("user_nickname", baseInfo.getShop_name());
		startActivity(intent);
	}
	
	@Override
	public void onClick(View v) {
		boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
		switch (v.getId()) {
		case R.id.ll_phone:
			showPhoneNum();
			break;
		case R.id.ll_talk_list:// 聊天列表
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
				Utils.showText(TalkShopDetailActivity.this,"请您登录后进行聊天...");
			}
			break;
		case R.id.ll_talk:
			if(isShow && baseInfo != null){
				String name = baseInfo.getIm_username();
				if(name != null && name.length() != 0){
					updateAlias(name);
				}
			}else{
				Utils.showText(TalkShopDetailActivity.this,"请您登录后进行聊天...");
			}
			break;
		case R.id.back:
			finish();
			break;
		case R.id.pay:
			if(isShow){
				 gotoPay(baseInfo.getId());
			}else{
				Utils.showText(TalkShopDetailActivity.this,"请您登录后进行操作...");
			}
			break;
		case R.id.add_txl:
			if(isShow){
				if(!isAdd){
					SaveShop();
				}
			}else{
				Utils.showText(TalkShopDetailActivity.this,"请您登录后进行操作...");
			}
			break;
		case R.id.had_add_txl:
			if(isShow){
				if(isAdd){
					DeleteShop();
				}
			}else{
				Utils.showText(TalkShopDetailActivity.this,"请您登录后进行操作...");
			}
			break;
		default:
			break;
		}
	}

	private void gotoPay(String shop_id) {
		Bundle data = new Bundle();
		data.putString("shop_id", shop_id);
		Utils.goOtherWithDataActivity(TalkShopDetailActivity.this, PayWXActivity.class, data);
	}
	
	@SuppressLint("RtlHardcoded")
	@SuppressWarnings("deprecation")
	private void showPhoneNum(){
		View view = LayoutInflater.from(TalkShopDetailActivity.this).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		int num = tels.size();
		if( num == 1 ){
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
		}else if(num == 2){
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
		}else if(num == 3){
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
		}else if(num == 4){
			tv1.setVisibility(View.VISIBLE);
			tv1.setText(tels.get(0).getTel());
			tv2.setVisibility(View.VISIBLE);
			tv2.setText(tels.get(1).getTel());
			tv3.setVisibility(View.VISIBLE);
			tv3.setText(tels.get(2).getTel());
			tv4.setVisibility(View.VISIBLE);
			tv4.setText(tels.get(3).getTel());
		}else if(num > 4){
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
				getCallShop();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv2.getText().toString();
				getCallShop();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv3.getText().toString();
				getCallShop();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv4.getText().toString();
				getCallShop();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv5.getText().toString();
				getCallShop();
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
		dialog = new Dialog(TalkShopDetailActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}
	private void getCallShop() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.CALL_SHOP, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
					dealTelResult(result);
					dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(TalkShopDetailActivity.this, "网络访问失败");
			}
		}){

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", baseInfo.getId());
				params.put("tel", numPhone);
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
			
		}else if(status == 300){
				logout();
		}
	}
	
	private void DeleteShop() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.DELETE_SHOP_SAVED, new Response.Listener<String>() {
			
			@Override
			public void onResponse(String result) {
				dealDeleteShop(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(TalkShopDetailActivity.this, "网络访问失败");
			}
		}){
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", baseInfo.getId());
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	private void SaveShop() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.SAVE_SHOP, new Response.Listener<String>() {
			
			@Override
			public void onResponse(String result) {
				dealSaveShop(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(TalkShopDetailActivity.this, "网络访问失败");
			}
		}){
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", baseInfo.getId());
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealSaveShop(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			isAdd = true;
			had_add_txl.setVisibility(View.VISIBLE);
			add_txl.setVisibility(View.GONE);
		}else if(status == 300){
			logout();
		}
	}
	
	private void logout() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST+Urls.LOGOUT, new Response.Listener<String>() {
			
			@Override
			public void onResponse(String result) {
				dealLogoutShop(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(TalkShopDetailActivity.this, "网络访问失败");
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
			Intent i = new Intent(TalkShopDetailActivity.this,LoginActivity.class);
			startActivity(i);
		}
	}
	private void dealDeleteShop(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			isAdd = false;
			had_add_txl.setVisibility(View.GONE);
			add_txl.setVisibility(View.VISIBLE);
		}else if (status == 300) {
			logout();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}
	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		if (num == 0) {
		final int[] imgs = new int[] { R.drawable.failure_lunbotu, R.drawable.failure_lunbotu };
		for (int i = 0; i < imgs.length; i++) {
			ImageView imageview1 = new ImageView(TalkShopDetailActivity.this);
			imageview1.setScaleType(ScaleType.FIT_XY);
			imageview1.setImageResource(imgs[i]);
			pagePic.add(imageview1);
		}
		} else if(num > 0){
			final String[] imgArray = new String[num];
			for(int n=0;n<num; n++){
				imgArray[n] =Constant.URL_TEST+ lunbotu.get(n).getImage_url();
			}
			for (int i = 0; i < num; i++) {
				ImageView imageview = new ImageView(TalkShopDetailActivity.this);
				imageview.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(imgArray[i],imageview);
				imageview.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showImage(AutoPagerView.num,imgArray);
					}
				});
				pagePic.add(imageview);
			}
		}
		pagerView.setPageViewPics(pagePic, 0);
	}

	private void showImage(int position, String[] urls) {
		Intent intent = new Intent(TalkShopDetailActivity.this, ShowImageActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
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
				Utils.showText(TalkShopDetailActivity.this, "网络访问失败");
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
				Intent i = new Intent(TalkShopDetailActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
