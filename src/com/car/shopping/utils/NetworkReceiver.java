package com.car.shopping.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class NetworkReceiver extends BroadcastReceiver{
	private INetwork mNetwork;
	public static boolean isConnceted = false;
	
	public interface INetwork{
		void hasNet();
		void notNet();
	}
	public void setNetWorkListener(INetwork network){
		this.mNetwork = network;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			if(null != netInfo){
				mNetwork.hasNet();
			}else{
				Utils.showText(context, "请您检查网络是否正常连接...");
				mNetwork.notNet();
			}
		}
	}
}
