package com.example.swimmingtraningsystem.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swimmingtraningsystem.R;

/********************************************************************
 * [Summary] TODO ���ڴ˴���Ҫ����������ʵ�ֵĹ��ܡ���Ϊ����ע����Ҫ��Ϊ����IDE����������tip��������ؼ�����Ҫ [Remarks]
 * TODO ���ڴ˴���ϸ������Ĺ��ܡ����÷�����ע������Լ���������Ĺ�ϵ.
 *******************************************************************/

public class LoadingDialog extends Dialog {
	private Context context = null;
	private static LoadingDialog customProgressDialog = null;

	public LoadingDialog(Context context) {
		super(context);
		this.context = context;
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	public static LoadingDialog createDialog(Context context) {
		customProgressDialog = new LoadingDialog(context,
				R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.base_loading);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

		ImageView imageView = (ImageView) customProgressDialog
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}

	/**
	 * 
	 * [Summary] setTitile ����
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public LoadingDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	/**
	 * 
	 * [Summary] setMessage ��ʾ����
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public LoadingDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.txtLoading);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}
}
