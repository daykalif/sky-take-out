package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private JwtProperties jwtProperties;

	/**
	 * 登录
	 *
	 * @param employeeLoginDTO
	 * @return
	 */
	@PostMapping("/login")
	@ApiOperation(value = "员工登录")   // “value”可不写，可以直接写值
	public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
		log.info("员工登录：{}", employeeLoginDTO);

		Employee employee = employeeService.login(employeeLoginDTO);

		//登录成功后，生成jwt令牌
		Map<String, Object> claims = new HashMap<>();
		claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
		String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

		EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder().id(employee.getId()).userName(employee.getUsername()).name(employee.getName()).token(token).build();

		return Result.success(employeeLoginVO);
	}

	/**
	 * 退出
	 *
	 * @return
	 */
	@PostMapping("/logout")
	@ApiOperation("员工退出")
	public Result<String> logout() {
		return Result.success();
	}


	/**
	 * 新增员工
	 */
	@PostMapping    // 继承父类RequestMapping注解, 所以路径为/admin/employee
	public Result save(@RequestBody EmployeeDTO employeeDTO) {   // @RequestBody注解：表示将请求体中的json数据封装到EmployeeDTO对象中
		log.info("新增员工：{}", employeeDTO);

		System.out.println("当前线程的id2：" + Thread.currentThread().getId());

		employeeService.save(employeeDTO);
		return Result.success();
	}


	/**
	 * 分页查询员工列表
	 */
	@GetMapping("/page")
	@ApiOperation("分页查询员工列表")    // @ApiOperation用于描述接口功能，参数为接口功能描述
	public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
		log.info("分页查询员工列表：{}", employeePageQueryDTO);
		PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
		return Result.success(pageResult);
	}


	/**
	 * 启用禁用员工账号状态
	 *
	 * @param tempStatus
	 * @param id
	 * @return
	 * @PathVariable注解：表示将请求路径中的参数绑定到方法参数上；也可以写成 @PathVariable Integer status
	 */
	@PostMapping("/status/{status}")
	@ApiOperation("启用禁用员工账号状态")
	public Result startOrStop(@PathVariable("status") Integer tempStatus, Long id) {
		log.info("启用禁用员工账号：{},{}", tempStatus, id);
		employeeService.startOrStop(tempStatus, id);
		return Result.success();
	}
}
