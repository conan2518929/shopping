package com.car.shopping.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.AppManager;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseChouJiangInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddressActivity extends BaseActivity {

	private LinearLayout back;
	private Button btn;
	private EditText et_address, et_name, et_tel;
	private String mAddress = "", mName = "", mTel = "", record_id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		et_address = getView(R.id.et_address);
		et_name = getView(R.id.et_name);
		et_tel = getView(R.id.et_tel);
		btn = getView(R.id.btn);
	}

	@Override
	public void initData() {
		record_id = getIntent().getExtras().getString("id");
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAddress = et_address.getText().toString().trim();
				mName = et_name.getText().toString().trim();
				mTel = et_tel.getText().toString().trim();
				if (mAddress.length() != 0 && mName.length() != 0 && mTel.length() != 0) {
					writeAddress();
				} else {
					Utils.showText(AddressActivity.this, "请您完整填写收货人信息后兑换....");
				}
			}
		});
	}
	/**
	 * 初始化数据
	 * */
	private void writeAddress() {
		showLoadingDialog();
		AppContext.getInstance().cancelPendingRequests(TAG);
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_GET_ITEM_RAND_POST, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("填写收获地址="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealData(result);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(AddressActivity.this, "网络访问失败");
			}
		}) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("record_id", record_id);
				params.put("name", mName);
				params.put("mobile", mTel);
				params.put("address", mAddress);
				return params;
			}
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealData(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			Utils.showToast(AddressActivity.this, "我们会尽快给您发货，请您耐心等待....", 2000);
			finish();
		} 
		dismissLoadingDialog();
	}
}
