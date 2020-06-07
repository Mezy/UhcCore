package com.gmail.val59000mc.utils;

public class TimeUtils{

	public static final long SECOND_TICKS = 20L;

	public static final long SECOND = 1000;
	public static final long MINUTE = SECOND*60;
	public static final long HOUR = MINUTE*60;
	public static final long HOUR_2 = HOUR*2;

	public static String getFormattedTime(long time){
		int h,m;
		h = (int) time / (60 * 60);
		time -= h * (60 * 60);
		m = (int) time / 60;
		time -= m * 60;
		
		if(h == 0){
			if(m == 0){
				return time+"s";
			}else{
				return m+"m "+time+"s";
			}
		}else{
			return h+"h "+m+"m "+time+"s";
		}
	}

}