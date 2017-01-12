package com.car.shopping.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.activity.AlertDialogActivity;
import com.car.shopping.activity.CarBrandActivity;
import com.car.shopping.activity.CarTypeActivity;
import com.car.shopping.activity.FindByTypeActivity;
import com.car.shopping.activity.MainActivity;
import com.car.shopping.activity.SearchTotalActivity;
import com.car.shopping.activity.ShopDetailActivity;
import com.car.shopping.adapter.HomeShopAdapter;
import com.car.shopping.adapter.HomeShopAdapter.MyClickListener;
import com.car.shopping.adapter.HomeTitleAddressAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseInterfaces;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseHomeDataInfo;
import com.car.shopping.entity.BaseRecommendShopInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.CityListInfo;
import com.car.shopping.entity.LunBoTuInfo;
import com.car.shopping.entity.NewVersionInfo;
import com.car.shopping.entity.RecommendShopsInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.SystemInfo;
import com.car.shopping.entity.TalkNickNameInfo;
import com.car.shopping.entity.TelsInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.ChatActivity;
import com.car.shopping.talk.ui.LianXiRenListActivity;
import com.car.shopping.utils.UtilMap;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.AddPopWindow;
import com.car.shopping.widget.AutoPagerView;
import com.car.shopping.widget.MarqueeTextView;
import com.car.shopping.widget.PullToRefreshBase;
import com.car.shopping.widget.PullToRefreshBase.OnRefreshListener;
import com.car.shopping.widget.PullToRefreshListView;
import com.google.gson.Gson;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

@SuppressLint("InflateParams")
public class HomeFragment extends BaseFragment implements BaseInterfaces, OnClickListener {

	private final String TAG = "HomeFragment";
	private View layoutView, conentView;
	private PullToRefreshListView mPullListView;
	private ListView listView;
	private AutoPagerView pagerView;
	private HomeShopAdapter shopAdapter;
	private HomeTitleAddressAdapter titleAddressAdapter;
	private LayoutInflater inflater;
	private AddPopWindow addPopWindow;
	private ListView lv;
	private LinearLayout ll_title_left, title_search;
	private Dialog dialog;
	private Display display;
	private TextView address;
	private MarqueeTextView guanggao;
	
	private BaseRecommendShopInfo baseShopInfo;
	private BaseHomeDataInfo baseInfo;
	private NewVersionInfo version;
	private List<RecommendShopsInfo> recommend_shops;
	private List<LunBoTuInfo> lunbotu;
	private SystemInfo system_info;
	private List<CityListInfo> city_list;
	private boolean isOne = false;
	private int num = 0;
	private int clickNum = 0;
	private String numPhone = "";
	private List<TelsInfo> tels;
	private String announcement;//公告
	private String point_rule;//积分规则
	// 未读消息 Button
	private Button show_talk_num;
	private LinearLayout ll_talk;
	private Handler h = new Handler();
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo>data;
	private Map<String, String> map = UtilMap.getInstance().init();
	
	private int time = 0;
	private String mNum = "";
	private int mPosioton = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		layoutView = inflater.inflate(R.layout.fragment_home, container, false);
		initViews();
		initData();
		return layoutView;
	}

	@Override
	public void initViews() {
		show_talk_num = (Button) layoutView.findViewById(R.id.show_talk_num);
		mPullListView = (PullToRefreshListView) layoutView.findViewById(R.id.listview);
		ll_title_left = (LinearLayout) layoutView.findViewById(R.id.ll_title_left);
		ll_talk = (LinearLayout) layoutView.findViewById(R.id.ll_talk);
		title_search = (LinearLayout) layoutView.findViewById(R.id.title_search);
		address = (TextView) layoutView.findViewById(R.id.address);
	}

	@Override
	public void initData() {
		ll_title_left.setOnClickListener(this);
		ll_talk.setOnClickListener(this);
		title_search.setOnClickListener(this);
		city_list = new ArrayList<>();
		recommend_shops = new ArrayList<>();
		data = new ArrayList<>();
		lunbotu = new ArrayList<>();
		tels = new ArrayList<>();

		mPullListView.setPullLoadEnabled(true);
		mPullListView.setScrollLoadEnabled(false);
		View headerView = getActivity().getLayoutInflater().inflate(R.layout.home_add_listview, null);
		pagerView = (AutoPagerView) headerView.findViewById(R.id.auto_pagerview);
		guanggao  = (MarqueeTextView) headerView.findViewById(R.id.guanggao);
		ImageView img_cpp = (ImageView) headerView.findViewById(R.id.img_cpp);
		ImageView img_cfl = (ImageView) headerView.findViewById(R.id.img_cfl);
		ImageView img_dxcp = (ImageView) headerView.findViewById(R.id.img_dxcp);
		ImageView img_qcyp = (ImageView) headerView.findViewById(R.id.img_qcyp);
		img_cpp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.gotoOtherActivity(getActivity(), CarBrandActivity.class);
			}
		});
		img_cfl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDate(0);
			}
		});
		img_dxcp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoByType(7);
			}
		});
		img_qcyp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoByType(8);
			}
		});
		listView = mPullListView.getRefreshableView();
		shopAdapter = new HomeShopAdapter(getActivity(), recommend_shops, mListener);
		listView.addHeaderView(headerView);
		listView.setAdapter(shopAdapter);
		listView.setVerticalScrollBarEnabled(false);
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getData();
//				 mPullListView.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPullListView.onPullUpRefreshComplete();
			}
		});
		mPullListView.setLastUpdatedLabel(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME));
		mPullListView.doPullRefreshing(false, 100);
