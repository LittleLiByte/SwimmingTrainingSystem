package com.example.swimmingtraningsystem.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.model.Athlete;

/**
 * 添加计划数据适配器
 * 
 * @author LittleByte
 * 
 */
public class AddPlanListAdapter extends BaseAdapter {
	private Context context;
	private List<Athlete> list;
	private float x, ux;
	private Button curDel_btn;
	private Map<Long, Boolean> map;

	public AddPlanListAdapter(Context context, List<Athlete> list,
			Map<Long, Boolean> map) {
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
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(
					R.layout.add_plan_list_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.plan_tv);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.tvTitle.setText(this.list.get(position).getName());
		return view;
	}

	final static class ViewHolder {
		private TextView tvTitle;
	}

}
