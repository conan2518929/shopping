package com.car.shopping.http;

import android.content.Context;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public abstract class VolleyInterface {
	
	private Context mContext;
	public static Listener<String> mListener;
	public static ErrorListener mErrorListtener;

	public VolleyInterface(Context context, Listener<String> listener, ErrorListener errorListener) {
		this.mContext = context;
		this.mListener = listener;
		this.mErrorListtener = errorListener;
	}

	public abstract void onMySuccess(String result);

	public abstract void onMyError(VolleyError error);

	public Listener<String> loadingListener() {
		mListener = new Listener<String>() {

			@Override
			public void onResponse(String result) {
				onMySuccess(result);
			}
		};
		return mListener;
	}

	public ErrorListener errorListener() {
		mErrorListtener = new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				onMyError(error);
			}
		};
		return mErrorListtener;
	}
}
