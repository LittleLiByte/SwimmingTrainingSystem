package com.scnu.swimmingtrainingsystem.adapter;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.model.PlanHolder;

/**
 * 选择运动员数据适配器
 * 
 * @author LittleByte
 * 
 */
public class ChooseAthleteAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	private SparseBooleanArray map;

	public ChooseAthleteAdapter(Context context, List<String> list,
			SparseBooleanArray map) {
		this.context = context;
		this.list = list;
		this.map = map;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PlanHolder holder = null;
		if (convertView == null) {
			holder = new PlanHolder();
			convertView = View
					.inflate(context, R.layout.choose_list_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.ath_name);
			holder.cb = (CheckBox) convertView.findViewById(R.id.tick);
			convertView.setTag(holder);
		} else {
			holder = (PlanHolder) convertView.getTag();
		}

		holder.tv.setText(list.get(position));

		// 根据Map来设置checkbox的选中状况
		holder.cb.setChecked(this.map.get(position));
		return convertView;
	}

	public List<String> getChooseAthlete() {
		return list;
	}
}
