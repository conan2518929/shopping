package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.car.shopping.entity.BaseTJYJInfo;
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
import com.car.shopping.utils.WXManager;
import com.car.shopping.widget.AutoPagerView;
import com.car.shopping.widget.FlowLayout;
import com.car.shopping.widget.MarqueeTextView;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")
public class ShopDetailActivity extends BaseActivity implements OnClickListener {

	private LinearLayout back, ll_phone, ll_two, ll_talk, ll_talk_list,ll_show_video;
	private AutoPagerView pagerView;
	private Dialog dialog;
	private Display display;
	private TextView add_txl, had_add_txl, pay;
	private TextView shopname, cartype, address, tv_phoneNum_one, tv_num, jianying, show_miaosu,tv_miaosu;
	private String id = "";
	private boolean isAdd = false;
	private BaseShopDetail baseInfo;
	private int num = 0;
	private List<ImageUrlInfo> lunbotu;
	private List<BrandsInfo> brands;
	private ImageView pic1, pic2, pic3, pic4, pic5, pic6, pic7, pic8, pic9, pic10;
	private List<TelsNumInfo> tels;
	private List<TagsInfo> tags;
	private FlowLayout flowlayout;
	private TextView tv1, tv2, tv3, tv4;
	private String numPhone = "";
	private Button show_talk_num,btn_callback;

	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data;
	private Map<String, String> map = UtilMap.getInstance().init();

	private TextView fx;
	private String[] arr = new String[] { "分享到朋友圈", "分享给好友" };
	private String[]images ;
	
	private MarqueeTextView fangke;
	private TextView fw,total;
	private ImageView play;
	
	private TelephonyManager manager ;
	private int time = 0;
	
	private EditText callback;
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
		tv_miaosu = getView(R.id.tv_miaosu);
		tv_phoneNum_one = getView(R.id.tv_phoneNum_one);
		jianying = getView(R.id.jianying);
		flowlayout = getView(R.id.flowlayout);
		fx = getView(R.id.fx);
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
		fangke = getView(R.id.allname);
		fw = getView(R.id.fw);
		total = getView(R.id.total);
		ll_show_video = getView(R.id.ll_show_video);
		play = getView(R.id.play);
		btn_callback = getView(R.id.btn_callback);
		callback = getView(R.id.callback);
		
	}

	@Override
	public void initData() {
		//获取电话服务
        manager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        // 手动注册对PhoneStateListener中的listen_call_state状态进行监听
        manager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
		id = getIntent().getExtras().getString("shop_id");
		lunbotu = new ArrayList<>();
		brands = new ArrayList<>();
		tels = new ArrayList<>();
		tags = new ArrayList<>();
		data = new ArrayList<>();
		initGallery();
		btn_callback.setOnClickListener(this);
		pay.setOnClickListener(this);
		fx.setOnClickListener(this);
		back.setOnClickListener(this);
		ll_talk.setOnClickListener(this);
		add_txl.setOnClickListener(this);
		had_add_txl.setOnClickListener(this);
		ll_talk_list.setOnClickListener(this);
		ll_phone.setOnClickListener(this);
		if (id != null && id.length() > 0) {
			boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			if(isShow){
				getHasLogin();
			}else{
				getShopData();
			}
		}
	}
