package com.scnu.swimmingtrainingsystem.activity;

import com.scnu.swimmingtrainingsystem.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * »¶Ó­µ¼º½Activity
 * @author LittleByte
 *
 */
public class WlecomeActivity extends Activity {
	private AlphaAnimation start_anima;
	private View view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.activity_wlecome, null);
		setContentView(view);
		initView();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
	}

	private void initView() {
		// TODO Auto-generated method stub
		start_anima = new AlphaAnimation(0.3f, 1.0f);
		start_anima.setDuration(2000);
		view.startAnimation(start_anima);
		start_anima.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				redirectTo();
			}
		});
	}

	/**
	 * Ìø×ªÖÁµÇÂ¼Ò³Ãæ
	 */
	private void redirectTo() {
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		finish();
	}
}
