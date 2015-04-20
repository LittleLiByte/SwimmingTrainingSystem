package com.scnu.swimmingtrainingsystem.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;

public class TimeLineListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	public TimeLineListAdapter(Context context,
			ArrayList<HashMap<String, String>> listItem) {
		this.context = context;
		this.list = listItem;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.timeline_item, null);
			viewHolder.tv_between_time = (TextView) convertView
					.findViewById(R.id.tv_between_time);
			viewHolder.tv_ranking = (TextView) convertView
					.findViewById(R.id.tv_ranking);
			viewHolder.tv_score = (TextView) convertView
					.findViewById(R.id.tv_score);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			viewHolder.tv_between_time.setText("");
			viewHolder.tv_ranking.setText("µÚ1Ãû");
			viewHolder.tv_score
					.setText(list.get(position).get("athlete_score"));
		} else {
			viewHolder.tv_between_time.setText(list.get(position).get(
					"score_between"));
			viewHolder.tv_ranking.setText(list.get(position).get(
					"athlete_ranking"));
			viewHolder.tv_score
					.setText(list.get(position).get("athlete_score"));
		}
		return convertView;
	}

	final class ViewHolder {
		private TextView tv_between_time;
		private TextView tv_ranking;
		private TextView tv_score;
	}

}
