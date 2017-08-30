package com.joe.frame.web.api;

import java.util.Deque;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import com.joe.frame.web.bean.InterfaceInfo;
import com.joe.frame.web.dto.NormalDTO;
import com.joe.frame.web.service.InterfaceService;

@Path("system")
public class SystemResource {
	@Autowired
	private InterfaceService service;

	/**
	 * 获取当前正在处理的请求总数
	 * 
	 * @return
	 */
	@GET
	@Path("active/all")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Long> getAllActive() {
		NormalDTO<Long> dto = new NormalDTO<Long>();
		dto.setData(service.getAllActive());
		return dto;
	}

	/**
	 * 获取当前正在处理的各个接口的请求数量
	 * 
	 * @return
	 * 
	 */
	@GET
	@Path("active/interface")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<TreeMap<String, AtomicInteger>> getActiveInterface() {
		NormalDTO<TreeMap<String, AtomicInteger>> dto = new NormalDTO<TreeMap<String, AtomicInteger>>();
		dto.setData(service.getActiveInterface());
		return dto;
	}

	/**
	 * 获取各个接口的请求历史记录
	 * 
	 * @return
	 */
	@GET
	@Path("history")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<TreeMap<String, Deque<InterfaceInfo>>> getHistory() {
		NormalDTO<TreeMap<String, Deque<InterfaceInfo>>> dto = new NormalDTO<TreeMap<String, Deque<InterfaceInfo>>>();
		dto.setData(service.getHistory());
		return dto;
	}
	
	/**
	 * 获取所有接口的历史请求数量和
	 * @return
	 */
	@GET
	@Path("history/interface/allcount")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Long> getAllHistoryCount(){
		NormalDTO<Long> dto = new NormalDTO<Long>();
		dto.setData(service.getAllHistoryCount());
		return dto;
	}
	
	/**
	 * 获取各个接口的请求数量
	 * @return
	 */
	@GET
	@Path("history/interface/count")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<TreeMap<String, AtomicLong>> getHistoryCount(){
		NormalDTO<TreeMap<String, AtomicLong>> dto = new NormalDTO<TreeMap<String, AtomicLong>>();
		dto.setData(service.getHistoryCount());
		return dto;
	}
	
	/**
	 * 获取最近几次请求的消耗时间
	 * @return
	 */
	@GET
	@Path("time/all")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Queue<Integer>> getAllConsumeTime(){
		NormalDTO<Queue<Integer>> dto = new NormalDTO<Queue<Integer>>();
		dto.setData(service.getAllConsumeTime());
		return dto;
	}
	
	/**
	 * 获取最近几次请求的平均消耗时间
	 * @return
	 */
	@GET
	@Path("time/average")
	@Produces(MediaType.APPLICATION_JSON)
	public NormalDTO<Integer> getAverageTime(){
		NormalDTO<Integer> dto = new NormalDTO<Integer>();
		dto.setData(service.getAverageTime());
		return dto;
	}
	
	
}
