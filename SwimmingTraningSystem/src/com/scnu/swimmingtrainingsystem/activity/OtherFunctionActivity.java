package com.scnu.swimmingtrainingsystem.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.R;
import com.scnu.swimmingtrainingsystem.fragment.FrequenceFragment;
import com.scnu.swimmingtrainingsystem.fragment.SprintFragment;

/**
 * 其他功能界面
 * 
 * @author LittleByte
 * 
 */
@SuppressLint("SimpleDateFormat")
public class OtherFunctionActivity extends FragmentActivity implements
		OnClickListener {

	private ViewPager viewpager;
	private TextView tvDash;
	private TextView tvSprint;
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
	SprintFragment sprintFragment;
	FrequenceFragment dashFragment;
	/**
	 * 当前选中的项
	 */
	int currenttab = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_function);
		try {
			initView();
			initData();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			startActivity(new Intent(this, LoginActivity.class));
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		viewpager = (ViewPager) findViewById(R.id.vp_fuction);
		tvDash = (TextView) findViewById(R.id.tvTag1);
		tvDash.setOnClickListener(this);
		tvSprint = (TextView) findViewById(R.id.tvTag2);
		tvSprint.setOnClickListener(this);
		tvDash.setSelected(true);
		tvSprint.setSelected(false);
	}

	private void initData() {
		initCursor(2);
		sprintFragment = new SprintFragment();
		dashFragment = new FrequenceFragment();
		fragmentList.add(dashFragment);
		fragmentList.add(sprintFragment);
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

	/**
	 * 初始化游标
	 * 
	 * @param size
	 */
	private void initCursor(int size) {
		cursorWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.cursor).getWidth();
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

	/**
	 * 选择对应碎片
	 * 
	 * @param index
	 */
	public void select(int index) {
		int one = 2 * offset + cursorWidth;
		switch (index) {
		case 0:
			animation = new TranslateAnimation(one, 0, 0, 0);
			tvDash.setSelected(true);
			tvSprint.setSelected(false);
			break;
		case 1:
			animation = new TranslateAnimation(0, one, 0, 0);
			tvDash.setSelected(false);
			tvSprint.setSelected(true);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(R.anim.slide_bottom_in,
					R.anim.slide_top_out);
			return false;
		}
		return false;
	}
}
