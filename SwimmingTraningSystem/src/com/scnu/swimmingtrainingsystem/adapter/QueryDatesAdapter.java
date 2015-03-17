package com.scnu.swimmingtrainingsystem.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QueryDatesAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> list = new ArrayList<String>();

	public QueryDatesAdapter(Context mContext, List<String> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = View.inflate(mContext,
					android.R.layout.simple_list_item_1, null);
		}
		TextView tView = (TextView) convertView
				.findViewById(android.R.id.text1);
		tView.setTextSize(16);
		tView.setText(list.get(position));
		return convertView;
	}

	public void setDatas(List<String> list) {
		this.list.clear();
		this.list.addAll(list);
	}

	public List<String> getList() {
		return this.list;
	}
}
