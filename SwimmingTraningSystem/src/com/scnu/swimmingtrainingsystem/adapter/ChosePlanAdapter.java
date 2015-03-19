package com.scnu.swimmingtrainingsystem.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.scnu.swimmingtrainingsystem.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.model.Plan;
import com.scnu.swimmingtrainingsystem.model.PlanHolder;

/**
 * 选择计划数据适配器
 * 
 * @author LittleByte
 * 
 */
public class ChosePlanAdapter extends BaseAdapter {
	private Context context;
	private List<Plan> list;
	private Map<Integer, Boolean> map;

	public ChosePlanAdapter(Context context, List<Plan> list,
			HashMap<Integer, Boolean> map) {
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

//		holder.tv.setText(list.get(position).getName());

		// 根据Map来设置checkbox的选中状况
		holder.cb.setChecked(this.map.get(position));
		return convertView;
	}

}
