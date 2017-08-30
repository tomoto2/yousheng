package com.joe.frame.core.utils;

public class ParamsUtil {
	
	
	public static boolean paramsIsNul(String params){
		if(params == null || "".equals(params)){
			return true;
		}
		return false;
	}
	
}
