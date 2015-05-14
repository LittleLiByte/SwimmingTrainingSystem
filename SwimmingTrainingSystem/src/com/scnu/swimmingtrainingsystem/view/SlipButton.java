package com.scnu.swimmingtrainingsystem.view;

import com.scnu.swimmingtrainingsystem.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;


/**
 * 自定义滑动开关
 * @author LittleByte
 *
 */
public class SlipButton extends View {
	public boolean Nowchoose = false;
	private boolean OnSlip = false;
	private float NowX;
	private Rect Btn_on, Btn_off;
	private Bitmap bg_on, bg_off, slip_btn;

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public boolean isChecked() {
		return Nowchoose;
	}

	public void setChecked(boolean check) {
		Nowchoose = check;
		invalidate();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		bg_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_on);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_off);
		slip_btn = BitmapFactory.decodeResource(getResources(),
				R.drawable.redcyfsdfscle);
		Btn_on = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		Btn_off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
				bg_off.getWidth(), slip_btn.getHeight());
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;
		{
			if (NowX < (bg_on.getWidth() / 2) && !Nowchoose) {
				canvas.drawBitmap(bg_off, matrix, paint);
			} else {
				canvas.drawBitmap(bg_on, matrix, paint);
			}
			if (OnSlip) {
				if (NowX >= bg_on.getWidth())
					x = bg_on.getWidth() - slip_btn.getWidth() / 2;
				else
					x = NowX - slip_btn.getWidth() / 2;

			} else {
				if (Nowchoose)
					x = Btn_off.left;
				else
					x = Btn_on.left;
			}
			if (x < 0)
				x = 0;
			else if (x > bg_on.getWidth() - slip_btn.getWidth())
				x = bg_on.getWidth() - slip_btn.getWidth();
			canvas.drawBitmap(slip_btn, x, 0, paint);
		}
	}

	public void SetOnCheckedChangedListener(OnCheckedChangeListener l) {
	}
}
