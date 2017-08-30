package com.joe.frame.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.joe.utils.DateUtil;
/**
 * 生成订单编号---14位， 17072411155001
 * @author lpx
 *
 * 2017年7月24日
 */
public class OrderNum extends Thread{  

	private static long orderNum = 0l;  
	private static String date ;  

	
	
	public static void main(String[] args) throws InterruptedException {  
		for (int i = 0; i < 1000; i++) { 
			String code = getSix();
			System.out.println(code);
//			Thread.sleep(1000);  
		} 
	}  
	
	
	public static synchronized String getSix() {  
		String str = new SimpleDateFormat("mmss").format(new Date()); 
		Random random = new Random();
		int i = random.nextInt(9) + 1;
		 String numId = "3"+str +i;
		 if(numId.length() !=6 ){
			 numId = getSix();
		 }
		 return numId;
	}
	
	
	/** 
	 * 生成6位数 
	 * @return 
	 */  
	public static synchronized String getSix2() {  
		String str = new SimpleDateFormat("HHmmss").format(new Date());  
		if(date==null||!date.equals(str)){  
			date = str;  
			orderNum  = 0l;  
		}  
		orderNum ++;  
		long orderNo = Long.parseLong((date)) * 1000; 
		orderNo += orderNum;
		String haha = String.valueOf(orderNo);
		return "3"+haha.substring(4)+"";  
	}
	
	
	

	/** 
	 * 生成订单编号 
	 * @return 
	 */  
	public static synchronized String getOrderNo() {  
		String str = new SimpleDateFormat("yyMMddHHmm").format(new Date());  
		if(date==null||!date.equals(str)){  
			date = str;  
			orderNum  = 0l;  
		}  
		orderNum ++;  
		long orderNo = Long.parseLong((date)) * 1000;  
		orderNo += orderNum;;  
		return orderNo+"";  
	}  


	//gh
	public static String getOrders(){
		// 生成订单ID，
		Random random = new Random();
		int i = random.nextInt(900) + 100;
		String ortderNum = DateUtil.getFormatDate("yyyyMMddHHmmss" + i);
		return ortderNum;
	}


	/**
	 * 会重复
	 * 随机生成6位数，以3开头
	 * @return 订单号
	 */
	public static String getOrderNumber(){
		Random random = new Random(System.currentTimeMillis());
		//		int num = random.nextInt (90) +10; 10-99
		int num = random.nextInt (100000); //0-9999
		return "3"+String.valueOf(num);
	}


}  