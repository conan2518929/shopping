package com.car.shopping.talk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.ui.VideoCallActivity;
import com.car.shopping.talk.ui.VoiceCallActivity;
import com.hyphenate.util.EMLog;

public class CallReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		if(!DemoHelper.getInstance().isLoggedIn())
		    return;
		//����username
		String from = intent.getStringExtra("from");
		//call type
		String type = intent.getStringExtra("type");
		if("video".equals(type)){ //��Ƶͨ��
		    context.startActivity(new Intent(context, VideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}else{ //��Ƶͨ��
		    context.startActivity(new Intent(context, VoiceCallActivity.class).
		            putExtra("username", from).putExtra("isComingCall", true).
		            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		EMLog.d("CallReceiver", "app received a incoming call");
	}
}