package com.car.shopping.widget;

import com.car.shopping.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class AddPopWindow extends PopupWindow {
	
	public AddPopWindow(final Activity context,View conentView) {

		// 设置SelectPicPopupWindow的View
				this.setContentView(conentView);
				// 设置SelectPicPopupWindow弹出窗体的宽
				this.setWidth(LayoutParams.WRAP_CONTENT);
				// 设置SelectPicPopupWindow弹出窗体的高
				this.setHeight(LayoutParams.WRAP_CONTENT);

				// 设置SelectPicPopupWindow弹出窗体可点击
				this.setFocusable(true);
				this.setOutsideTouchable(true);
				// 刷新状态
				this.update();
				// 实例化一个ColorDrawable颜色为半透明
				ColorDrawable dw = new ColorDrawable(0000000000);
				// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
				this.setBackgroundDrawable(dw);

				// 设置SelectPicPopupWindow弹出窗体动画效果
				this.setAnimationStyle(R.style.AnimationPreview);
	}

	/**
	 * 显示popupWindow
	 * 
	 * @param parent
	 */
	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, -100, 0);
		} else {
			this.dismiss();
		}
	}
}
