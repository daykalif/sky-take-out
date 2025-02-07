package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		String token = JwtUtil.createJWT(
				jwtProperties.getAdminSecretKey(),
				jwtProperties.getAdminTtl(),
				claims);

		EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
				.id(employee.getId())
				.userName(employee.getUsername())
				.name(employee.getName())
				.token(token)
				.build();

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
		employeeService.save(employeeDTO);
		return Result.success();
	}
}
