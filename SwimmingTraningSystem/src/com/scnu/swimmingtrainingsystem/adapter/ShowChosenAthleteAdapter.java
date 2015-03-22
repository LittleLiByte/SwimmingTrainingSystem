package com.scnu.swimmingtrainingsystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;

/**
 * 被选中的运动员数据适配器
 * 
 * @author LittleByte
 * 
 */
public class ShowChosenAthleteAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;

	public ShowChosenAthleteAdapter(Context context, List<String> list) {
		this.context = context;
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
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.chosen_athlete_list_item, null);
			viewHolder.tvTitle = (TextView) view
					.findViewById(R.id.tv_chosen_ath_name);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.tvTitle.setText(this.list.get(position));
		return view;
	}

	final static class ViewHolder {
		private TextView tvTitle;
	}

}
