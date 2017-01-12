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
import com.car.shopping.entity.BaseTJYJInfo;
import com.car.shopping.entity.DataInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JiFenRuleActivity extends BaseActivity{

	private LinearLayout back;
	private TextView content;
	private BaseTJYJInfo baseInfo;
	private DataInfo dataInfo;
	private String detail = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jifenrule);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		content = getView(R.id.content);
	}

	@Override
	public void initData() {
		detail = getIntent().getExtras().getString("detail");
		if(detail != null){
			content.setText(detail);
		}
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
