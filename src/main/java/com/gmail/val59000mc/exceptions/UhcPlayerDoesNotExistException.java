package com.gmail.val59000mc.exceptions;

public class UhcPlayerDoesNotExistException extends Exception {

	private static final long serialVersionUID = 1159293747235742412L;

	public UhcPlayerDoesNotExistException(String name){
		super("Player "+name+" doesn't exist");
	}

}