//		
//		if(){
//			
//		}else{
//			getData();
//		}
		
	}

	@SuppressWarnings("unused")
	private String simpleMapToJsonStr(Map<?, ?> map) {
		if (map == null || map.isEmpty()) {
			return "null";
		}
		String jsonStr = "[";
		Set<?> keySet = map.keySet();
		for (Object key : keySet) {
			jsonStr += "\"" + map.get(key) + "\",";
		}
		jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
		jsonStr += "]";
		return jsonStr;
	}

	@SuppressWarnings("deprecation")
	private void getData() {
		AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME, new Date().toLocaleString());
		mPullListView.setLastUpdatedLabel(new Date().toLocaleString());
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_INITIAL_DATA, new Response.Listener<String>() {
			
			@Override
			public void onResponse(String result) {
//				try {
//					JSONObject json = new JSONObject(result);
//					System.out.println("页面初始化="+json.toString());
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
				dealData(result);
				mPullListView.onPullDownRefreshComplete();
				((MainActivity) getActivity()).dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				((MainActivity) getActivity()).dismissLoadingDialog();
				Utils.showText(getActivity(), "网络访问失败");
				mPullListView.onPullDownRefreshComplete();
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<>();
				String cityId = AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.CHOOSE_CITY, "210100");
				int vCode = Utils.getVersionCode(getActivity());
				map.put("city_id", cityId);
				map.put("internal_code", vCode + "");
				map.put("platform", "android");
				return map;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void getCityData() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.GET_INITIAL_DATA, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
//				try {
//					JSONObject json = new JSONObject(result);
//					System.out.println("json="+json.toString());
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
				dealShopData(result);
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
				map.put("city_id", city_list.get(clickNum).getId());
				map.put("platform", "android");
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
			baseInfo = gson.fromJson(result, BaseHomeDataInfo.class);
			if (baseInfo != null) {
				version = baseInfo.getVersion();
				recommend_shops = baseInfo.getRecommend_shops();
				lunbotu = baseInfo.getLunbotu();
				system_info = baseInfo.getSystem_info();
				city_list = baseInfo.getCity_list();
				announcement = system_info.getAnnouncement();
				point_rule = system_info.getPoint_rule();
				
				guanggao.setText(announcement);
				
				num = lunbotu.size();
				initGallery();
				shopAdapter.refresh(recommend_shops);
				AppContext.imp_SharedPref.putSharePrefString(SharedPrefConstant.KEFU_TEL, system_info.getService_tel());
				AppContext.imp_SharedPref.putSharePrefString(SharedPrefConstant.RULE, system_info.getTrade_rule());
				
				address.setText(AppContext.imp_SharedPref.getSharePrefString(SharedPrefConstant.CHOOSE_CITY_NAME, "沈阳"));

				if (!isOne) {
					isOne = true;
					int vCode = Utils.getVersionCode(getActivity());
					if (!version.getVer().equals("")) {
						int ver = Integer.parseInt(version.getVer());
						int isUpdate = Integer.parseInt(version.getCompulsive());
						String strUrl = Constant.URL_TEST + version.getUrl();
						if (ver > vCode) {
							Intent intent = new Intent(getActivity(), AlertDialogActivity.class);
							intent.putExtra(SharedPrefConstant.EXTRA_VER_URL, strUrl);
							intent.putExtra(SharedPrefConstant.EXTRA_VER_VERDICT, isUpdate);
							startActivity(intent);
						}
					}
				}
				
			}
		}
	}
