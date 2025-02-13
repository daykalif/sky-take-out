package com.itheima.controller;

import com.itheima.entity.User;
import com.itheima.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Autowired
	private UserMapper userMapper;

	@PostMapping
	//@CachePut(cacheNames = "userCache", key = "#user.id")   // 如果使用Spring Cache缓存数据，key的生成：userCache::xxx
	//@CachePut(cacheNames = "userCache", key = "#result.id") // 对象导航
	//@CachePut(cacheNames = "userCache", key = "#p0.id") // 获取第一个参数的id
	@CachePut(cacheNames = "userCache", key = "#a0.id") // 获取第一个参数的id
	public User save(@RequestBody User user) {
		userMapper.insert(user);
		return user;
	}

	@DeleteMapping
	public void deleteById(Long id) {
		userMapper.deleteById(id);
	}

	@DeleteMapping("/delAll")
	public void deleteAll() {
		userMapper.deleteAll();
	}

	@GetMapping
	// key的生成：userCache::xxx，如果缓存中存在，则从缓存中获取，否则通过反射，调用getById方法，从数据库中获取
	@Cacheable(cacheNames = "userCache", key = "#id")
	public User getById(Long id) {
		User user = userMapper.getById(id);
		return user;
	}

}
