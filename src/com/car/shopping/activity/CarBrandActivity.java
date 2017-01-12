package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.CarBrandLvTwoAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseBrandInfo;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.BrandInfo;
import com.car.shopping.entity.CarModelInfo;
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
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint({ "InflateParams", "ViewHolder" })
public class CarBrandActivity extends BaseActivity implements OnItemClickListener{

	private LinearLayout back;
	private ListView lv, lv_one,lv_two;
	private ImageView btn_search;
	private EditText search_et;
	private CarBrandLvTwoAdapter adapter_two;
	
	private String[] letter = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	private List<String> letterToCity = new ArrayList<String>();
	
	private String[] letter1;
	
	private BrandInfo infoChoose;
	
	private MyAdapter myAdapter;
	private MyAdapter1 myAdapter1;
	
	private BaseBrandInfo baseInfo;
	private List<BrandInfo> data;
	private List<CarModelInfo> series_list;
	private Map<String,BrandInfo>map = new HashMap<String,BrandInfo>();
	private Button show_talk_num;
	private Handler h = new Handler();
	private LinearLayout ll_talk;
	private String carName = "";
	
	private BaseTalkNickNameInfo baseTalkNickName;
	private List<TalkNickNameInfo> data1;
	private Map<String, String> mapNickName = UtilMap.getInstance().init();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carbrand);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		show_talk_num = getView(R.id.show_talk_num);
		lv = getView(R.id.lv);
		lv_one = getView(R.id.lv_one);
		lv_two = getView(R.id.lv_two);
		search_et = getView(R.id.search_et);
		btn_search = getView(R.id.btn_search);
		ll_talk = getView(R.id.ll_talk_list);
	}

	@Override
	public void initData() {
		data1 = new  ArrayList<>();
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = search_et.getText().toString().trim();
				if(str.length() != 0){
					getResultTitle(str);
				}else{
					Utils.showToast(CarBrandActivity.this, "请您输入您要搜索的商品...", 1000);
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
					Utils.showText(CarBrandActivity.this,"请您登录后进行聊天...");
				}
			}
		});
		data = new ArrayList<>();
		series_list = new ArrayList<>();
		
		myAdapter = new MyAdapter();
		lv_one.setAdapter(myAdapter);
		
		adapter_two = new CarBrandLvTwoAdapter(CarBrandActivity.this, series_list, R.layout.item_carbrand_two);	
		lv_two.setAdapter(adapter_two);
		myAdapter1 = new MyAdapter1(); 
		lv.setAdapter(myAdapter1);
		
		lv_one.setOnItemClickListener(this);
		lv_two.setOnItemClickListener(this);
		lv.setOnItemClickListener(this);
		getData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EMClient.getInstance().chatManager().addMessageListener(messageListener);
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
		show_talk_num.setText("" + unreadNum);
	}
	
	class MyAdapter1 extends BaseAdapter {

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
			View view = LayoutInflater.from(CarBrandActivity.this).inflate(R.layout.item_letter, null);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl);
			tv.setText(letter[position]);
			if(position == letter.length-1){
				rl.setBackgroundResource(R.drawable.zimu_bottom);
			}else if(position == 0){
				rl.setBackgroundResource(R.drawable.zimu_top);
			}
			return view;
		}
	}
	
	class MyAdapter extends BaseAdapter {

		final static int TYPE_1 = 1;
		final static int TYPE_2 = 2;
		protected int mposition = 1;
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return letterToCity.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return letterToCity.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		
		@Override
		public int getItemViewType(int position) {
			for (int i = 0; i < letter.length; i++) {
				if (letterToCity.get(position).equals(letter[i])) {
					return TYPE_1;
				}
			}
			return TYPE_2;
		}

		@Override
		public int getViewTypeCount() {
			return 3;
		}

		public void setPosition(int position){
			this.mposition = position;
		}
		
		public void changeView(){
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder1 vh1 = null;
			ViewHolder2 vh2 = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case TYPE_1:
					convertView = LayoutInflater.from(CarBrandActivity.this).inflate(R.layout.letter, null);
					vh1 = new ViewHolder1();
					vh1.tv = (TextView) convertView.findViewById(R.id.letterTextView);
					convertView.setTag(vh1);
					break;
				case TYPE_2:
					convertView = LayoutInflater.from(CarBrandActivity.this).inflate(R.layout.item_carbrand_one, null);
					vh2 = new ViewHolder2();
					vh2.tv = (TextView) convertView.findViewById(R.id.tv);
					vh2.img = (ImageView) convertView.findViewById(R.id.img);
					vh2.rl = (RelativeLayout) convertView.findViewById(R.id.rl);
					convertView.setTag(vh2);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_1:
					vh1 = (ViewHolder1) convertView.getTag();
					break;
				case TYPE_2:
					vh2 = (ViewHolder2) convertView.getTag();
					break;
				default:
					break;
				}
			}
			
			switch (type) {
			case TYPE_1:
				vh1.tv.setText(letterToCity.get(position));
				break;
			case TYPE_2:
				vh2.tv.setText(letterToCity.get(position));
				BrandInfo info = map.get(letterToCity.get(position));
				if(info.getBrand_logo().length() != 0){
					ImageLoader.getInstance().displayImage(Constant.URL_TEST + info.getBrand_logo(),vh2.img);
				}
				vh2.rl.setBackgroundColor(getResources().getColor(R.color.white));
				if(position == mposition){
					vh2.rl.setBackgroundColor(getResources().getColor(R.color.gray_five));
				}
				break;
			default:
				break;
			}
			return convertView;
		}
	}
	
	class ViewHolder1 {
		TextView tv;
	}

	class ViewHolder2 {
		TextView tv;
		ImageView img;
		RelativeLayout rl;
	}

	@SuppressLint("ShowToast")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.lv_one:
			boolean isLetter = false;
			for (int i = 0; i < letter.length; i++) {
				if (letter[i].equals(letterToCity.get(position))) {
					isLetter = true;
					break;
				}
			}
			if (!isLetter) {
				carName = letterToCity.get(position);
				infoChoose = map.get(letterToCity.get(position));
				adapter_two.refresh(infoChoose.getSeries_list());
				myAdapter.setPosition(position);
				myAdapter.changeView();
			}
			break;
		case R.id.lv_two:
			if((infoChoose.getSeries_list().get(position).getSeries_name()).equals("全部")){
				String strAll = "";
				int num = infoChoose.getSeries_list().size();
				if(num >1){
					for(int i=1;i<num;i++){
						strAll = strAll + infoChoose.getSeries_list().get(i).getSeries_name() + " ";
					}
					System.out.println("====11111strAll===="+strAll);
//					getResult(carName);
					getResult(strAll);
				}else{
					Utils.showText(CarBrandActivity.this, "没有数据...");
				}
			}else{
				getResult(infoChoose.getSeries_list().get(position).getSeries_name());
				System.out.println("222=="+infoChoose.getSeries_list().get(position).getSeries_name());
			}
			
			break;
		case R.id.lv:
			String str = letter[position];
			for(int n=0;n<letterToCity.size();n++){
				if(str.equals(letterToCity.get(n))){
					myAdapter.setPosition(n+1);
					myAdapter.changeView();
					lv_one.setSelection(n);
					infoChoose = map.get(letterToCity.get(n+1));
					adapter_two.refresh(infoChoose.getSeries_list());
				}
			}
			break;

		default:
			break;
		}
	}
	
	private void getResult(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		data.putInt("type", 1);
		Utils.goOtherWithDataActivity(CarBrandActivity.this, ResultShopLisOnetActivity.class, data);
	}
	private void getResultTitle(String name){
		Bundle data = new Bundle();
		data.putString("name", name);
		data.putInt("type", 1);
		Utils.goOtherWithDataActivity(CarBrandActivity.this, ResultShopListActivity.class, data);
	}
	private void getData() {
		showLoadingDialog();
		VolleyRequest.getInstance().RequestGet(CarBrandActivity.this, 
				Constant.URL_TEST+Urls.SEARCH_AM_BRAND, TAG, 
				new VolleyInterface(this, VolleyInterface.mListener, VolleyInterface.mErrorListtener) {

			@Override
			public void onMySuccess(String result) {
				dealData(result);
				dismissLoadingDialog();
			}

			@Override
			public void onMyError(VolleyError error) {
				dismissLoadingDialog();
				Utils.showText(CarBrandActivity.this, "网络访问失败");
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
			baseInfo = gson.fromJson(result, BaseBrandInfo.class);
			if(baseInfo != null){
				data = baseInfo.getData();
				if(data.size() > 0){
					series_list = data.get(0).getSeries_list();
					initLetter();
					myAdapter.notifyDataSetChanged();
					carName = letterToCity.get(1);
					infoChoose = map.get(carName);
					adapter_two.refresh(series_list);
				}
			}
		}
	}
	
	private void initLetter(){
		map.clear();
		for(int m=0;m<data.size();m++){
			map.put(data.get(m).getBrand_name(), data.get(m));
		}
		
		letter1 = new String[data.size()];
		for(int i=0;i<data.size();i++){
			letter1[i] = data.get(i).getZimu();
		}
		String str = "";
		for (int i = 0; i < letter.length; i++) {
			str = letter[i];
			boolean isAddLetter = false;
			for (int j = 0; j < letter1.length; j++) {
				if (str.equals(letter1[j])) {
					if (!isAddLetter) {
						letterToCity.add(str);
						isAddLetter = true;
					}
					letterToCity.add(data.get(j).getBrand_name());
				}
			}
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
				Utils.showText(CarBrandActivity.this, "网络访问失败");
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
						mapNickName.put(data1.get(i).getIm_username(), data1.get(i).getNickname());
					}	
				}
				Intent i = new Intent(CarBrandActivity.this, LianXiRenListActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		}
	}
}
