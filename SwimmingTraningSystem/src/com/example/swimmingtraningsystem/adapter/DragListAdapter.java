package com.example.swimmingtraningsystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.model.Athlete;

/**
 * 运动员名字拖拽列表数据适配器
 * 
 * @author LittleByte
 * 
 */
public class DragListAdapter extends BaseAdapter {

	private Context context;
	private List<Athlete> lists;

	public DragListAdapter(Context context, List<Athlete> lists) {
		this.context = context;
		this.lists = lists;
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
		return 0;
	}

	public List<Athlete> getList() {
		return lists;
	}

	public void remove(Object dragItem) {
		lists.remove(dragItem);
	}

	public void insert(Object dragItem, int position) {
		lists.add(position, (Athlete) dragItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.drag_list_item, null);
			holder.athName = (TextView) convertView
					.findViewById(R.id.drag_list_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.athName.setText(lists.get(position).getName());
		return convertView;
	}

	final class ViewHolder {
		private TextView athName;
	}
}
