package com.scnu.swimmingtrainingsystem.adapter;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.scnu.swimmingtrainingsystem.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.scnu.swimmingtrainingsystem.util.Constants;

/**
 * androidWheel ˝æ›  ≈‰∆˜
 * 
 * @author LittleByte
 */
public class MyAndroidWheelAdapter extends AbstractWheelTextAdapter {
	private String countries[] = Constants.countries;

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