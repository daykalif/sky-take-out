package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;

	/**
	 * 员工登录
	 *
	 * @param employeeLoginDTO
	 * @return
	 */
	public Employee login(EmployeeLoginDTO employeeLoginDTO) {
		String username = employeeLoginDTO.getUsername();
		String password = employeeLoginDTO.getPassword();

		//1、根据用户名查询数据库中的数据
		Employee employee = employeeMapper.getByUsername(username);

		//2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
		if (employee == null) {
			//账号不存在
			throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
		}

		//密码比对
		password = DigestUtils.md5DigestAsHex(password.getBytes());    //对前端传过来的明文密码进行md5加密后和数据库中的密文密码进行比对
		if (!password.equals(employee.getPassword())) {
			//密码错误
			throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
		}

		if (employee.getStatus() == StatusConstant.DISABLE) {
			//账号被锁定
			throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
		}

		//3、返回实体对象
		return employee;
	}

	/*
	 * 新增员工
	 *
	 * 调用持久层mapper，将员工信息保存到数据库中；
	 *
	 * 传入的时候是个DTO，方便我们封装前端提交过来的数据；
	 * 传入持久层的时候建议存入实体对象，方便后续的持久化操作；
	 * 因此我们需要将DTO对象转化为实体对象；
	 * Employee实体对象的属性要比DTO对象多一些，因此需要我们手动封装；
	 */
	@Override
	public void save(EmployeeDTO employeeDTO) {
		System.out.println("当前线程的id3：" + Thread.currentThread().getId());

		Employee employee = new Employee();
		//	对象属性拷贝，一次性将DTO对象中的属性值拷贝到实体对象中；
		BeanUtils.copyProperties(employeeDTO, employee);    // 一次性属性拷贝，需要保持属性名一致；

		//	设置账号的状态，默认正常状态
		employee.setStatus(StatusConstant.ENABLE);

		//	设置密码，默认密码为123456; 需要进行md5加密
		employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

		//	设置当前记录的创建时间和修改时间
		employee.setCreateTime(LocalDateTime.now());
		employee.setUpdateTime(LocalDateTime.now());

		// 设置当前记录创建人id和修改人id
		employee.setCreateUser(BaseContext.getCurrentId());
		employee.setUpdateUser(BaseContext.getCurrentId());

		//	调用持久层mapper，将员工信息保存到数据库中
		employeeMapper.insert(employee);
	}


	/**
	 * 分页查询员工信息
	 *
	 * @param employeePageQueryDTO
	 * @return PageHelper.startPage底层是通过拦截器实现的，拦截器会拦截到sql语句，类似于mybatis的动态sql，动态拼接limit，并动态计算页码和每页记录数；
	 */
	@Override
	public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
		// select * from employee limit 0,10
		// 开始分页查询，PageHelper的底层也是通过ThreadLocal来实现的
		PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

		// 调用持久层mapper，查询分页数据
		Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

		long total = page.getTotal();
		List<Employee> records = page.getResult();
		return new PageResult(total, records);    // 调用PageResult的有参构造方法
	}


	/**
	 * 启用禁用员工账号状态
	 *
	 * @param tempStatus
	 * @param id
	 */
	@Override
	public void startOrStop(Integer tempStatus, Long id) {
		// update employee set status = ? where id = ?

		// 方式一：创建实体对象
		//Employee employee = new Employee();
		//employee.setStatus(tempStatus);
		//employee.setId(id);

		// 方式二：使用builder模式
		Employee employee = Employee.builder()
				.status(tempStatus)
				.id(id)
				.build();

		employeeMapper.update(employee);
	}
}
