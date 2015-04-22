package com.scnu.swimmingtrainingsystem.activity;

import com.scnu.swimmingtrainingsystem.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.util.Constants;

/**
 * 使用说明Activity
 * 
 * @author LittleByte
 * 
 */
public class UseTipsActiviy extends Activity {
	private MyApplication application;
	private ExpandableListView expandableListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips);
		try {
			init();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void init() {
		application=(MyApplication) getApplication();
		@SuppressWarnings("unused")
		long userID = (Long) application.getMap().get(Constants.CURRENT_USER_ID);
		
		expandableListView = (ExpandableListView) findViewById(R.id.tips_list);
		final ExpandableListAdapter adapter = new BaseExpandableListAdapter() {

			// 自己定义一个获得文字信息的方法
			TextView getTextView() {
				AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, 64);
				TextView textView = new TextView(UseTipsActiviy.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				textView.setPadding(15, 0, 0, 0);
				textView.setTextSize(24);
				textView.setTextColor(Color.BLACK);
				return textView;
			}

			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return Constants.TITLES.length;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return 1;
			}

			@Override
			public Object getGroup(int groupPosition) {
				// TODO Auto-generated method stub
				return Constants.TITLES[groupPosition];
			}

			@Override
			public Object getChild(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return Constants.CONTENTS[groupPosition][childPosition];
			}

			@Override
			public long getGroupId(int groupPosition) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded,
					View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(UseTipsActiviy.this);
				ll.setOrientation(0);
				TextView textView = getTextView();
				textView.setTextColor(Color.BLACK);
				textView.setText(getGroup(groupPosition).toString());
				ll.addView(textView);
				return ll;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(UseTipsActiviy.this);
				TextView textView = new TextView(UseTipsActiviy.this);
				textView.setTextSize(16);
				textView.setPadding(10, 5, 10, 5);
//				textView.setText(getChild(groupPosition, childPosition)
//						.toString());
				textView.setText(Html.fromHtml(getChild(groupPosition, childPosition)
						.toString()));
				ll.addView(textView);
				return ll;
			}

			@Override
			public boolean isChildSelectable(int groupPosition,
					int childPosition) {
				// TODO Auto-generated method stub
				return false;
			}

		};
		expandableListView.setAdapter(adapter);
	}
}
