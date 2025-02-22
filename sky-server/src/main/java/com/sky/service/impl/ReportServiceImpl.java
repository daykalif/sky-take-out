package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserMapper userMapper;

	/**
	 * 统计指定时间区间内的营业额统计
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	@Override
	public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
		//当前集合用于存放从begin到end范围内的每天的日期
		List<LocalDate> dateList = new ArrayList<>();
		dateList.add(begin);

		while (!begin.equals(end)) {
			//日期计算，计算指定日期的后一天对应的日期
			begin = begin.plusDays(1);
			dateList.add(begin);
		}

		//存放每天的营业额
		List<Double> turnoverList = new ArrayList<>();
		for (LocalDate date : dateList) {
			//查询date日期对应的营业额数据，营业额是指：状态为“已完成”的订单金额合计
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);    // YY:MM::DD 00:00:00
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);      // YY:MM::DD 23:59:59

			// 订单金额合计sql：select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
			Map map = new HashMap();
			map.put("begin", beginTime);
			map.put("end", endTime);
			map.put("status", Orders.COMPLETED);
			Double turnover = orderMapper.sumByMap(map);
			turnover = turnover == null ? 0.0 : turnover;
			turnoverList.add(turnover);
			log.info("营业额：{}", turnover);
		}

		return TurnoverReportVO
				.builder()
				.dateList(
						StringUtils.join(dateList, ",")    // 将日期集合以逗号分隔拼接成字符串
				)
				.turnoverList(
						StringUtils.join(turnoverList, ",")    // 将营业额集合以逗号分隔拼接成字符串
				)
				.build();
	}


	/**
	 * 统计指定时间区间内的用户数据
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	@Override
	public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
		//当前集合用于存放从begin到end范围内的每天的日期
		List<LocalDate> dateList = new ArrayList<>();
		dateList.add(begin);

		while (!begin.equals(end)) {
			//日期计算，计算指定日期的后一天对应的日期
			begin = begin.plusDays(1);
			dateList.add(begin);
		}

		// 存放每天的新增用户数（根据用户注册时间统计每天的用户数量sql：select count(id) from user where create_time < ? and create_time > ?）
		List<Integer> newUserList = new ArrayList<>();
		// 存放每天的总用户数（select count(id) from user where create_time < ?）
		List<Integer> totalUserList = new ArrayList<>();

		for (LocalDate date : dateList) {
			LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

			Map map = new HashMap();
			map.put("end", endTime);

			//总用户数量
			Integer totalUser = userMapper.countByMap(map);

			map.put("begin", beginTime);
			//新增用户数量
			Integer newUser = userMapper.countByMap(map);

			totalUserList.add(totalUser);
			newUserList.add(newUser);
		}

		return UserReportVO
				.builder()
				.dateList(StringUtils.join(dateList, ","))
				.totalUserList(StringUtils.join(totalUserList, ","))
				.newUserList(StringUtils.join(newUserList, ","))
				.build();
	}


	/**
	 * 统计指定时间区间内的订单数据
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	@Override
	public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
		//当前集合用于存放从begin到end范围内的每天的日期
		List<LocalDate> dateList = new ArrayList<>();
		dateList.add(begin);

		while (!begin.equals(end)) {
			//日期计算，计算指定日期的后一天对应的日期
			begin = begin.plusDays(1);
			dateList.add(begin);
		}

		// 存放每天的订单总数
		List<Integer> orderCountList = new ArrayList<>();
		// 存放每天的有效订单数
		List<Integer> validOrderCountList = new ArrayList<>();

		//	遍历dataList集合，查询每天的有效订单数和订单总数
		for (LocalDate date : dateList) {
			// 查询每天订单总数sql: select count(id) from orders where order_time > ? and order_time < ?
			LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.MIN);
			LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
			Integer orderCount = getOrderCount(localDateTime, endTime, null);

			// 查询每天有效订单数sql: select count(id) from orders where order_time > ? and order_time < ? and status = 5
			Integer validOrderCount = getOrderCount(localDateTime, endTime, Orders.COMPLETED);

			orderCountList.add(orderCount);
			validOrderCountList.add(validOrderCount);
		}

		//计算时间区间内的订单总数量
		Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

		//计算时间区间内的有效订单数量
		Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

		//计算订单完成率
		Double orderCompletionRate = 0.0;
		if (totalOrderCount != 0) {
			orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
		}

		return OrderReportVO.builder()
				.dateList(StringUtils.join(dateList, ","))
				.orderCountList(StringUtils.join(orderCountList, ","))
				.validOrderCountList(StringUtils.join(validOrderCountList, ","))
				.totalOrderCount(totalOrderCount)
				.validOrderCount(validOrderCount)
				.orderCompletionRate(orderCompletionRate)
				.build();
	}

	/**
	 * 根据条件查询订单数量
	 *
	 * @param begin
	 * @param end
	 * @param status
	 * @return
	 */
	private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
		Map map = new HashMap();
		map.put("begin", begin);
		map.put("end", end);
		map.put("status", status);

		return orderMapper.countByMap(map);
	}
}
