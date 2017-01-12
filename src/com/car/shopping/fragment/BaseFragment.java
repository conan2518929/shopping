package com.car.shopping.fragment;

import com.car.shopping.activity.MainActivity;

import android.app.Fragment;
import android.os.Bundle;
/**
 * 2015-9-17 易戈
 * 
 * 这个类是添加第N个fragment
 *
 */
public class BaseFragment extends Fragment{
	
	protected MainActivity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (MainActivity) getActivity();
	}
}
