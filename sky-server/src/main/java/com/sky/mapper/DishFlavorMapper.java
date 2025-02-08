package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper    // 表示该接口是一个MyBatis的Mapper接口，用于操作数据库中的dish_flavor表
public interface DishFlavorMapper {

	void insertBatch(List<DishFlavor> flavors);
}
