package com.dldzkj.app.renxing;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

/**
 * 提示信息的管理
 */

public class PromptManager {
	private static ProgressDialog dialog;
	private static boolean isLog = true;

	public static void showLog(String TAG, String msg) {
		if (isLog && msg != null)
			Log.i(TAG, msg);
	}

	public static ProgressDialog getDialog() {
		return dialog;
	}

	public static void showProgressDialog(Context context) {
		dialog = new ProgressDialog(context);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setMessage(context.getResources().getString(R.string.progress_tip));
		dialog.show();
		showLog("dialog", dialog + "===show" + "=====" + context.getClass().getName());
	}

	public static void closeProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			showLog("dialog", dialog + "===close");
		}
	}


	/**
	 * 确定/取消对话框
	 * 
	 * @param context
	 */
	public static void showYesOrNo(Context context, String title, final OnClickListener listener) {
		Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher)//
				.setTitle(R.string.app_name)//
				.setMessage(title).setPositiveButton(context.getResources().getString(R.string.is_positive), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.onClick(dialog, which);
					}
				}).setNegativeButton(context.getResources().getString(R.string.cancel), null).show();

	}

	/**
	 * 显示错误提示框
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showErrorDialog(Context context, String msg) {
		new Builder(context)//
				.setIcon(R.drawable.ic_launcher)//
				.setTitle(R.string.app_name)//
				.setMessage(msg)//
				.setNegativeButton(context.getString(R.string.is_positive), null)//
				.show();
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int msgResId) {
		Toast.makeText(context, msgResId, Toast.LENGTH_LONG).show();
	}

	// 当测试阶段时true
	private static final boolean isShow = true;

	/**
	 * 测试用 在正式投入市场：删
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToastTest(Context context, String msg) {
		if (isShow) {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

}
