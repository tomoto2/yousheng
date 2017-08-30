package com.joe.frame.core.utils;
import java.util.UUID;
public class guid {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UUID uuid = UUID.randomUUID();
		System.out.println(".{"+uuid.toString()+"}");
	}
}