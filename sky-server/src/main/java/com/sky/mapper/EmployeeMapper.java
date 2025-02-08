package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

	/**
	 * 根据用户名查询员工
	 *
	 * @param username
	 * @return
	 */
	@Select("select * from employee where username = #{username}")
	Employee getByUsername(String username);


	/*
	 * 新增员工
	 *
	 * @param employee
	 *
	 * 如果 实体类属性名 为驼峰，而 数据库中字段名 不是驼峰，
	 * 可以在application.yml中开启驼峰命名
	 */
	@AutoFill(OperationType.INSERT)    // 第三步：添加自动填充注解
	@Select("insert into employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
			"values (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
	void insert(Employee employee);


	/**
	 * 分页查询
	 *
	 * @param employeePageQueryDTO
	 * @return
	 */
	Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


	/**
	 * 根据主键动态修改属性
	 *
	 * @param employee
	 */
	// 第三步：添加自动填充注解
	@AutoFill(value = OperationType.UPDATE)
	void update(Employee employee);


	/**
	 * 根据id查询员工信息
	 *
	 * @param id
	 * @return
	 */
	@Select("select * from employee where id = #{id}")
	Employee getById(Long id);
}
