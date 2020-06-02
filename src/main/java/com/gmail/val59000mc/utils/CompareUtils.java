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

	public static boolean validateName(String name){
		boolean valid = name != null
				&& !name.isEmpty()
				&& name.length() <= 16
				&& !name.contains(" ");

		if (valid){
			name = name.replaceAll("[_a-zA-Z0-9]", "");
			valid = name.isEmpty();
		}

		return valid;
	}

}