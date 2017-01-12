package com.car.shopping.utils;

import java.io.File;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class WXManager {
	private static final String URL_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
	private static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
	private static final int THUMB_SIZE = 150;
	private static WXManager mWXManager = null;

	private IWXAPI api;

	public static WXManager instance() {
		if(null == mWXManager){
			mWXManager = new WXManager();
		}
		return mWXManager;
	}

	/**
	 * @param api
	 *            the api to set
	 */
	public void setApi(IWXAPI api) {
		this.api = api;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	public boolean handleIntent(Intent arg0, IWXAPIEventHandler arg1) {
		if (api == null)
			return false;
		return api.handleIntent(arg0, arg1);
	}

	public boolean isWXAppInstalled() {
		if (api == null)
			return false;
		return api.isWXAppInstalled();
	}

	public boolean isWXAppSupportAPI() {
		if (api == null)
			return false;
		return api.isWXAppSupportAPI();
	}

	public boolean sendReq(BaseReq arg0) {
		if (api == null)
			return false;
		return api.sendReq(arg0);
	}

	/**
	 * 分享文本到微�??
	 * 
	 * @param isTimelineCb
	 *            是否分享到朋友圈
	 * @param text
	 *            分享到微信的文本内容
	 * @return true 成功，false失败
	 */
	public boolean sendTextToWX(boolean isTimelineCb, String text) {
		if (api == null)
			return false;
		if (TextUtils.isEmpty(text)) {
			return false;
		}

		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = text;

		// 构�?�一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识�??个请�??
		req.message = msg;
		req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;

		// 调用api接口发�?�数据到微信
		return api.sendReq(req);
	}

	/**
	 * 分享图片
	 */
	public boolean sendImageToWX(boolean isTimelineCb, String imagePath,
			String imageUrl) {
		if (api == null)
			return false;
		// 本地图片
		if (imagePath != null) {
			File file = new File(imagePath);
			if (!file.exists()) {
				return false;
			}

			WXImageObject imgObj = new WXImageObject();
			imgObj.setImagePath(imagePath);

			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = imgObj;
			Bitmap bmp = BitmapFactory.decodeFile(imagePath);
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
					THUMB_SIZE, true);
			bmp.recycle();
//			msg.thumbData = Utils.bmpToByteArray(thumbBmp, true);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("img");
			req.message = msg;
			req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline
					: SendMessageToWX.Req.WXSceneSession;
			return api.sendReq(req);
		}
		return false;
	}

	
	private WXWebpageObject wxWebPageObj;
	private WXImageObject wxImgObj;
	private WXMediaMessage wxMediaMsg;
	private SendMessageToWX.Req req;
	
	public BaseReq getReq(String url, String imgPath, String title, Bitmap thumb, int flag){
		if(null == wxWebPageObj)
			wxWebPageObj = new WXWebpageObject();
		
		if(null == wxImgObj)
			wxImgObj = new WXImageObject();
		
		if(null == wxMediaMsg)
			wxMediaMsg = new WXMediaMessage();
		
		if(!TextUtils.isEmpty(url)){
			wxWebPageObj.webpageUrl = url;
			wxMediaMsg.mediaObject = wxWebPageObj;
		}else if(!TextUtils.isEmpty(imgPath)){
			wxImgObj.setImagePath(imgPath);
			wxMediaMsg.mediaObject = wxImgObj;
		}
		
		if(null != thumb)
			wxMediaMsg.setThumbImage(thumb);
		
		if(!TextUtils.isEmpty(title))
			wxMediaMsg.title = title;
		
	    if(null == req)
			req = new SendMessageToWX.Req();
	    req.message = wxMediaMsg;
	    req.transaction = String.valueOf(System.currentTimeMillis());
	    
	    if(flag > 0)
	    	req.scene = SendMessageToWX.Req.WXSceneTimeline;
	    else
	    	req.scene = SendMessageToWX.Req.WXSceneSession;
		return req;
	}
}

