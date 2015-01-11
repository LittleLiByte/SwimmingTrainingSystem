package com.example.swimmingtraningsystem.util;

import java.util.Comparator;

import com.example.swimmingtraningsystem.model.Temp;

/**
 * 让temp集合具备比较性
 * 
 * @author LittleByte
 * 
 */
public class MyComparable implements Comparator<Temp> {

	@Override
	public int compare(Temp lhs, Temp rhs) {
		// TODO Auto-generated method stub
		Temp temp1 = lhs;
		Temp temp2 = rhs;
		int num = temp1.getScore().compareTo(temp2.getScore());
		if (num == 0)
			return temp1.getAthleteName().compareTo(temp2.getAthleteName());
		return num;
	}

}
