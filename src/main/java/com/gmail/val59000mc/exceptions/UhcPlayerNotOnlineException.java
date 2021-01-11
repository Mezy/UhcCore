package com.gmail.val59000mc.exceptions;

public class UhcPlayerNotOnlineException extends Exception {

	private static final long serialVersionUID = 1159293747235742412L;

	public UhcPlayerNotOnlineException(String name){
		super("Error : Player "+name+" is not online");
	}

}