//处理选择地点更新页面
	private void dealShopData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
//			baseShopInfo = gson.fromJson(result, BaseRecommendShopInfo.class);
			baseInfo = gson.fromJson(result, BaseHomeDataInfo.class);
			if (baseInfo != null) {
//			if (baseShopInfo != null) {
//				recommend_shops.clear();
//				recommend_shops = baseShopInfo.getData();
//				shopAdapter.refresh(recommend_shops);
//				address.setText(city_list.get(clickNum).getName());
//				AppContext.imp_SharedPref.putSharePrefString(SharedPrefConstant.CHOOSE_CITY, city_list.get(clickNum).getId());
//				AppContext.imp_SharedPref.putSharePrefString(SharedPrefConstant.CHOOSE_CITY_NAME, city_list.get(clickNum).getName());
				/**
				 * 选择城市后 更新轮播图代码
				 * */
				recommend_shops.clear();
				recommend_shops = baseInfo.getRecommend_shops();
				shopAdapter.refresh(recommend_shops);
				address.setText(city_list.get(clickNum).getName());
				lunbotu.clear();
				lunbotu = baseInfo.getLunbotu();
				num = lunbotu.size();
				initGallery();
				
				AppContext.imp_SharedPref.putSharePrefString(SharedPrefConstant.CHOOSE_CITY, city_list.get(clickNum).getId());
				AppContext.imp_SharedPref.putSharePrefString(SharedPrefConstant.CHOOSE_CITY_NAME, city_list.get(clickNum).getName());
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		EMClient.getInstance().chatManager().removeMessageListener(messageListener);
		AppContext.getInstance().cancelPendingRequests(TAG);
	}

	private void updateAlias(String name,int position) {
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			intent.putExtra(com.car.shopping.talk.Constant.EXTRA_USER_ID, recommend_shops.get(position).getIm_username());
			intent.putExtra("user_nickname", recommend_shops.get(position).getShop_name());
			startActivity(intent);
	}

	private void initGallery() {
		List<View> pagePic = new ArrayList<View>();
		if (num == 0) {
			final int[] imgs = new int[] { R.drawable.failure_lunbotu, R.drawable.failure_lunbotu };
			for (int i = 0; i < imgs.length; i++) {
				ImageView imageview1 = new ImageView(getActivity());
				imageview1.setScaleType(ScaleType.FIT_XY);
				imageview1.setImageResource(imgs[i]);
				pagePic.add(imageview1);
			}
		} else if(num > 0) {
			for (int i = 0; i < num; i++) {
				ImageView imageview = new ImageView(getActivity());
				imageview.setScaleType(ScaleType.CENTER_CROP);
				ImageLoader.getInstance().displayImage(Constant.URL_TEST + lunbotu.get(i).getImage(), imageview);
				
				imageview.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String shopId = "";
						shopId = lunbotu.get(AutoPagerView.num).getShop_id();
						Bundle data = new Bundle();
						data.putString("shop_id", shopId);
						Utils.goOtherWithDataActivity(getActivity(),ShopDetailActivity.class,data);	
					}
				});
				pagePic.add(imageview);
			}
		}
		pagerView.setPageViewPics(pagePic, 1);
	}

	/**
	 * 左上角弹出窗口功能
	 * */
	@SuppressLint("ShowToast")
	public void showPopupWindow() {

		inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.popupwindow_fx, null);
		addPopWindow = new AddPopWindow(getActivity(), conentView);
		lv = (ListView) conentView.findViewById(R.id.lv);
		titleAddressAdapter = new HomeTitleAddressAdapter(getActivity(), city_list, R.layout.home_title_lv_item);
		lv.setAdapter(titleAddressAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (city_list != null && city_list.size() > 0) {
					clickNum = position;
					getCityData();
				}
				addPopWindow.dismiss();
			}
		});
		addPopWindow.showPopupWindow(ll_title_left);
	}

	@Override
	public void onClick(View v) {
		boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
		switch (v.getId()) {
		case R.id.ll_title_left:
			showPopupWindow();
			break;
		case R.id.ll_talk:
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
			break;
		case R.id.title_search:
			Utils.gotoOtherActivity(getActivity(), SearchTotalActivity.class);
			break;

		default:
			break;
		}
	}
	
	/**
     * 获取会话列表
     * 
     * @param context
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
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
				try {
					JSONObject json = new JSONObject(result);
					System.out.println("昵称=="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
				System.out.println("data=="+data.size());
				if(data.size() > 0){
					for(int i=0;i<data.size();i++){
						System.out.println("data.get(i).getNickname()"+data.get(i).getNickname());
						map.put(data.get(i).getIm_username(), data.get(i).getNickname());
					}	
				}
				Intent i = new Intent(getActivity(), LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
    
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {

			switch (v.getId()) {
			case R.id.tv_tel:// 打电话
				showDialog(position, v);
				break;
			case R.id.tv_talk:// 聊天
				boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
				if (isShow) {
					if(recommend_shops != null){
						String name = recommend_shops.get(position).getIm_username();
						if (name != null && name.length() != 0) {
							updateAlias(name,position);
						}
					}
				} else {
					Utils.showText(getActivity(), "请您登录后进行聊天...");
				}
				break;

			default:
				break;
			}
		}
	};

	@SuppressLint("RtlHardcoded")
	@SuppressWarnings("deprecation")
	private void showDialog(final int position, View v) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.tel_number, null);
		WindowManager windowManager = (WindowManager) (getActivity().getSystemService(Context.WINDOW_SERVICE));
		display = windowManager.getDefaultDisplay();
		view.setMinimumWidth(display.getWidth());
		LinearLayout ll_bottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
		final TextView tv1 = (TextView) view.findViewById(R.id.tv_one);
		final TextView tv2 = (TextView) view.findViewById(R.id.tv_two);
		final TextView tv3 = (TextView) view.findViewById(R.id.tv_three);
		final TextView tv4 = (TextView) view.findViewById(R.id.tv_four);
		final TextView tv5 = (TextView) view.findViewById(R.id.tv_five);
		TextView tv_qx = (TextView) view.findViewById(R.id.tv_qx);
		tels = recommend_shops.get(position).getTels();
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
//				getCallShop(position, numPhone);
				mNum = numPhone ;
				mPosioton = position;
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
//				getCallShop(position, numPhone);
				mNum = numPhone ;
				mPosioton = position;
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
//				getCallShop(position, numPhone);
				mNum = numPhone ;
				mPosioton = position;
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
//				getCallShop(position, numPhone);
				mNum = numPhone ;
				mPosioton = position;
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
				mNum = numPhone ;
				mPosioton = position;
				time = 0;
				h.postDelayed(runnable1, 1000);
//				getCallShop(position, numPhone);
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
		ll_bottom.setOnClickListener(new OnClickListener() {
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

	private void getDate(int position) {
		Bundle data = new Bundle();
		data.putInt("data", position);
		Utils.goOtherWithDataActivity(getActivity(), CarTypeActivity.class, data);
	}

	private void gotoByType(int position) {
		Bundle data = new Bundle();
		data.putInt("type", position);
		Utils.goOtherWithDataActivity(getActivity(), FindByTypeActivity.class, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = AppContext.mSharedPref.getSharePrefInteger(SharedPrefConstant.TALK_NUMBER, 0);
		show_talk_num.setText("" + unreadNum);
		
		if(time >= 60){
			getCallShop(mPosioton,mNum,"1");
			time = 0;
			mPosioton = 0;
			mNum = "";
		}else if(time < 60 && time > 0){
			getCallShop(mPosioton,mNum,"0");
			time = 0;
			mPosioton = 0;
			mNum = "";
		}
	}

	private void getCallShop(final int position, final String num,final String str) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		((MainActivity) getActivity()).showLoadingDialog();
//		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.CALL_SHOP, 
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_CALL_SHOP_POINT,
				new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
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
				params.put("shop_id", recommend_shops.get(position).getId());
				params.put("tel", num);
				System.out.println("传递参数str=="+str);
				params.put("add_point", str);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void refreshUIWithMessage() {
		getActivity().runOnUiThread(new Runnable() {
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
	
	Runnable runnable1 = new Runnable() {

		@Override
		public void run() {
				
			time++;
			if (time >=  60) {
				h.removeCallbacks(runnable1);
				return;
			}
			h.postDelayed(this, 1000);
		}
	};
	
	
}