/**
 *  访问初始化数据前 登录的时候调用接口
 * */
	private void getHasLogin() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_VISIT_SHOP,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						dealHasLogin(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<>();
				map.put("shop_id", id);
				return map;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealHasLogin(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
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
		if(time >= 60){
			getCallShop("1");
			time = 0;
		}else if(time < 60 && time > 0){
			getCallShop("0");
			time = 0;
		}
	}

	private void getShopData() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_SHOP_DETAIL,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						try {
							JSONObject json = new JSONObject(result);
							System.out.println("商家详情="+json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dealData(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<>();
				map.put("shop_id", id);
				return map;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			baseInfo = gson.fromJson(result, BaseShopDetail.class);
			if (baseInfo != null) {
				lunbotu = baseInfo.getLunbotu();
				brands = baseInfo.getBrands();
				tels = baseInfo.getTels();
				tags = baseInfo.getTags();
				num = lunbotu.size();
				System.out.println("num = lunbotu.size()=="+lunbotu.size());
				ll_show_video.setVisibility(View.GONE);
				fangke.setText(baseInfo.getVisitors_str());//访客名字
				tv_phoneNum_one.setText(baseInfo.getService_tel());//显示400电话
				if(baseInfo.getService_tel().length() > 0){
					tv_phoneNum_one.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + baseInfo.getService_tel()));
							startActivity(intent);
						}
					});
				}
				fw.setText(baseInfo.getVisit_num());//访问数量
				total.setText(baseInfo.getSave_num());//收藏量
				if(baseInfo.getHas_video().length() == 1){
					if(baseInfo.getHas_video().equals("0")){
						ll_show_video.setVisibility(View.GONE);
					}else if(baseInfo.getHas_video().equals("1")){
						ll_show_video.setVisibility(View.VISIBLE);
						play.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								String videoUrl = baseInfo.getVideo_url();
								if (videoUrl != null && videoUrl.length() != 0) {
									Intent i = new Intent(ShopDetailActivity.this, VideoActivity.class);
									i.putExtra("info", Constant.URL_TEST + videoUrl);
									startActivity(i);
								} else {
									Utils.showText(ShopDetailActivity.this, "没有播放视频...");
								}
							}
						});
					}
				}
				if(num > 0 ){
					images = new String[num];
					for(int i=0;i<num;i++){
						images[i] = Constant.URL_TEST + lunbotu.get(i).getImage_url(); 
					}
				}
				tv_miaosu.setText(baseInfo.getDescription());
				initGallery();
				shopname.setText(baseInfo.getShop_name());

				int brandsNum = brands.size();
				if (brandsNum > 0) {
					pic1.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(0).getBrand(), pic1);
				}
				if (brandsNum > 1) {
					pic2.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(1).getBrand(), pic2);
				}
				if (brandsNum > 2) {
					pic3.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(2).getBrand(), pic3);
				}
				if (brandsNum > 3) {
					pic4.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(3).getBrand(), pic4);
				}
				if (brandsNum > 4) {
					pic5.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(4).getBrand(), pic5);
				}
				if (brandsNum > 5) {
					ll_two.setVisibility(View.VISIBLE);
					pic6.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(5).getBrand(), pic6);
				}
				if (brandsNum > 6) {
					pic7.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(6).getBrand(), pic7);
				}
				if (brandsNum > 7) {
					pic8.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(7).getBrand(), pic8);
				}
				if (brandsNum > 8) {
					pic9.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(8).getBrand(), pic9);
				}
				if (brandsNum > 9) {
					pic10.setVisibility(View.VISIBLE);
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + brands.get(9).getBrand(), pic10);
				}
				show_miaosu.setText(baseInfo.getAm_series());
				address.setText(baseInfo.getAddress());
				if (tels.size() > 0) {
					int n = 0;
					for (int i = 0; i < tels.size(); i++) {
						n = n + Integer.parseInt(tels.get(i).getCall_count());
					}
					tv_num.setText(n + "");
				}
				if(baseInfo.getJianying() != null && baseInfo.getJianying().length() == 0){
					jianying.setVisibility(View.GONE);
				}
				jianying.setText(baseInfo.getJianying());

				int tagsNum = tags.size();
				if (tagsNum > 0) {
					flowlayout.setVisibility(View.VISIBLE);
					tv1.setVisibility(View.VISIBLE);
					tv1.setText(tags.get(0).getTag());
				}
				if (tagsNum > 1) {
					tv2.setVisibility(View.VISIBLE);
					tv2.setText(tags.get(1).getTag());
				}
				if (tagsNum > 2) {
					tv3.setVisibility(View.VISIBLE);
					tv3.setText(tags.get(2).getTag());
				}
				if (tagsNum > 3) {
					tv4.setVisibility(View.VISIBLE);
					tv4.setText(tags.get(3).getTag());
				}

				String strSaved = baseInfo.getSaved();
				if (strSaved != null && strSaved.equals("1")) {
					isAdd = true;
					had_add_txl.setVisibility(View.VISIBLE);
					add_txl.setVisibility(View.GONE);
				} else if (strSaved != null && strSaved.equals("0")) {
					isAdd = false;
					had_add_txl.setVisibility(View.GONE);
					add_txl.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void updateAlias(String name) {
		Intent intent = new Intent(ShopDetailActivity.this, ChatActivity.class);
		intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, baseInfo.getIm_username());
		intent.putExtra("user_nickname", baseInfo.getShop_name());
		startActivity(intent);
	}

	private void shared(int num) {
		if (!WXManager.instance().isWXAppInstalled()) {
			Utils.showToast(ShopDetailActivity.this, "您还未安装微信客户端...", 1000);
			return;
		}
		 Bitmap thumb1 = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
		 WXManager.instance().sendReq(WXManager.instance().getReq("http://www.peipeixia.com/"+"share?shop_id="+id,null, baseInfo.getShop_name(), thumb1, num));
	}

	@Override
	public void onClick(View v) {
		boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
		switch (v.getId()) {
		case R.id.btn_callback://拨打回拨电话
			String content = callback.getText().toString().trim();
			
			String url = "http://api.id98.cn/api/v2/callback?appkey=9e0bfd61e94319714420d5de3b01de0a&phone="+
					content+"&call=15142096690&phoneShow=1&callShow=1";
			
//			if(content.length() > 0){
//				if(baseInfo != null && baseInfo.getService_tel() !=null){
//					if(baseInfo.getService_tel().length() == 0){
//						Utils.showText(ShopDetailActivity.this,"该商家没有设置拨打电话...");
//					}else{
//						callBack(content,baseInfo.getService_tel());
//					}
//				}
//			}else{
//				Utils.showText(ShopDetailActivity.this,"请输入您的11位手机号...");
//			}
			
			callBack(url);
			break;
		case R.id.fx:
			if (isShow) {
				AlertDialog.Builder builder1 = new Builder(ShopDetailActivity.this);
				builder1.setItems(arr, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						// 分享朋友
						if (position == 0) {
							shared(1);
							dialog.dismiss();
						} // 分享给好友
						else if (position == 1) {
							shared(0);
							dialog.dismiss();
						}
					}
				});
				builder1.create().show();
			} else {
				Utils.showText(ShopDetailActivity.this, "请您登录后进行聊天...");
			}
			break;
		case R.id.ll_phone:
			showPhoneNum();
			break;
		case R.id.ll_talk_list:// 聊天列表
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
				Utils.showText(ShopDetailActivity.this, "请您登录后进行聊天...");
			}
			break;
		case R.id.ll_talk:
			if (isShow && baseInfo != null) {
				String name = baseInfo.getIm_username();
				if (name != null && name.length() != 0) {
					updateAlias(name);
				}
			} else {
				Utils.showText(ShopDetailActivity.this, "请您登录后进行聊天...");
			}
			break;
		case R.id.back:
			finish();
			break;
		case R.id.pay:
			if (isShow) {
				if (id != null && id.length() > 0) {
					gotoPay(id);
				}
			} else {
				Utils.showText(ShopDetailActivity.this, "请您登录后进行操作...");
			}
			break;
		case R.id.add_txl:
			if (isShow) {
				if (!isAdd) {
					SaveShop();
				}
			} else {
				Utils.showText(ShopDetailActivity.this, "请您登录后进行操作...");
			}
			break;
		case R.id.had_add_txl:
			if (isShow) {
				if (isAdd) {
					DeleteShop();
				}
			} else {
				Utils.showText(ShopDetailActivity.this, "请您登录后进行操作...");
			}
			break;
		default:
			break;
		}
	}

	private void gotoPay(String shop_id) {
		Bundle data = new Bundle();
		data.putString("shop_id", shop_id);
		Utils.goOtherWithDataActivity(ShopDetailActivity.this, PayWXActivity.class, data);
	}

	Handler h = new Handler();
	Runnable runnable1 = new Runnable() {

		@Override
		public void run() {
				
			time++;
			System.out.println("time="+time);
			if (time >=  60) {
				h.removeCallbacks(runnable1);
				return;
			}
			h.postDelayed(this, 1000);
		}
	};
	@SuppressLint("RtlHardcoded")
	@SuppressWarnings("deprecation")
	private void showPhoneNum() {
		View view = LayoutInflater.from(ShopDetailActivity.this).inflate(R.layout.tel_number_txl, null);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
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
				time = 0;
				h.postDelayed(runnable1, 1000);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		
		tv2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv2.getText().toString();
//				getCallShop();
				time = 0;
				h.postDelayed(runnable1, 1000);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv3.getText().toString();
//				getCallShop();
				time = 0;
				h.postDelayed(runnable1, 1000);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv4.getText().toString();
				time = 0;
				h.postDelayed(runnable1, 1000);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numPhone));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		tv5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				numPhone = tv5.getText().toString();
