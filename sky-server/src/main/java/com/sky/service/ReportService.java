package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
	/**
	 * 统计指定时间区间内的营业额统计
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);


	/**
	 * 统计指定时间区间内的用户统计
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	UserReportVO getUserStatistics(LocalDate begin, LocalDate end);


	/**
	 * 统计指定时间区间内的订单统计
	 *
	 * @param begin
	 * @param end
	 * @return
	 */
	OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);
}
