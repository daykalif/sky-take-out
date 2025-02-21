package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * @RestController 是一个组合注解，它是 @Controller 和 @ResponseBody 的结合体。
 * 在 Spring MVC 应用程序中使用该注解，可以将类标记为控制器，并且该控制器中的所有处理方法返回的对象会自动序列化为 JSON、XML 等格式的数据，
 * 直接作为 HTTP 响应体返回给客户端，而不是返回视图名称。
 *
 *
 * @Controller 是一个控制器注解，用于标记一个类是一个控制器类，表示该类中的方法将处理客户端的请求。
 * @ResponseBody 是一个响应体注解，用于标记一个方法返回的结果直接作为 HTTP 响应体返回给客户端，而不需要返回视图名称。
 */
@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "用户端订单相关接口")
@Slf4j
public class OrderController {
	@Autowired
	private OrderService orderService;

	/**
	 * 用户下单
	 *
	 * @param ordersSubmitDTO
	 * @return
	 */
	@PostMapping("/submit")
	@ApiOperation("用户下单")
	public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {    // @RequestBody注解：表示将请求体中的json数据封装到OrdersSubmitDTO对象中，并返回数据类型为OrderSubmitVO
		log.info("用户下单，参数为：{}", ordersSubmitDTO);
		OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
		return Result.success(orderSubmitVO);
	}


	/**
	 * 订单支付
	 *
	 * @param ordersPaymentDTO
	 * @return
	 */
	@PutMapping("/payment")
	@ApiOperation("订单支付")
	public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
		log.info("订单支付：{}", ordersPaymentDTO);
		OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
		log.info("生成预支付交易单：{}", orderPaymentVO);
		return Result.success(orderPaymentVO);
	}


	/**
	 * 客户催单
	 *
	 * @return
	 */
	@GetMapping("/reminder/{id}")
	@ApiOperation("客户催单")
	private Result reminder(@PathVariable("id") Long id) {
		orderService.reminder(id);
		return Result.success();
	}
}
