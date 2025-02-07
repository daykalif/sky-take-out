package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 捕获业务异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler
	public Result exceptionHandler(BaseException ex) {
		log.error("异常信息：{}", ex.getMessage());
		return Result.error(ex.getMessage());
	}


	/**
	 * 处理SQLIntegrityConstraintViolationException异常
	 *
	 * @param ex
	 * @return
	 */
	@ExceptionHandler    // @ExceptionHandler用于处理异常，参数为要处理的异常类型；
	public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
		// Duplicate entry 'zhangsan' for key 'employee.idx_username'
		String message = ex.getMessage();
		if (message.contains("Duplicate entry")) {
			String[] split = message.split(" ");
			String username = split[2].substring(1, split[2].lastIndexOf("'"));
			return Result.error(username + MessageConstant.ALREADY_EXISTS);
		}
		return Result.error(MessageConstant.UNKNOWN_ERROR);
	}
}
