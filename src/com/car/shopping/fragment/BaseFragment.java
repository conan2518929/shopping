package com.car.shopping.fragment;

import com.car.shopping.activity.MainActivity;

import android.app.Fragment;
import android.os.Bundle;
/**
 * 2015-9-17 �׸�
 * 
 * ���������ӵ�N��fragment
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
