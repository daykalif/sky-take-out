package com.sky.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest    // 表示当前类是一个Spring Boot测试类，会被Spring IOC容器扫描到并管理
public class SpringDataRedisTest {
	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void testRedisTemplate() {
		System.out.println("redisTemplate = " + redisTemplate);
		ValueOperations valueOperations = redisTemplate.opsForValue();
		HashOperations hashOperations = redisTemplate.opsForHash();
		ListOperations listOperations = redisTemplate.opsForList();
		SetOperations setOperations = redisTemplate.opsForSet();
		ZSetOperations zSetOperations = redisTemplate.opsForZSet();
	}

	/*
	 * 操作字符串类型的数据
	 *
	 * 常用操作命令：set get setex setnx
	 *
	 * Spring Data Redis提供的接口，底层调用了Jedis的set get setex setnx等命令
	 */
	@Test
	public void testString() {
		// 存储数据
		redisTemplate.opsForValue().set("city", "北京");

		// 获取数据
		String city = (String) redisTemplate.opsForValue().get("city");
		System.out.println(city);

		// 设置过期时间
		redisTemplate.opsForValue().set("code", "1234", 3, TimeUnit.MINUTES);

		// 设置key不存在时才设置
		redisTemplate.opsForValue().setIfAbsent("lock", "1");
		redisTemplate.opsForValue().setIfAbsent("lock", "2");

		System.out.println("=========================================");
	}


	/*
	 * 操作Hash类型的数据
	 *
	 * 常用操作命令：hset hget hdel hkeys hvals
	 */
	@Test
	public void testHash() {
		HashOperations hashOperations = redisTemplate.opsForHash();

		hashOperations.put("User", "name", "张三");
		hashOperations.put("User", "age", "18");

		String name = (String) hashOperations.get("User", "name");
		System.out.println(name);

		String age = (String) hashOperations.get("User", "age");
		System.out.println(age);

		Set keys = hashOperations.keys("User");
		System.out.println(keys);

		List values = hashOperations.values("User");
		System.out.println(values);

		hashOperations.delete("User", "name", "age");

		System.out.println("=========================================");
	}


	/*
	 * 操作列表类型的数据
	 *
	 * 常用操作命令：lpush lrange rpop llen
	 */
	@Test
	public void testList() {
		ListOperations listOperations = redisTemplate.opsForList();

		listOperations.leftPushAll("mylist", "a", "b", "c");
		listOperations.leftPush("mylist", "d");

		List list = listOperations.range("mylist", 0, -1);
		System.out.println(list);

		listOperations.rightPop("mylist");

		Long size = listOperations.size("mylist");
		System.out.println(size);
	}


	/*
	 * 操作集合类型的数据
	 *
	 * 常用操作命令：sadd smembers scard sinter sunion srem
	 */
	@Test
	public void testSet() {
		SetOperations setOperations = redisTemplate.opsForSet();
		setOperations.add("set1", "a", "b", "c", "d");
		setOperations.add("set2", "a", "b", "x", "y");

		Set members = setOperations.members("set1");
		System.out.println(members);

		Long size = setOperations.size("set1");
		System.out.println(size);

		Set intersection = setOperations.intersect("set1", "set2");
		System.out.println(intersection);

		Set union = setOperations.union("set1", "set2");
		System.out.println(union);

		setOperations.remove("set1", "a", "b");
	}


	/*
	 * 操作有序集合类型的数据
	 *
	 * 常用操作命令：zadd zrange zincrby zrem
	 */
	@Test
	public void testZSet() {
		ZSetOperations zSetOperations = redisTemplate.opsForZSet();
		zSetOperations.add("zset", "a", 10);
		zSetOperations.add("zset", "b", 12);
		zSetOperations.add("zset", "c", 9);

		Set zset = zSetOperations.range("zset", 0, -1);
		System.out.println(zset);

		zSetOperations.incrementScore("zset", "c", 10);

		zSetOperations.remove("zset", "a", "b");
	}


	/*
	 * 通用命令操作
	 *
	 * keys exists type del
	 */
	@Test
	public void testCommon() {
		Set keys = redisTemplate.keys("*");
		System.out.println(keys);

		Boolean city = redisTemplate.hasKey("city");
		Boolean set1 = redisTemplate.hasKey("set1");

		for (Object key : keys) {
			DataType type = redisTemplate.type(key);
			System.out.println(type.name());
		}

		redisTemplate.delete("mylist");
	}
}
