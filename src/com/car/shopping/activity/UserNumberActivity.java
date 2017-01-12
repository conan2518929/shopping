package com.car.shopping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.adapter.UserNumberAdapter;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseUerNumInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.entity.UserNumInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.car.shopping.widget.PullToRefreshBase;
import com.car.shopping.widget.PullToRefreshListView;
import com.car.shopping.widget.PullToRefreshBase.OnRefreshListener;
import com.google.gson.Gson;

public class UserNumberActivity extends BaseActivity{

	private TextView title;
	private LinearLayout back;
	private String mPage_num = "1";
	private int page_num = 1;
	private UserNumberAdapter adapter;
	private PullToRefreshListView mPullListView;
	private List<UserNumInfo> list;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_number);
		initViews();
		initData();
	} 
	
	@Override
	public void initViews() {
		// TODO Auto-generated method stub
		back = getView(R.id.back);
		title = getView(R.id.title);
		mPullListView = getView(R.id.listview);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		list = new ArrayList<>();
		mPullListView.setPullLoadEnabled(true);
		mPullListView.setScrollLoadEnabled(false);

		listView = mPullListView.getRefreshableView();
		adapter = new UserNumberAdapter(UserNumberActivity.this, list);
		listView.setAdapter(adapter);
		listView.setVerticalScrollBarEnabled(false);
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				mPage_num = "1";
				page_num = 1;
				deleteCcomment(Urls.API_GET_USER_LIST);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				deleteCcomment(Urls.API_GET_USER_LIST);
			}
		});
		mPullListView.setLastUpdatedLabel(AppContext.mSharedPref.getSharePrefString(SharedPrefConstant.LASTUPDATETIME_HOME));
		mPullListView.doPullRefreshing(false, 100);
	}

	/**
	 * 删除某条评论
	 * */
	private void deleteCcomment(String url) {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String result) {
						try {
							JSONObject json = new JSONObject(result);
							System.out.println("====用户量===" + json.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dealDeleteCcomment(result);
						dismissLoadingDialog();
						mPullListView.onPullUpRefreshComplete();
						mPullListView.onPullDownRefreshComplete();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						dismissLoadingDialog();
						Utils.showText(UserNumberActivity.this, "网络访问失败");
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("page_num", mPage_num);
				params.put("page_limit", "20");
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealDeleteCcomment(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		if (statusInfo != null) {
			int status = statusInfo.getStatus();
			if (status == 200) {
				BaseUerNumInfo baseData = gson.fromJson(result, BaseUerNumInfo.class);
				page_num =  ++page_num;
				mPage_num = page_num +"";
				if(baseData != null &&(baseData.getData().getData().size() != 0)){
					title.setText("用户量"+baseData.getData().getTotal_num());
					list.addAll(baseData.getData().getData());
					adapter.refresh(list);
				}
			}
		}
	}
	
}
