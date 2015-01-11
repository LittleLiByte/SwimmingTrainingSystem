package com.example.swimmingtraningsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.fragment.AddPlanFragment;
import com.example.swimmingtraningsystem.fragment.ViewPlanFragment;

public class PlanActivity extends FragmentActivity implements OnClickListener {

	private ViewPager viewpager;
	private TextView viewPlan;
	private TextView addPlan;
	private int offset; // 间隔
	private int cursorWidth; // 游标的长度
	private ImageView cursor = null;
	private Animation animation = null;
	private MyFrageStatePagerAdapter adapter;
	/**
	 * 页面集合
	 */
	List<Fragment> fragmentList = new ArrayList<Fragment>();

	/**
	 * Fragment（页面）
	 */
	AddPlanFragment addPlanFragment;
	ViewPlanFragment viewPlanFragment;
	/**
	 * 当前选中的项
	 */
	int currenttab = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan);
		initView();
		initData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		viewpager = (ViewPager) findViewById(R.id.plan_container);
		viewPlan = (TextView) findViewById(R.id.tvTag1);
		viewPlan.setOnClickListener(this);
		addPlan = (TextView) findViewById(R.id.tvTag2);
		addPlan.setOnClickListener(this);
	}

	private void initData() {
		initCursor(2);
		addPlanFragment = new AddPlanFragment();
		viewPlanFragment = new ViewPlanFragment();
		fragmentList.add(viewPlanFragment);
		fragmentList.add(addPlanFragment);
		adapter = new MyFrageStatePagerAdapter(getSupportFragmentManager());
		viewpager.setAdapter(adapter);
		viewpager.setOffscreenPageLimit(0);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				select(viewpager.getCurrentItem());
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void initCursor(int size) {
		cursorWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.cursor1).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		offset = ((dm.widthPixels / size) - cursorWidth) / 2;
		cursor = (ImageView) findViewById(R.id.ivCursor);
		Matrix matrix = new Matrix();
		matrix.setTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tvTag1:
			viewpager.setCurrentItem(0);
			break;
		case R.id.tvTag2:
			viewpager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}

	public void select(int index) {
		int one = 2 * offset + cursorWidth;
		switch (index) {
		case 0:
			animation = new TranslateAnimation(one, 0, 0, 0);
			break;
		case 1:
			animation = new TranslateAnimation(0, one, 0, 0);
			break;
		default:
			break;
		}
		animation.setFillAfter(true);
		animation.setDuration(300);
		cursor.startAnimation(animation);
	}

	class MyFrageStatePagerAdapter extends FragmentStatePagerAdapter {

		public MyFrageStatePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;
		}

	}
}
