package com.joe.frame.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.joe.frame.core.dto.ChildYejiDTO;

/**
 * 对map和list进行排序
 * @author lpx
 *
 * 2017年7月25日
 */
public class SortCollection {

	/**
	 * 打印输出
	 * @param list 数据集合
	 */
	public static void printList(List<ChildYejiDTO> list)
	{
		System.out.println("sss");
		for (ChildYejiDTO test : list)
			System.out.println(test.getNickName()+"/// "+test.getDayMoney() +"///" +test.getMonthMoney());
	}
	
	public static void main(String[] args) {

		//按照map中某个值进行排序
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 2);
		map.put("c", 3);
		map.put("c", 5);
		map.put("d", 4);
		List<Entry<String,Integer>> list =
				new ArrayList<Entry<String,Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});

		for(Entry<String,Integer> ii :list){
			System.out.println(ii);
		}
	
	
		//对list进行排序，按照对象中的某个值
		ArrayList<ChildYejiDTO> newList = new ArrayList<ChildYejiDTO>();
		for(int i=0;i<5;i++){
			ChildYejiDTO dto = new ChildYejiDTO();
			dto.setDayMoney(100+i);
			dto.setMonthMoney(100+i);
			dto.setNickName("aaa"+i);
			newList.add(dto);
		}
		//排序
		printList(newList);
		/*	Collections.sort(newList, new Comparator<ChildYejiDTO>() {
			public int compare(ChildYejiDTO o1, ChildYejiDTO o2) {
				return (int) (o2.getDayMoney() - o1.getDayMoney());
			}
		});
		 */
		//jdk 1.8特性 lambda表达式
		Collections.sort(newList, (o1, o2) -> {
			return (int) (o2.getDayMoney() - o1.getDayMoney());
		});

		printList(newList);
	}

	
	
	
}
