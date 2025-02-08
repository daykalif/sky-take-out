package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
	@Autowired
	public DishService dishService;

	@PostMapping
	@ApiOperation("新增菜品")
	public Result save(@RequestBody DishDTO dishDTO) {
		log.info("新增菜品:{}", dishDTO);
		dishService.saveWithFlavor(dishDTO);
		return null;
	}


	/**
	 * 这里的参数是: ?key1=value1&key2=value2 地址栏拼接的形式，因此不需要加@RequestBody注解
	 */
	@GetMapping("/page")
	@ApiOperation("菜品分页查询")
	public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
		log.info("菜品分页查询：{}", dishPageQueryDTO);
		PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
		return Result.success(pageResult);
	}


	/**
	 * 菜品批量删除
	 *
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	@ApiOperation("菜品批量删除")
	public Result delete(@RequestParam List<Long> ids) {    // @RequestParam：表示将请求参数绑定到方法参数上，参数名必须一致
		log.info("菜品批量删除:{}", ids);
		dishService.deleteBatch(ids);
		return Result.success();
	}


	/**
	 * 根据id查询菜品
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	@ApiOperation("根据id查询菜品")
	public Result<DishVO> getById(@PathVariable Long id) {    // @PathVariable：表示将请求参数绑定到方法参数上，参数名必须一致
		DishVO dishVO = dishService.getByIdWithFlavor(id);
		return Result.success(dishVO);
	}


	/**
	 * 修改菜品
	 *
	 * @param dishDTO
	 * @return
	 */
	@PutMapping
	@ApiOperation("修改菜品")
	public Result update(@RequestBody DishDTO dishDTO) {
		log.info("修改菜品:{}", dishDTO);
		dishService.updateWithFlavor(dishDTO);	// 可以修改菜品，也可以修改菜品口味
		return Result.success();
	}

}
