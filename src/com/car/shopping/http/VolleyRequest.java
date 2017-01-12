package com.car.shopping.http;

import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.app.AppContext;

public class VolleyRequest {
	
	public StringRequest stringRequest;
	public Context mContext;
	private static VolleyRequest dragonClientApi = null;

	public static VolleyRequest getInstance() {
		if (dragonClientApi == null) {
			return new VolleyRequest();
		} else {
			return dragonClientApi;
		}
	}

	public void RequestPost(Context mContext, int method, String url, String tag, final Map<String, String> params, VolleyInterface vif) {
		AppContext.getInstance().cancelPendingRequests(tag);
		stringRequest = new StringRequest(url, vif.loadingListener(), vif.errorListener()) {
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				return params;
			}
			
		};
		AppContext.getInstance().addToRequestQueue(stringRequest, tag);
	}

	public void RequestGet(Context mContext, String url, String tag, VolleyInterface vif) {
		AppContext.getInstance().cancelPendingRequests(tag);
		stringRequest = new StringRequest(Method.GET, url, vif.loadingListener(), vif.errorListener());
		AppContext.getInstance().addToRequestQueue(stringRequest, tag);
	}
}
