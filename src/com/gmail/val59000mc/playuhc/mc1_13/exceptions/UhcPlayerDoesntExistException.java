package com.gmail.val59000mc.playuhc.mc1_13.exceptions;

public class UhcPlayerDoesntExistException extends UhcException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1159293747235742412L;
	public UhcPlayerDoesntExistException(String name){
		super("Error : Player "+name+" doesn't exist");
	}
}
