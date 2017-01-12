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


/** ΢�ſͻ��˻ص�activityʾ�� */
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
	            //����ص�
	        	System.out.println("=====����ص�====");
	        	result = "�ɹ�";
				Toast.makeText(context, result , 1).show();
	            break;
	        default:
	        	result = "ʧ��";
	            break;
	        }
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "ȡ��";
//			Toast.makeText(context, result , 1).show();
			// ����ȡ��
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "����";
//			Toast.makeText(context, result , 1).show();
			// ����ܾ�
			break;
		default:
			break;
		}
		finish();
	}
}
