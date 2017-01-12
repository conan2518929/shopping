package com.car.shopping.activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.car.shopping.R;
import com.car.shopping.app.AppContext;
import com.car.shopping.app.BaseActivity;
import com.car.shopping.constant.Urls;
import com.car.shopping.entity.BaseTalkNickNameInfo;
import com.car.shopping.entity.StatusInfo;
import com.car.shopping.fragment.FragmentController;
import com.car.shopping.sharepref.SharedPrefConstant;
import com.car.shopping.talk.Constant;
import com.car.shopping.talk.DemoHelper;
import com.car.shopping.talk.DemoModel;
import com.car.shopping.utils.Utils;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.easeui.widget.EaseSwitchButton;
import com.hyphenate.util.EMLog;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements OnClickListener {

	private long mExitTime;

	private LinearLayout ll_one, ll_two, ll_three, ll_four,ll_five;
	private FragmentController controller;
	private ImageView img_four, img_three, img_two, img_one,img_five;
	private TextView tv_four, tv_three, tv_two, tv_one,tv_five;
	private DemoModel settingsModel;
	private EaseSwitchButton vibrateSwitch;
	// 账号在别处登录
	public boolean isConflict = false;
	// 账号被移除
	private boolean isCurrentAccountRemoved = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null && savedInstanceState.getBoolean(Constant.ACCOUNT_REMOVED, false)) {
			// 防止被移除后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			DemoHelper.getInstance().logout(false, null);
			return;
		} else if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
			// 防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
			// 三个fragment里加的判断同理
			return;
		}

		setContentView(R.layout.activity_main);
