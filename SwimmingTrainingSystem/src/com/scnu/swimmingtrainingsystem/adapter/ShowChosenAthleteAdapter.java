package com.scnu.swimmingtrainingsystem.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
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
	private List<String> swimGesture = new ArrayList<String>();
	private SparseIntArray spinnerSelection = new SparseIntArray();
	private Map<String, String> athMap;

	public ShowChosenAthleteAdapter(Context context, List<String> list,
			SparseIntArray spinnerSelection, Map<String, String> athMap) {
		this.context = context;
		this.list = list;
		this.spinnerSelection = spinnerSelection;
		this.athMap = athMap;

		swimGesture.add("自由泳");
		swimGesture.add("仰泳");
		swimGesture.add("蛙泳");
		swimGesture.add("蝶泳");
		swimGesture.add("混合泳");
		initAthMap();
	}

	private void initAthMap() {
		// TODO Auto-generated method stub
		if (athMap == null || athMap.size() == 0) {
			athMap=new HashMap<String, String>();
			for(int i=0;i<list.size();i++){
				athMap.put(list.get(i), "自由泳");
			}
		}
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
			viewHolder.spGesture = (Spinner) view
					.findViewById(R.id.chosen_gesture);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, swimGesture);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		viewHolder.spGesture.setAdapter(adapter);

		viewHolder.tvTitle.setText(this.list.get(position));
		viewHolder.spGesture
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int selection, long id) {
						// TODO Auto-generated method stub
						spinnerSelection.put(position, selection);
						String gestrue = swimGesture.get(selection);
						athMap.put(list.get(position), gestrue);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
		viewHolder.spGesture.setSelection(spinnerSelection.get(position));
		return view;
	}

	final static class ViewHolder {
		private TextView tvTitle;
		private Spinner spGesture;
	}

	public SparseIntArray getSpinnerSelection() {
		return spinnerSelection;
	}

	public Map<String, String> getMap() {
		return athMap;
	}
}
