package com.example.swimmingtraningsystem.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.model.Athlete;

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
			viewHolder.btnDel = (Button) view.findViewById(R.id.plan_cancle);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 换掉了原来listview中的onItemClick
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		// 为每一个view项设置触控监听
		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				final ViewHolder holder = (ViewHolder) v.getTag();

				// 当按下时处理
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					// 获取按下时的x轴坐标
					x = event.getX();
					// 判断之前是否出现了删除按钮如果存在就隐藏
					if (curDel_btn != null) {
						if (curDel_btn.getVisibility() == View.VISIBLE) {
							curDel_btn.setVisibility(View.GONE);
							return true;
						}
					}

				} else if (event.getAction() == MotionEvent.ACTION_UP) {// 松开处理

					// 获取松开时的x坐标
					ux = event.getX();

					// 判断当前项中按钮控件不为空时
					if (holder.btnDel != null) {
						// 按下和松开绝对值差当大于20时显示删除按钮，否则不显示
						if (Math.abs(x - ux) > 20) {
							holder.btnDel.setVisibility(View.VISIBLE);
							curDel_btn = holder.btnDel;
							return true;
						}
					}
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;

				} else {

				}
				return false;
			}
		});
		viewHolder.tvTitle.setText(this.list.get(position).getName());

		// 为删除按钮添加监听事件，实现点击删除按钮时删除该项
		viewHolder.btnDel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (curDel_btn != null)
					curDel_btn.setVisibility(View.GONE);
				map.put(list.get(position).getId(), false);
				list.remove(position);
				notifyDataSetChanged();

			}
		});
		return view;
	}

	final static class ViewHolder {
		private TextView tvTitle;
		private Button btnDel;
	}

}
