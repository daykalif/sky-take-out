package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
	@Autowired
	private OrderMapper orderMapper;

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

		return TurnoverReportVO
				.builder()
				.dateList(
						StringUtils.join(dateList, ",")    // 将日期集合以逗号分隔拼接成字符串
				)
				.build();
	}
}
