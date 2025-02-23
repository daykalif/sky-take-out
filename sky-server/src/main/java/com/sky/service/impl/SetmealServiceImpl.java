package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

	@Autowired
	private SetmealMapper setmealMapper;
	@Autowired
	private SetmealDishMapper setmealDishMapper;
	@Autowired
	private DishMapper dishMapper;

	/**
	 * 新增套餐，同时需要保存套餐和菜品的关联关系
	 *
	 * @param setmealDTO
	 */
	@Transactional
	public void saveWithDish(SetmealDTO setmealDTO) {
		Setmeal setmeal = new Setmeal();
		BeanUtils.copyProperties(setmealDTO, setmeal);

		//向套餐表插入数据
		setmealMapper.insert(setmeal);

		//获取生成的套餐id
		Long setmealId = setmeal.getId();

		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		setmealDishes.forEach(setmealDish -> {
			setmealDish.setSetmealId(setmealId);
		});

		//保存套餐和菜品的关联关系
		setmealDishMapper.insertBatch(setmealDishes);
	}


	/**
	 * 条件查询
	 *
	 * @param setmeal
	 * @return
	 */
	public List<Setmeal> list(Setmeal setmeal) {
		List<Setmeal> list = setmealMapper.list(setmeal);
		return list;
	}

	/**
	 * 根据id查询菜品选项
	 *
	 * @param id
	 * @return
	 */
	public List<DishItemVO> getDishItemById(Long id) {
		return setmealMapper.getDishItemBySetmealId(id);
	}


	/**
	 * 套餐起售、停售
	 *
	 * @param status
	 * @param id
	 */
	public void startOrStop(Integer status, Long id) {
		//起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
		if (status == StatusConstant.ENABLE) {
			//select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
			List<Dish> dishList = dishMapper.getBySetmealId(id);
			if (dishList != null && dishList.size() > 0) {
				dishList.forEach(dish -> {
					if (StatusConstant.DISABLE == dish.getStatus()) {
						throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
					}
				});
			}
		}

		Setmeal setmeal = Setmeal.builder()
				.id(id)
				.status(status)
				.build();
		setmealMapper.update(setmeal);
	}

}