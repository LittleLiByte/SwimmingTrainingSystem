package com.scnu.swimmingtrainingsystem.adapter;

import java.util.HashMap;
import java.util.List;

import com.scnu.swimmingtrainingsystem.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.model.Plan;

/**
 * 查看计划列表数据适配器
 * 
 * @author LittleByte
 * 
 */
public class ViewPlanListAdapter extends BaseAdapter {
	private Context context;
	private List<Plan> lists;
	/**
	 * 用来控制CheckBox的选中状况
	 */
	private HashMap<Long, Boolean> isSelectedMap;
	/**
	 * 用来控制CheckBox的显示状况
	 */
	private HashMap<Long, Integer> isvisibleMap;

	public ViewPlanListAdapter(Context context, List<Plan> lists) {
		this.context = context;
		this.lists = lists;
		isSelectedMap = new HashMap<Long, Boolean>();
		isvisibleMap = new HashMap<Long, Integer>();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		for (int i = 0; i < lists.size(); i++) {
			getIsSelectedMap().put(lists.get(i).getId(), false);
			getIsvisibleMap().put(lists.get(i).getId(), CheckBox.INVISIBLE);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return lists.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.view_plan_list_item, null);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.view_plan_tv);
			viewHolder.btnDel = (CheckBox) convertView.findViewById(R.id.check);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.btnDel.setChecked(getIsSelectedMap().get(
					lists.get(position).getId()));
			viewHolder.btnDel.setVisibility(getIsvisibleMap().get(
					lists.get(position).getId()));
		}
		viewHolder.tvTitle.setText(lists.get(position).getName());

		return convertView;
	}

	public final class ViewHolder {
		private TextView tvTitle;
		public CheckBox btnDel;
	}

	/**
	 * 获取CheckBox的选中状况
	 * 
	 * @return
	 */
	public HashMap<Long, Boolean> getIsSelectedMap() {
		return isSelectedMap;
	}

	/**
	 * 获取CheckBox的显示状况
	 * 
	 * @return
	 */
	public HashMap<Long, Integer> getIsvisibleMap() {
		return isvisibleMap;
	}
}
