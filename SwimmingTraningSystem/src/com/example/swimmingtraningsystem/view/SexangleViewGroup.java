package com.example.swimmingtraningsystem.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SexangleViewGroup extends ViewGroup {

	private static int SPACE;// view��view���¼��

	private float Vheight;

	/**
	 * ��������ײ��ļ��
	 */
	private int bottomSpace;

	double leftSpace;

	public SexangleViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int lenght = (int) (getWidth() / 2);// ÿ����VIEW�ĳ���

		// �Ƕ�ת����
		double radian30 = 30 * Math.PI / 180;

		// �������������ӶԽ����γɵ��������εĴ�ֱ�߳���
		Vheight = (float) (lenght / 2 * Math.cos(radian30));
		bottomSpace = (int) (lenght / 2 - Vheight);

		SPACE = bottomSpace * 3;

		int offsetX = lenght * 3 / 4 + SPACE;// X��ÿ��ƫ�Ƶĳ���
		int offsetY = lenght / 2;// Y��ÿ��ƫ�Ƶĳ���

		int rowIndex = 0;// ���±�
		int childCount = 2;// ����
		int tempCount = 2;// �и���
		int startL = 0;
		int startT = 0;

		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (i == childCount) {
				rowIndex++;
				if (tempCount == 2) {
					tempCount = 3;
				} else {
					tempCount = 2;
				}
				childCount += tempCount;
			}

			if (tempCount == 2) {
				startL = i % 2 * offsetX;
				startT = i % 2 * offsetY;

				if (i > 3 && i % 4 == 0) {
					int j = i / 4;
					startL = lenght * 3 / 4 + SPACE;
					startT = offsetY * j;
				}
				if ((i - 6) % 5 == 0 && i > 2) {
					int j = (i - 1) / 5;
					startL = offsetX * (j + 1);
					startT = offsetY * (j + 1);
				}

			} else if (tempCount == 3) {
				if (i <= 2) {
					startL = i % 2 * offsetX;
					startT = i % 2 * offsetY;
				} else {
					startL = (i % 3 + 1) * offsetX;
					startT = (i % 3 + 1) * offsetY;
				}
				if ((i - 4) % 5 == 0 && i >= 4) {
					startL = 0;
					startT = (i % 3 + 1) * offsetY;
				}

				if ((i - 8) % 5 == 0 && i >= 8) {
					int j = (i - 2) / 5;
					startL = offsetX * (j + 1);
					startT = offsetY * (j + 1);
				}

				if ((i - 9) % 5 == 0 && i >= 9) {
					int j = (i - 2) / 5;
					startL = offsetX * (j + 2);
					startT = offsetY * (j + 2);
				}

			}
			child.layout(startL, startT + rowIndex * lenght, startL + lenght,
					startT + lenght + rowIndex * lenght);
		}

	}
}
