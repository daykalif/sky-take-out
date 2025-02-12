package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
	@Autowired
	private DishMapper dishMapper;

	@Autowired
	private DishFlavorMapper dishFlavorMapper;

	@Autowired
	private SetmealDishMapper setmealDishMapper;

	@Autowired
	private SetmealMapper setmealMapper;

	/**
	 * 新增菜品和对应的口味
	 *
	 * @param dishDTO
	 */
	@Override
	@Transactional    // 开启事务
	public void saveWithFlavor(DishDTO dishDTO) {
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);    // 属性拷贝; dishDTO的属性名需要和dish的属性名一致；

		// 向菜品表插入1条数据
		dishMapper.insert(dish);

		// 获取insert语句生成的主键值；需要在对应Mapper的xml文件中配置useGeneratedKeys="true"和keyProperty="id"
		Long dishId = dish.getId();

		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && flavors.size() > 0) {
			// 向口味表插入n条数据
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishId);
			});
			dishFlavorMapper.insertBatch(flavors);
		}
	}

	/**
	 * 菜品分页查询
	 *
	 * @param dishPageQueryDTO
	 * @return
	 */
	@Override
	public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
		// 开启分页查询
		PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

		Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
		return new PageResult(page.getTotal(), page.getResult());
	}


	/**
	 * 批量删除菜品
	 *
	 * @param ids
	 */
	@Override
	@Transactional    // 开启事务
	public void deleteBatch(List<Long> ids) {
		// 判读当前菜品是否能够删除 -- 是否存在起售中的菜品？？
		for (Long id : ids) {
			Dish dish = dishMapper.getById(id);
			if (dish.getStatus() == StatusConstant.ENABLE) {
				// 当前菜品处于起售中，不能删除
				throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
			}
		}

		// 判读当前菜品是否能够删除 -- 是否被套餐关联了？？
		List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
		if (setmealIds != null && setmealIds.size() > 0) {
			// 当前菜品被套餐关联了，不能删除
			throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
		}

		// 方式一：删除菜品表中的菜品数据【该方式会有多条sql语句】
		/*for (Long id : ids) {
			// 删除菜品表中的菜品数据
			dishMapper.deleteById(id);
			// 删除菜品关联的口味数据
			dishFlavorMapper.deleteByDishId(id);
		}*/


		// 方式二：【该方式只有2条sql语句】
		// 根据菜品id集合批量删除菜品数据
		// sql: delete from dish where id in (?,?,?)
		dishMapper.deleteByIds(ids);

		// 根据菜品id集合批量删除关联的口味数据
		// sql: delete from dish_flavor where dish_id in (?,?,?)
		dishFlavorMapper.deleteByDishIds(ids);
	}


	/**
	 * 根据id查询菜品
	 *
	 * @param id
	 * @return
	 */
	@Override
	public DishVO getByIdWithFlavor(Long id) {
		// 根据id查询菜品数据
		Dish dish = dishMapper.getById(id);

		// 根据菜品id查询口味数据
		List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);

		// 将查询结果封装到dishVO中
		DishVO dishVO = new DishVO();
		BeanUtils.copyProperties(dish, dishVO);    // 属性拷贝，将dish的属性拷贝到dishVO中
		dishVO.setFlavors(dishFlavorList);    // 将口味数据设置到dishVO中

		return dishVO;
	}


	/**
	 * 根据id修改菜品基本信息和对应的口味信息
	 *
	 * @param dishDTO
	 */
	@Override
	public void updateWithFlavor(DishDTO dishDTO) {
		// 修改菜品表基本信息，这里虽然传入dishDTO对象也可以，但是为了语义清晰，还是单独创建一个dish对象
		Dish dish = new Dish();
		BeanUtils.copyProperties(dishDTO, dish);
		dishMapper.update(dish);

		// 删除原有的口味数据
		dishFlavorMapper.deleteByDishId(dishDTO.getId());

		// 重新插入口味数据
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (flavors != null && flavors.size() > 0) {
			// 向口味表插入n条数据
			flavors.forEach(dishFlavor -> {
				dishFlavor.setDishId(dishDTO.getId());
			});
			dishFlavorMapper.insertBatch(flavors);
		}
	}


	/**
	 * 条件查询菜品和口味
	 *
	 * @param dish
	 * @return
	 */
	public List<DishVO> listWithFlavor(Dish dish) {
		List<Dish> dishList = dishMapper.list(dish);

		List<DishVO> dishVOList = new ArrayList<>();

		for (Dish d : dishList) {
			DishVO dishVO = new DishVO();
			BeanUtils.copyProperties(d, dishVO);

			//根据菜品id查询对应的口味
			List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

			dishVO.setFlavors(flavors);
			dishVOList.add(dishVO);
		}

		return dishVOList;
	}
}
