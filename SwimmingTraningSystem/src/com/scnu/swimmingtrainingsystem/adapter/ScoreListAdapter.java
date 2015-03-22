package com.scnu.swimmingtrainingsystem.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;

/**
 * 成绩列表匹配数据适配器
 * 
 * @author LittleByte
 * 
 */
public class ScoreListAdapter extends BaseAdapter {
	private Context context;
	private List<String> scores = new ArrayList<String>();


	public ScoreListAdapter(Context context, List<String> scores) {
		this.context = context;
		this.scores = scores;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return scores.size();
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
		viewHolder.tv1.setText("第" + (position + 1) + "名");
		viewHolder.tv2.setText(scores.get(position));
		return convertView;
	}

	public void remove(Object object) {
		scores.remove(object);
		notifyDataSetChanged();
	}

	final class ViewHolder {
		private TextView tv1;
		private TextView tv2;
	}

}
