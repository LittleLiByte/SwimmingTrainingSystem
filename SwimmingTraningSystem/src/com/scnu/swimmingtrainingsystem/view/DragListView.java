package com.scnu.swimmingtrainingsystem.view;

import com.scnu.swimmingtrainingsystem.R;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.scnu.swimmingtrainingsystem.adapter.DragListAdapter;

/**
 * ����קlistview
 * 
 * @author LittleByte
 * 
 */
public class DragListView extends ListView {

	private TextView dragTextView;// ����ק�����ʵ����һ��ImageView
	private int dragSrcPosition;// ��ָ�϶���ԭʼ���б��е�λ��
	private int dragPosition;// ��ָ�϶���ʱ�򣬵�ǰ�϶������б��е�λ��

	private int dragPoint;// �ڵ�ǰ�������е�λ��
	private int dragOffset;// ��ǰ��ͼ����Ļ�ľ���(����ֻʹ����y������)

	private WindowManager windowManager;// windows���ڿ�����
	private WindowManager.LayoutParams windowParams;// ���ڿ�����ק�����ʾ�Ĳ���

	private int scaledTouchSlop;// �жϻ�����һ������
	private int upScrollBounce;// �϶���ʱ�򣬿�ʼ���Ϲ����ı߽�
	private int downScrollBounce;// �϶���ʱ�򣬿�ʼ���¹����ı߽�

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	// ����touch�¼�����ʵ���Ǽ�һ�����
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();

			dragSrcPosition = dragPosition = pointToPosition(x, y);
			if (dragPosition == AdapterView.INVALID_POSITION) {
				return super.onInterceptTouchEvent(ev);
			}

			ViewGroup itemView = (ViewGroup) getChildAt(dragPosition
					- getFirstVisiblePosition());
			dragPoint = y - itemView.getTop();
			dragOffset = (int) (ev.getRawY() - y);

			TextView dragger = (TextView) itemView
					.findViewById(R.id.drag_list_item_text);
			if (dragger != null && x > dragger.getLeft() - 20) {
				//
				upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 3);
				downScrollBounce = Math.max(y + scaledTouchSlop,
						getHeight() * 2 / 3);

				// itemView.setDrawingCacheEnabled(true);
				// Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				String text = dragger.getText().toString();
				startDrag(text, y);
			}
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * �����¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (dragTextView != null && dragPosition != INVALID_POSITION) {
			int action = ev.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				stopDrag();
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			default:
				break;
			}
			return true;
		}
		// Ҳ������ѡ�е�Ч��
		return super.onTouchEvent(ev);
	}

	/**
	 * ׼���϶�����ʼ���϶����ͼ��
	 * 
	 * @param bm
	 * @param y
	 */
	public void startDrag(String text, int y) {
		stopDrag();

		windowParams = new WindowManager.LayoutParams();
		windowParams.gravity = Gravity.TOP;
		windowParams.x = 100;
		windowParams.y = y - dragPoint + dragOffset;
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParams.format = PixelFormat.TRANSLUCENT;
		windowParams.windowAnimations = 0;

		TextView textView = new TextView(getContext());
		textView.setText(text);
		;
		windowManager = (WindowManager) getContext().getSystemService("window");
		windowManager.addView(textView, windowParams);
		dragTextView = textView;
	}

	/**
	 * ֹͣ�϶���ȥ���϶����ͷ��
	 */
	public void stopDrag() {
		if (dragTextView != null) {
			windowManager.removeView(dragTextView);
			dragTextView = null;
		}
	}

	/**
	 * �϶�ִ�У���Move������ִ��
	 * 
	 * @param y
	 */
	public void onDrag(int y) {
		if (dragTextView != null) {
			windowParams.alpha = 0.8f;
			windowParams.y = y - dragPoint + dragOffset;
			windowManager.updateViewLayout(dragTextView, windowParams);
		}
		// Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		// ����
		int scrollHeight = 0;
		if (y < upScrollBounce) {
			scrollHeight = 8;// �������Ϲ���8�����أ�����������Ϲ����Ļ�
		} else if (y > downScrollBounce) {
			scrollHeight = -8;// �������¹���8�����أ�������������Ϲ����Ļ�
		}

		if (scrollHeight != 0) {
			// ���������ķ���setSelectionFromTop()
			setSelectionFromTop(dragPosition,
					getChildAt(dragPosition - getFirstVisiblePosition())
							.getTop() + scrollHeight);
		}
	}

	/**
	 * �϶����µ�ʱ��
	 * 
	 * @param y
	 */
	public void onDrop(int y) {

		// Ϊ�˱��⻬�����ָ��ߵ�ʱ�򣬷���-1������
		int tempPosition = pointToPosition(0, y);
		if (tempPosition != INVALID_POSITION) {
			dragPosition = tempPosition;
		}

		// �����߽紦��
		if (y < getChildAt(1).getTop()) {
			// �����ϱ߽�
			dragPosition = 1;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			// �����±߽�
			dragPosition = getAdapter().getCount() - 1;
		}

		// ���ݽ���
		if (dragPosition > 0 && dragPosition < getAdapter().getCount()) {
			DragListAdapter adapter = (DragListAdapter) getAdapter();
			Object dragItem = adapter.getItem(dragSrcPosition);
			adapter.remove(dragItem);
			adapter.insert(dragItem, dragPosition);
			adapter.notifyDataSetChanged();
		}

	}
}
