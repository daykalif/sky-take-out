package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * @ConfigurationProperties：表示当前类是一个配置属性类
 * prefix = "sky.jwt"：表示将当前类中的所有属性与配置文件中以sky.jwt为前缀的属性进行绑定
 * 文件地址：将springboot中配置文件application.yml，将配置项封装为java对象：JwtProperties，并将对象注入到EmployeeController
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

	/**
	 * 管理端员工生成jwt令牌相关配置
	 */
	private String adminSecretKey;
	private long adminTtl;
	private String adminTokenName;

	/**
	 * 用户端微信用户生成jwt令牌相关配置
	 */
	private String userSecretKey;
	private long userTtl;
	private String userTokenName;

}
