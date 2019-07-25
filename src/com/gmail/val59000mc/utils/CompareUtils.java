package com.gmail.val59000mc.utils;

import java.util.Arrays;
import java.util.List;

public class CompareUtils {
	
	public static boolean equalsToAny(Object comparable, Object... compareList){
		return Arrays.asList(compareList).contains(comparable);
	}

	public static boolean stringListContains(List<String> list, String contains){
		if (list == null || contains == null) return false;
		for (String s : list){
			if (s.contains(contains)){
				return true;
			}
		}
		return false;
	}

}