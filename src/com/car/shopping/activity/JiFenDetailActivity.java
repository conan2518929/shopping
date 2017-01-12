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
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Constant;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.JiFenInfo;
import com.car.shopping.entity.ListInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JiFenDetailActivity extends BaseActivity {

	private ListInfo info = null;
	private LinearLayout back;
	private Button btn;
	private ImageView img;
	private TextView name,number;
	private EditText et_address,et_name,et_tel;
	private String mAddress = "",mName = "",mTel = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jifendetail);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		btn = getView(R.id.btn);
		name = getView(R.id.name);
		number = getView(R.id.number);
		img = getView(R.id.img);
		et_address = getView(R.id.et_address);
		et_name = getView(R.id.et_name);
		et_tel = getView(R.id.et_tel);
	}
	
	@Override
	public void initData() {

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AppContext.getInstance().cancelPendingRequests(TAG);
				finish();
			}
		});
		
		btn.setOnClickListener(new OnClickListener() {//确认兑换
			
			@Override
			public void onClick(View arg0) {
				mAddress = et_address.getText().toString().trim();
				mName = et_name.getText().toString().trim();
				mTel = et_tel.getText().toString().trim();
				if(mAddress.length() != 0 && mName.length() != 0 && mTel.length() != 0){
					tijiao();
				}else{
					Utils.showText(JiFenDetailActivity.this, "请您完整填写收货人信息后兑换....");
				}
			}
		});

		info = (ListInfo) getIntent().getExtras().getSerializable("data");
		if (info != null) {
			name.setText(info.getItem_name());
			number.setText(info.getPoint()+"积分");
			ImageLoader.getInstance().displayImage(Constant.URL_TEST +info.getItem_pic(),img);
		}
	}

	private void tijiao() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TEST + Urls.API_GET_ITEM_EX, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				try {
					JSONObject json =new JSONObject(result);
					System.out.println("确认兑换结果==="+json.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dealQD(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(JiFenDetailActivity.this, "网络访问失败");
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("item_id", info.getItem_id());
				params.put("name", mName);
				params.put("mobile", mTel);
				params.put("address", mAddress);
				return params;
			}
		};;
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}
	
	private void dealQD(String result) {
		Gson gson = new Gson();
		JiFenInfo statusInfo = gson.fromJson(result, JiFenInfo.class);
		if(statusInfo != null){
			int status = statusInfo.getStatus();
			if (status == 200) {
				Utils.showText(JiFenDetailActivity.this, "兑换成功...");
				AppContext.mSharedPref.putSharePrefString(SharedPrefConstant.JIFEN, "当前积分"+statusInfo.getPoint());
				finish();
			}else if (status == 500) {
				Utils.showText(JiFenDetailActivity.this, "兑换失败,可能是您的积分不够...");
			}
		}
	}
}
