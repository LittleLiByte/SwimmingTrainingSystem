package com.example.swimmingtraningsystem.adapter;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.util.XUtils;

/**
 * Adapter for Wheel
 */
public class MyAndroidWheelAdapter extends AbstractWheelTextAdapter {
	private String countries[] = XUtils.countries;

	public MyAndroidWheelAdapter(Context context) {
		super(context, R.layout.drag_list_item, NO_RESOURCE);
		setItemTextResource(R.id.drag_list_item_text);
	}

	@Override
	public View getItem(int index, View cachedView, ViewGroup parent) {
		View view = super.getItem(index, cachedView, parent);
		return view;
	}

	@Override
	public int getItemsCount() {
		return countries.length;
	}

	@Override
	protected CharSequence getItemText(int index) {
		return countries[index];
	}
}