//				getCallShop();
//				h.postDelayed(runnable1, 1000);
				time = 0;
				h.postDelayed(runnable1, 1000);
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
		dialog = new Dialog(ShopDetailActivity.this, R.style.ActionSheetDialogStyle);
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	private void getCallShop(final String str) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
//		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.CALL_SHOP,
				StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_CALL_SHOP_POINT,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						try {
							JSONObject json =new JSONObject(result);
							System.out.println("aaaaaa"+json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dealTelResult(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
					}
				}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("shop_id", baseInfo.getId());
				params.put("tel", numPhone);
				System.out.println("传递参数str=="+str);
				params.put("add_point", str);
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

	private void DeleteShop() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.DELETE_SHOP_SAVED,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						dealDeleteShop(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
					}
				}) {

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
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.SAVE_SHOP,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						dealSaveShop(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
					}
				}) {

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
		} else if (status == 300) {
			logout();
		}
	}

	private void logout() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.LOGOUT,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						dealLogoutShop(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
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
			Intent i = new Intent(ShopDetailActivity.this, LoginActivity.class);
			startActivity(i);
			Utils.showText(ShopDetailActivity.this, "由于您长时间未操作....请您重新登录账号...");
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
		} else if (status == 300) {
			logout();
		}
	}

	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		if (num <= 0) {
			final int[] imgs = new int[] { R.drawable.failure_lunbotu, R.drawable.failure_lunbotu };
			for (int i = 0; i < imgs.length; i++) {
				ImageView imageview1 = new ImageView(ShopDetailActivity.this);
				imageview1.setScaleType(ScaleType.FIT_XY);
				imageview1.setImageResource(imgs[i]);
				pagePic.add(imageview1);
			}
		} else if(num > 0){
			for (int i = 0; i < num; i++) {
				ImageView imageview = new ImageView(ShopDetailActivity.this);
				imageview.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(images[i], imageview);
				imageview.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						showImage(AutoPagerView.num,images);
					}
				});
				pagePic.add(imageview);
			}
			
		}
		pagerView.setPageViewPics(pagePic, 0);
	}

	private void showImage(int position, String[] urls) {
		Intent intent = new Intent(ShopDetailActivity.this, ShowImageActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ShowImageActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(runnable1 != null){
			h.removeCallbacks(runnable1);
		}
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

	@Override
	protected void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
//		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	private void getNickNameResut(final String allname) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				Constant.URL_TEST + Urls.api_get_ALL_nickname, new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						dealReturnReuslt(result);
						dismissLoadingDialog();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(ShopDetailActivity.this, "网络访问失败");
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
				Intent i = new Intent(ShopDetailActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
	
	
	class MyPhoneStateListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                System.out.println("手机空闲起来了");
                if(runnable1 != null){
                	h.removeCallbacks(runnable1);
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                System.out.println("手机铃声响了，来电号码"+incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                System.out.println("电话被挂起了");
            default:
                break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
        
    }
	
	
	/**
	 * 超过60s打电话
	 * */
	private void exceedSixtySecond() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_GET_POINT_CALL, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("超过60s打电话"+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ShopDetailActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	/**
	 * 回拨打电话
	 * */
	private void callBack(String url) {
		
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("回拨电话"+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealCallBackData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(ShopDetailActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealCallBackData(String result){
		try {
			JSONObject json =new JSONObject(result);
			int errcode = json.getInt("errcode");
			if(errcode == 0){
				Utils.showToast(ShopDetailActivity.this, "呼叫成功", 1000);
			}else if(errcode == 1){
				Utils.showToast(ShopDetailActivity.this, "appkey不存在", 1000);
			}else if(errcode == 2){
				Utils.showToast(ShopDetailActivity.this, "主叫号码格式错误", 1000);
			}else if(errcode == 3){
				Utils.showToast(ShopDetailActivity.this, "被叫号码格式错误", 1000);
			}else if(errcode == 6){
				Utils.showToast(ShopDetailActivity.this, "账户余额不足", 1000);
			}else if(errcode == 7){
				Utils.showToast(ShopDetailActivity.this, "IP被拒绝", 1000);
			}else if(errcode == 8){
				Utils.showToast(ShopDetailActivity.this, "主被叫号码相同", 1000);
			}else if(errcode == 11){
				Utils.showToast(ShopDetailActivity.this, "运营商线路故障，请重试或联系我们", 1000);
			}else if(errcode == 12){
				Utils.showToast(ShopDetailActivity.this, "短时间存在相同的呼叫，请勿重复发起", 1000);
			}else if(errcode == -1){
				Utils.showToast(ShopDetailActivity.this, "短时间内发起大量无效呼叫，帐号被临时冻结，请稍候再试", 1000);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
