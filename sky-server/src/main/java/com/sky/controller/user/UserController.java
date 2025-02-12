package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
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

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
@Slf4j
public class UserController {
	@Autowired
	public UserService userService;

	@Autowired
	private JwtProperties jwtProperties;

	@PostMapping("/login")
	@ApiOperation("微信登录")
	public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {    // UserLoginDTO表示微信端传递过来的参数，封装了code；		UserLoginVO表示返回给前端的参数，封装了id、openid、token；
		log.info("微信用户登录：{}", userLoginDTO.getCode());

		// 微信登录
		User user = userService.wxLogin(userLoginDTO);

		// 为微信用户生成jwt令牌
		Map<String, Object> claims = new HashMap<>();
		claims.put(JwtClaimsConstant.USER_ID, user.getId());    // 将用户id作为jwt令牌中的自定义数据
		String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

		// 封装返回数据
		UserLoginVO userLoginVO = UserLoginVO.builder()
				.id(user.getId())
				.openid(user.getOpenid())
				.token(token)
				.build();

		return Result.success(userLoginVO);
	}
}
