package com.scnu.swimmingtrainingsystem.adapter;

import com.scnu.swimmingtrainingsystem.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * 成绩列表匹配数据适配器
 * 
 * @author LittleByte
 * 
 */
public class MatchAdapter extends BaseAdapter {
	private Context context;
	private String[] scores;

	public MatchAdapter(Context context, String[] scores) {
		this.context = context;
		this.scores = scores;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return scores.length;
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.matchscore_list_item, null);
			viewHolder.tv1 = (TextView) convertView.findViewById(R.id.ranking);
			viewHolder.tv2 = (TextView) convertView
					.findViewById(R.id.match_score);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv1.setText((position + 1) + "");
		viewHolder.tv2.setText(scores[position]);
		return convertView;
	}

	final class ViewHolder {
		private TextView tv1;
		private TextView tv2;

	}

}
