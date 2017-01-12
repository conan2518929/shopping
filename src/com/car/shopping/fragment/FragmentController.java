package com.car.shopping.fragment;

import com.car.shopping.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
/**
 * 2015-9-17 易戈
 * 
 * 这个类是fragment控制器
 *initFragment()初始化4个fragment
 */
public class FragmentController {

	private int containerId;
	private FragmentManager fm;
	private HomeFragment homeFragment;
	private SearchFragment searchFragment;
	private AddressBookFragment addressBookFragment;
	private MyFragment meFragment;
	private static FragmentController controller;
	/**
	 * 创建单例模式
	 * */
	public static FragmentController getInstance(Activity activity, int containerId) {
		if (controller == null) {
			controller = new FragmentController(activity, containerId);
		}
		return controller;
	}
	
	private FragmentController(Activity activity, int containerId) {
		this.containerId = containerId;
		fm = activity.getFragmentManager();
		showFragment(0);
	}
	
	public void hideFragments(FragmentTransaction ft) {
		if (homeFragment != null)
			ft.hide(homeFragment);
		if (searchFragment != null)
			ft.hide(searchFragment);
		 if (addressBookFragment != null)
		 ft.hide(addressBookFragment);
		if (meFragment != null)
			ft.hide(meFragment);
	}
	public void showFragment(int index) {
		FragmentTransaction ft = fm.beginTransaction();
		hideFragments(ft);
		switch (index) {
		case 0:
			if (homeFragment != null)
				ft.show(homeFragment);
			else {
				homeFragment = new HomeFragment();
				ft.add(R.id.fragment_container, homeFragment);
			}
			break;
		case 1:
			if (searchFragment != null)
				ft.show(searchFragment);
			else {
				searchFragment = new SearchFragment();
				ft.add(R.id.fragment_container, searchFragment);
			}
			break;
		case 2:
			if (addressBookFragment != null)
				ft.show(addressBookFragment);
			else {
				addressBookFragment = new AddressBookFragment();
				ft.add(R.id.fragment_container, addressBookFragment);
			}
			break;
		case 3:
			if (meFragment != null)
				ft.show(meFragment);
			else {
				meFragment = new MyFragment();
				ft.add(R.id.fragment_container, meFragment);
			}
			break;
		}
		ft.commit();
	}
	public void deleteFragment(int index) {
		FragmentTransaction ft = fm.beginTransaction();
		hideFragments(ft);
		switch (index) {
		case 0:
			if (homeFragment != null)
				ft.show(homeFragment);
			ft.remove(homeFragment);
			break;
		case 1:
			if (searchFragment != null)
				ft.remove(searchFragment);
			break;
		case 2:
			if (addressBookFragment != null)
				ft.remove(addressBookFragment);
			break;
		case 3:
			if (meFragment != null)
				ft.remove(meFragment);
			break;
		}
		ft.commit();
	}
}