package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

	@Autowired
	private SetmealService setmealService;

	/**
	 * 新增套餐
	 *
	 * @param setmealDTO
	 * @return
	 */
	@PostMapping
	@ApiOperation("新增套餐")
	@CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")    // 精切清理key: setmealCache::100
	public Result save(@RequestBody SetmealDTO setmealDTO) {
		setmealService.saveWithDish(setmealDTO);
		return Result.success();
	}

	/**
	 * 套餐起售停售
	 *
	 * @param status
	 * @param id
	 * @return
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("套餐起售停售")
	@CacheEvict(cacheNames = "setmealCache", allEntries = true)    // 清理所有key: setmealCache::*
	public Result startOrStop(@PathVariable Integer status, Long id) {
		setmealService.startOrStop(status, id);
		return Result.success();
	}
}