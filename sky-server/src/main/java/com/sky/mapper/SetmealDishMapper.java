package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
	/**
	 * 根据菜品id查询套餐id
	 *
	 * @param dishIds
	 * @return
	 */
	// SELECT setmeal_id FROM setmeal_dish WHERE dish_id IN (1,2,3,4,5)
	List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
