package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderDetailMapper orderDetailMapper;

	@Autowired
	private AddressBookMapper addressBookMapper;

	@Autowired
	private ShoppingCartMapper shoppingCartMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private WeChatPayUtil weChatPayUtil;

	/**
	 * 用户下单
	 *
	 * @param ordersSubmitDTO
	 * @return
	 */
	@Override
	public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
		// 1.异常情况的处理（收货地址为空、购物车为空）
		AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
		if (addressBook == null) {
			// 抛出业务异常
			throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
		}

		// 查询当前用户购物车数据
		Long userId = BaseContext.getCurrentId();
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setUserId(userId);
		List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
		if (shoppingCartList == null || shoppingCartList.size() == 0) {
			// 抛出业务异常
			throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
		}


		// 2.向订单表插入1条数据
		Orders orders = new Orders();    // 构造订单对象
		BeanUtils.copyProperties(ordersSubmitDTO, orders);    // 拷贝属性，将ordersSubmitDTO中的属性拷贝到orders中
		orders.setOrderTime(LocalDateTime.now());    // 设置下单时间
		orders.setPayStatus(Orders.UN_PAID);    // 设置支付状态为未支付
		orders.setStatus(Orders.PENDING_PAYMENT);    // 设置订单状态为待付款
		orders.setNumber(String.valueOf(System.currentTimeMillis()));    // 设置订单号
		orders.setPhone(addressBook.getPhone());    // 设置手机号
		orders.setConsignee(addressBook.getConsignee());    // 设置收货人
		orders.setUserId(userId);    // 设置用户id

		orderMapper.insert(orders);


		// 3.向订单明细表插入n条数据
		List<OrderDetail> orderDetailList = new ArrayList<>();
		for (ShoppingCart cart : shoppingCartList) {
			OrderDetail orderDetail = new OrderDetail();    // 封装订单明细对象
			BeanUtils.copyProperties(cart, orderDetail);    // 拷贝属性，将cart中的属性拷贝到orderDetail中
			orderDetail.setOrderId(orders.getId());    // 设置订单id，从订单对象中获取（需要在上面订单表插入数据时，xml文件中设置：useGeneratedKeys="true" keyProperty="id"）
			orderDetailList.add(orderDetail);
		}
		orderDetailMapper.insertBatch(orderDetailList);


		// 4.清空当前用户的购物车数据
		shoppingCartMapper.deleteByUserId(userId);

		// 5.封装VO返回结果
		OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
				.id(orders.getId())
				.orderTime(orders.getOrderTime())
				.orderNumber(orders.getNumber())
				.orderAmount(orders.getAmount())
				.build();

		return orderSubmitVO;
	}


	/**
	 * 订单支付
	 *
	 * @param ordersPaymentDTO
	 * @return
	 */
	public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
		// 当前登录用户id
		Long userId = BaseContext.getCurrentId();
		User user = userMapper.getById(userId);

		//调用微信支付接口，生成预支付交易单
		JSONObject jsonObject = weChatPayUtil.pay(
				ordersPaymentDTO.getOrderNumber(), //商户订单号
				new BigDecimal(0.01), //支付金额，单位 元
				"苍穹外卖订单", //商品描述
				user.getOpenid() //微信用户的openid
		);

		if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
			throw new OrderBusinessException("该订单已支付");
		}

		OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
		vo.setPackageStr(jsonObject.getString("package"));

		return vo;
	}

	/**
	 * 支付成功，修改订单状态
	 *
	 * @param outTradeNo
	 */
	public void paySuccess(String outTradeNo) {

		// 根据订单号查询订单
		Orders ordersDB = orderMapper.getByNumber(outTradeNo);

		// 根据订单id更新订单的状态、支付方式、支付状态、结账时间
		Orders orders = Orders.builder()
				.id(ordersDB.getId())
				.status(Orders.TO_BE_CONFIRMED)
				.payStatus(Orders.PAID)
				.checkoutTime(LocalDateTime.now())
				.build();

		orderMapper.update(orders);
	}
}
