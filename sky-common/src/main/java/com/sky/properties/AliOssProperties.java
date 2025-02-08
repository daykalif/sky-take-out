package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云文件上传配置
 * 对应 application.yml 中的 sky.alioss.*
 */
@Component     // 将当前类标记为组件, 让Spring IOC管理
@ConfigurationProperties(prefix = "sky.alioss")    // 将配置文件application.yml中的aliyun.oss.*前缀的属性值注入到当前类中
@Data // 添加getter/setter方法
public class AliOssProperties {

	private String endpoint;
	private String accessKeyId;
	private String accessKeySecret;
	private String bucketName;

}
