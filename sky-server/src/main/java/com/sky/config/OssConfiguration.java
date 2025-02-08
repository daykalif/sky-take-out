package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil对象
 */
@Configuration    // 表示当前类是一个配置类，会被Spring IOC容器扫描到并管理
@Slf4j
public class OssConfiguration {
	@Bean    // 表示当前方法的返回值是一个Bean对象，项目启动时，会被Spring IOC容器管理，自动装配
	@ConditionalOnMissingBean    // 表示当前Bean对象是否在Spring IOC容器中存在，如果不存在，则创建该Bean对象；（条件对象，当没有该对象的时候才去创建）
	public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
		log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);

		return new AliOssUtil(aliOssProperties.getEndpoint(),
				aliOssProperties.getAccessKeyId(),
				aliOssProperties.getAccessKeySecret(),
				aliOssProperties.getBucketName());
	}
}
