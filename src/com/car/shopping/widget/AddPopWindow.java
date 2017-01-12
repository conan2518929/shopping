package com.car.shopping.widget;

import com.car.shopping.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class AddPopWindow extends PopupWindow {
	
	public AddPopWindow(final Activity context,View conentView) {

		// ����SelectPicPopupWindow��View
				this.setContentView(conentView);
				// ����SelectPicPopupWindow��������Ŀ�
				this.setWidth(LayoutParams.WRAP_CONTENT);
				// ����SelectPicPopupWindow��������ĸ�
				this.setHeight(LayoutParams.WRAP_CONTENT);

				// ����SelectPicPopupWindow��������ɵ��
				this.setFocusable(true);
				this.setOutsideTouchable(true);
				// ˢ��״̬
				this.update();
				// ʵ����һ��ColorDrawable��ɫΪ��͸��
				ColorDrawable dw = new ColorDrawable(0000000000);
				// ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener �����������ؼ��仯�Ȳ���
				this.setBackgroundDrawable(dw);

				// ����SelectPicPopupWindow�������嶯��Ч��
				this.setAnimationStyle(R.style.AnimationPreview);
	}

	/**
	 * ��ʾpopupWindow
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