//		UncaughtException.getInstance().setContext(this);
		initViews();
		initData();
		View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.em_fragment_conversation_settings, null);
		vibrateSwitch = (EaseSwitchButton) v.findViewById(R.id.switch_vibrate);
		settingsModel = DemoHelper.getInstance().getModel();
		vibrateSwitch.closeSwitch();
		settingsModel.setSettingMsgVibrate(false);

		if (getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		int unreadNum = Utils.getUnreadMsgCountTotal();
		AppContext.mSharedPref.putSharePrefInteger(SharedPrefConstant.TALK_NUMBER, unreadNum);
	}

	@Override
	public void initViews() {
		ll_one = (LinearLayout) findViewById(R.id.ll_one);
		ll_two = (LinearLayout) findViewById(R.id.ll_two);
		ll_three = (LinearLayout) findViewById(R.id.ll_three);
		ll_four = (LinearLayout) findViewById(R.id.ll_four);
		ll_five = (LinearLayout) findViewById(R.id.ll_five);
		img_one = (ImageView) findViewById(R.id.img_one);
		img_two = (ImageView) findViewById(R.id.img_two);
		img_three = (ImageView) findViewById(R.id.img_three);
		img_four = (ImageView) findViewById(R.id.img_four);
		img_five = (ImageView) findViewById(R.id.img_five);
		tv_one = (TextView) findViewById(R.id.tv_one);
		tv_two = (TextView) findViewById(R.id.tv_two);
		tv_three = (TextView) findViewById(R.id.tv_three);
		tv_four = (TextView) findViewById(R.id.tv_four);
		tv_five = (TextView) findViewById(R.id.tv_five);
		controller = FragmentController.getInstance(this, R.id.fragment_container);
	}

	@Override
	public void initData() {
		ll_one.setOnClickListener(this);
		ll_two.setOnClickListener(this);
		ll_three.setOnClickListener(this);
		ll_four.setOnClickListener(this);
		ll_five.setOnClickListener(this);
		controller.showFragment(0);
	}

	@Override
	public void onClick(View v) {
		initColor();
		switch (v.getId()) {
		case R.id.ll_one:
			tv_one.setTextColor(this.getResources().getColor(R.color.blue));
			img_one.setImageResource(R.drawable.home_sy_blue);
			controller.showFragment(0);
			break;
		case R.id.ll_two:
			tv_two.setTextColor(this.getResources().getColor(R.color.blue));
			img_two.setImageResource(R.drawable.home_search_blue);
			controller.showFragment(1);
			break;
		case R.id.ll_three:
			boolean isShow = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			if (isShow) {
				tv_three.setTextColor(this.getResources().getColor(R.color.blue));
				img_three.setImageResource(R.drawable.home_txl_blue);
				controller.showFragment(2);
			} else {
				Utils.showText(MainActivity.this, "请登录后查看...");
			}
			break;
		case R.id.ll_four:
			tv_four.setTextColor(this.getResources().getColor(R.color.blue));
			img_four.setImageResource(R.drawable.home_my_blue);
			controller.showFragment(3);
			break;
		case R.id.ll_five:
			tv_five.setTextColor(this.getResources().getColor(R.color.blue));
			img_five.setImageResource(R.drawable.pic_hd);
			boolean isGo1 = AppContext.imp_SharedPref.getSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			if (isGo1) {
				Utils.gotoOtherActivity(MainActivity.this, InteractiveActivity.class);
			} else {
				Utils.showToast(MainActivity.this, "请您登录后查看...", 1000);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 恢复图标默认颜色
	 * */
	public void initColor() {
		tv_one.setTextColor(getResources().getColor(R.color.gray));
		tv_two.setTextColor(getResources().getColor(R.color.gray));
		tv_three.setTextColor(getResources().getColor(R.color.gray));
		tv_four.setTextColor(getResources().getColor(R.color.gray));
		tv_five.setTextColor(getResources().getColor(R.color.gray));
		img_one.setImageResource(R.drawable.home_sy_gray);
		img_two.setImageResource(R.drawable.home_search_gray);
		img_three.setImageResource(R.drawable.home_txl_gray);
		img_four.setImageResource(R.drawable.home_my_gray);
		img_five.setImageResource(R.drawable.pic_hd_hui);
	}

	/**
	 * 监听返回键
	 * */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Utils.showToast(MainActivity.this, "再按一次退出程序", 1000);
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
				super.onBackPressed();
			}
			return true;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private android.app.AlertDialog.Builder accountRemovedBuilder;
	private boolean isConflictDialogShow;
	private boolean isAccountRemovedDialogShow;
	private BroadcastReceiver internalDebugReceiver;
	private BroadcastReceiver broadcastReceiver;
	private LocalBroadcastManager broadcastManager;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		System.out.println("sssssssssssssssssssssss");
		isConflictDialogShow = true;
		DemoHelper.getInstance().logout(false, null);
		String st = getResources().getString(R.string.Logoff_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle(st);
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						logout();
						isConflictDialogShow = false;
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}

	/**
	 * 帐号被移除的dialog
	 */
	private void showAccountRemovedDialog() {
		isAccountRemovedDialogShow = true;
		DemoHelper.getInstance().logout(false, null);
		String st5 = getResources().getString(R.string.Remove_the_notification);
		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (accountRemovedBuilder == null)
					accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				accountRemovedBuilder.setTitle(st5);
				accountRemovedBuilder.setMessage(R.string.em_user_remove);
				accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						accountRemovedBuilder = null;
						finish();
					}
				});
				accountRemovedBuilder.setCancelable(false);
				accountRemovedBuilder.create().show();
				isCurrentAccountRemoved = true;
			} catch (Exception e) {
				EMLog.e(TAG, "---------color userRemovedBuilder error" + e.getMessage());
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("isConflict", isConflict);
		outState.putBoolean(Constant.ACCOUNT_REMOVED, isCurrentAccountRemoved);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra(Constant.ACCOUNT_CONFLICT, false) && !isConflictDialogShow) {
			showConflictDialog();
		} else if (intent.getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
			showAccountRemovedDialog();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}
		try {
			unregisterReceiver(internalDebugReceiver);
		} catch (Exception e) {
		}
	}

	private void logout() {
		AppContext.getInstance().cancelPendingRequests(TAG);
		showLoadingDialog();
		StringRequest stringRequest = new StringRequest(Request.Method.POST, com.car.shopping.constant.Constant.URL_TEST + Urls.LOGOUT, new Response.Listener<String>() {

			@Override
			public void onResponse(String result) {
				dealLogoutShop(result);
				dismissLoadingDialog();
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				dismissLoadingDialog();
				Utils.showText(MainActivity.this, "网络访问失败");
			}
		});
		AppContext.getInstance().addToRequestQueue(stringRequest, TAG);
	}

	private void dealLogoutShop(String result) {
		Gson gson = new Gson();
		StatusInfo statusInfo = gson.fromJson(result, StatusInfo.class);
		int status = statusInfo.getStatus();
		if (status == 200) {
			AppContext.imp_SharedPref.putSharePrefBoolean(SharedPrefConstant.IS_SHOW, false);
			Intent intent = new Intent();
			intent.setAction("com.app.action.broadcast");
			sendBroadcast(intent);
			Intent i = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(i);
		}
	}
	
	/**
	 * 检查当前用户是否被删除
	 */
	public boolean getCurrentAccountRemoved() {
		return isCurrentAccountRemoved;
	}

}
