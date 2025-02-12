package com.sky.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.wechat")	// 将配置文件application.yml中的sky.wechat.*前缀的属性值注入到当前类中
@Data
public class WeChatProperties {
	private String appid; //小程序的appid
	private String secret; //小程序的秘钥

	// 微信支付相关
	private String mchid; //商户号
	private String mchSerialNo; //商户API证书的证书序列号
	private String privateKeyFilePath; //商户私钥文件
	private String apiV3Key; //证书解密的密钥
	private String weChatPayCertFilePath; //平台证书
	private String notifyUrl; //支付成功的回调地址
	private String refundNotifyUrl; //退款成功的回调地址
}
