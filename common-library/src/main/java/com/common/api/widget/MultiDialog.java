package com.common.api.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.common.api.R;

public class MultiDialog extends Dialog {

	Context context;
	private String title;
	private String content;
	private String leftButton;
	private String rightButton;
	private TextView dialog_title;
	private TextView dialog_content;
	private Button dialog_btn_left;
	private Button dialog_btn_right;
	private android.view.View.OnClickListener leftListener;
	private android.view.View.OnClickListener rightListener;

	public MultiDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public MultiDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public MultiDialog(Context context, int theme, String title,
			String content, String leftButton, String rightButton) {
		super(context, theme);
		this.context = context;
		this.title = title;
		this.content = content;
		this.leftButton = leftButton;
		this.rightButton = rightButton;
	}

	public MultiDialog(Context context, int theme, String title,
			String content, String leftButton, String rightButton,
			android.view.View.OnClickListener leftListener,
			android.view.View.OnClickListener rightListener) {
		super(context, theme);
		this.context = context;
		this.title = title;
		this.content = content;
		this.leftButton = leftButton;
		this.rightButton = rightButton;
		this.leftListener = leftListener;
		this.rightListener = rightListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lashou_multi_dialog);
		dialog_title = (TextView) findViewById(R.id.dialog_title);
		dialog_content = (TextView) findViewById(R.id.dialog_content);
		dialog_btn_left = (Button) findViewById(R.id.dialog_btn_left);
		dialog_btn_right = (Button) findViewById(R.id.dialog_btn_right);
		if (title != null && !"".equals(title)) {
			dialog_title.setText(title);
		}
		if (content != null && !"".equals(content)) {
			dialog_content.setText(content);
		}
		if (leftButton != null && !"".equals(leftButton)) {
			dialog_btn_left.setText(leftButton);
		} else {
			dialog_btn_left.setText("");
		}
		if (rightButton != null && !"".equals(rightButton)) {
			dialog_btn_right.setText(rightButton);
		} else {
			dialog_btn_right.setText("");
		}
		if (leftListener == null) {
			dialog_btn_left.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		} else {
			dialog_btn_left.setOnClickListener(leftListener);
		}
		if (rightListener == null) {
			dialog_btn_right.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
		} else {
			dialog_btn_right.setOnClickListener(rightListener);
		}
	}
}