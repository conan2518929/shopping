package com.car.shopping.wxapi;

import com.car.shopping.utils.WXManager;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


/** 微信客户端回调activity示例 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
//	private IWXAPI api;
	private Context context = WXEntryActivity.this;
	private BaseResp resp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, false);
//		AppContext.wxApi.handleIntent(getIntent(), this);
		WXManager.instance().handleIntent(getIntent(), this);
	}
	
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	    WXManager.instance().handleIntent(getIntent(), this);
	}
	
	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		if (resp != null) {
			resp = resp;
		}
		
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			switch (resp.getType()) {
	        case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
	            //分享回调
	        	System.out.println("=====分享回调====");
	        	result = "成功";
				Toast.makeText(context, result , 1).show();
	            break;
	        default:
	        	result = "失败";
	            break;
	        }
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "取消";
//			Toast.makeText(context, result , 1).show();
			// 分享取消
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "出错";
//			Toast.makeText(context, result , 1).show();
			// 分享拒绝
			break;
		default:
			break;
		}
		finish();
	}
}
