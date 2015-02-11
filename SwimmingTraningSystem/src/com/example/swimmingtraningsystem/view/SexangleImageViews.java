package com.example.swimmingtraningsystem.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.swimmingtraningsystem.R;
import com.example.swimmingtraningsystem.model.ViewBean;

/**
 * 自定义六边形ImageView
 * 
 * @author LittleByte
 * 
 */
public class SexangleImageViews extends ImageView {

	private int mWidth;
	private int mHeight;
	@SuppressWarnings("unused")
	private int centreX;
	private int centreY;
	private int mLenght;
	private Paint paint;

	private int color;
	private float textsize = 24;

	private int home;
	private String texts;
	private Bitmap home_flight;
	private int state = 1; // 按下

	private OnSexangleImageClickListener listener;
	private Context mContext;

	public SexangleImageViews(Context context) {
		super(context);

	}

	public SexangleImageViews(Context context, ViewBean bean) {
		super(context);
		mContext = context;
		setCustomAttributes(bean);
	}

	private void setCustomAttributes(ViewBean bean) {
		textsize = bean.getTextsize();
		home = bean.getHome();
		texts = bean.getTexts();

		home_flight = bitmaps[home];
		color = R.color.transparent;
	}

	@SuppressLint("Recycle")
	public SexangleImageViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.sexangleImageView);
		color = typedArray.getInt(R.styleable.sexangleImageView_backcolor, 0);
		textsize = typedArray.getDimension(
				R.styleable.sexangleImageView_textSize, 24);
		home = typedArray.getInt(R.styleable.sexangleImageView_home, 0);
		texts = typedArray.getString(R.styleable.sexangleImageView_texts);

		home_flight = bitmaps[home];

	}

	/*
	 * private int[] colors = { getResources().getColor(R.color.color_remind),
	 * getResources().getColor(R.color.color_train),
	 * getResources().getColor(R.color.color_setting),
	 * getResources().getColor(R.color.color_sales) };
	 */

	private Bitmap[] bitmaps = {
			BitmapFactory.decodeResource(getResources(), R.drawable.people1),
			BitmapFactory.decodeResource(getResources(), R.drawable.plan),
			BitmapFactory.decodeResource(getResources(), R.drawable.clock1),
			BitmapFactory.decodeResource(getResources(), R.drawable.result1),
			BitmapFactory.decodeResource(getResources(), R.drawable.setting),

	};

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mWidth = getWidth();
		mHeight = getHeight();

		// 中心点
		centreX = mWidth / 2;
		centreY = mHeight / 2;
		mLenght = mWidth / 2;

		double radian30 = 30 * Math.PI / 180;
		float a = (float) (mLenght * Math.sin(radian30));
		float b = (float) (mLenght * Math.cos(radian30));
		float c = (mHeight - 2 * b) / 2;

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(color);
		paint.setAlpha(100);

		// 画六边形
		Path path = new Path();
		path.moveTo(getWidth(), getHeight() / 2);
		path.lineTo(getWidth() - a, getHeight() - c);
		path.lineTo(getWidth() - a - mLenght, getHeight() - c);
		path.lineTo(0, getHeight() / 2);
		path.lineTo(a, c);
		path.lineTo(getWidth() - a, c);
		path.close();
		canvas.drawPath(path, paint);

		paint.setColor(Color.WHITE);
		paint.setTextSize(textsize);
		// 去锯齿
		paint.setAntiAlias(true);
		// 画背景
		Matrix matrix = new Matrix();
		int x = this.getWidth() / 2 - home_flight.getWidth() / 2;
		int y = this.getHeight() / 2 - home_flight.getHeight() / 2;
		matrix.postTranslate(x, y);
		canvas.drawBitmap(home_flight, matrix, paint);
		// 文字水平居中
		paint.setTextSize(24);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(texts, getWidth() / 2, this.getHeight() - 33, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float start = 1.0f;
		float end = 0.94f;
		Animation scaleAnimation = new ScaleAnimation(start, end, start, end,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		Animation endAnimation = new ScaleAnimation(end, start, end, start,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(100);
		scaleAnimation.setFillAfter(true);
		endAnimation.setDuration(100);
		endAnimation.setFillAfter(true);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.startAnimation(scaleAnimation);
			float edgeLength = ((float) getWidth()) / 2;
			float radiusSquare = edgeLength * edgeLength * 3 / 4;
			float dist = (event.getX() - getWidth() / 2)
					* (event.getX() - getWidth() / 2)
					+ (event.getY() - getHeight() / 2)
					* (event.getY() - getHeight() / 2);
			if (dist <= radiusSquare) {// 点中六边形区域
				if (paint != null) {
					paint.setColor(Color.GREEN);
					paint.setAlpha(50);
					invalidate();
				}
			}

			break;

		case MotionEvent.ACTION_UP:
			if (paint != null) {
				this.startAnimation(endAnimation);
				paint.setColor(Color.BLACK);
				paint.setAlpha(100);
				if (listener != null) {
					listener.onClick(this);
				}
				invalidate();
			}
			break;
		// 滑动出去不会调用action_up,调用action_cancel
		case MotionEvent.ACTION_CANCEL:
			if (paint != null) {
				this.startAnimation(endAnimation);
				paint.setColor(getResources().getColor(R.color.transparent));
				invalidate();
			}
			break;
		}
		return true;
	}

	public void setOnSexangleImageClick(OnSexangleImageClickListener listener) {
		this.listener = listener;
	}

	public interface OnSexangleImageClickListener {
		public void onClick(View view);
	}
}
