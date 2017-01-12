package com.car.shopping.activity;

import com.car.shopping.R;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.entity.BaseBodyInfo;
import com.car.shopping.entity.BaseVinInfo;
import com.car.shopping.entity.BodyInfo;
import com.car.shopping.entity.ResultInfo;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.show.api.ShowApiRequest;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VinActivity extends BaseActivity {

	private LinearLayout back;
	private Button chaxun;
	private EditText et;
	private String mVin = "";
	private TextView txt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vin);
		initViews();
		initData();
	}

	@Override
	public void initViews() {
		back = getView(R.id.back);
		chaxun = getView(R.id.chaxun);
		et = getView(R.id.et);
		txt = (TextView)findViewById(R.id.tv);
	}

	@Override
	public void initData() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		chaxun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mVin = et.getText().toString().trim();
				if( mVin.length() == 17){
					queryVin();
				}
			}
		});
	}
	
	private Handler mHandler = new Handler();
	/**
	 * 初始化数据
	 */
	private void queryVin() {
		showLoadingDialog();
		new Thread() {
			// 在新线程中发送网络请求
			public void run() {
				
				String appid = "29616";// 要替换成自己的
				String secret = "2c3e496a2e2840b8b1e5b7869488401f";// 要替换成自己的mVin LGBM4AE45ES010745
				final String res = new ShowApiRequest("http://route.showapi.com/851-1", appid, secret).addTextPara("VIN", mVin).post();
				System.out.println(res);
				// 把返回内容通过handler对象更新到界面
				mHandler.post(new Thread() {
					public void run() {
						Gson gson = new Gson();
						BaseVinInfo baseVinInfo =  gson.fromJson(res, BaseVinInfo.class);
						if(baseVinInfo != null){
							if(baseVinInfo.getShowapi_res_code() == 0){
								BodyInfo showapi_res_body = baseVinInfo.getShowapi_res_body();
								if(showapi_res_body != null){
									ResultInfo result = showapi_res_body.getResult();
									if(result != null){
										BaseBodyInfo body = result.getBody();
										if(body != null){
											txt.setText( body.getCARINFO());
										}
									}
									Utils.showText(VinActivity.this, showapi_res_body.getRet_msg());
								}
							}else{
								Utils.showText(VinActivity.this, "查询失败");
							}
						}
						dismissLoadingDialog();
					}
				});
			}
		}.start();				
	}
}
