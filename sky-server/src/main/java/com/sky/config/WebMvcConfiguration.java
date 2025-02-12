package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

	@Autowired
	private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

	@Autowired
	private JwtTokenUserInterceptor jwtTokenUserInterceptor;

	/**
	 * 注册自定义拦截器
	 *
	 * @param registry
	 */
	protected void addInterceptors(InterceptorRegistry registry) {
		log.info("开始注册自定义拦截器...");

		// 校验所有管理端接口，除了登录接口
		registry.addInterceptor(jwtTokenAdminInterceptor)
				.addPathPatterns("/admin/**")
				.excludePathPatterns("/admin/employee/login");    // 不作校验：管理员登录

		// 校验所有用户端接口，除了登录接口和获取店铺营业状态
		registry.addInterceptor(jwtTokenUserInterceptor)
				.addPathPatterns("/user/**")
				.excludePathPatterns("/user/user/login")    // 不作校验：用户登录
				.excludePathPatterns("/user/shop/status");    // 不作校验：获取店铺营业状态
	}

	/**
	 * 通过knife4j生成接口文档
	 * 扫描管理端接口，可在管理端访问http://localhost:8080/doc.html查看
	 *
	 * @return
	 */
	@Bean
	public Docket adminDocket() {
		log.info("开始生成接口文档...");
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("苍穹外卖项目接口文档")
				.version("2.0")
				.description("苍穹外卖项目接口文档")
				.build();
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.groupName("管理端接口")
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.sky.controller.admin"))    // 注意这里，不要忘记写包名
				.paths(PathSelectors.any())
				.build();
		return docket;
	}


	/**
	 * 通过knife4j生成接口文档
	 * 扫描用户端接口，可在用户端访问http://localhost:8080/doc.html查看
	 *
	 * @return
	 */
	@Bean
	public Docket userDocket() {
		log.info("开始生成接口文档...");
		ApiInfo apiInfo = new ApiInfoBuilder()
				.title("苍穹外卖项目接口文档")
				.version("2.0")
				.description("苍穹外卖项目接口文档")
				.build();
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.groupName("用户端接口")
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.sky.controller.user"))    // 注意这里，不要忘记写包名
				.paths(PathSelectors.any())
				.build();
		return docket;
	}

	/**
	 * 设置静态资源映射
	 *
	 * @param registry 如果没有静态资源映射，访问接口会报404；
	 *                 访问http://localhost:8080/doc.html会被当成请求api处理了，报404，所以需要设置静态资源映射
	 */
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("开始设置静态资源映射...");
		registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	/**
	 * 扩展SpringMVC的消息转换器
	 * 作用：对后端返回给前端的数据统一进行转换，比如统一转换日期格式，统一返回json格式等
	 *
	 * @param converters
	 */
	@Override
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		log.info("扩展消息转换器...");
		//创建消息转换器对象
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		//需要为消息转换器设置一个对象转换器，对象转换器底层使用Jackson将Java对象序列化为json数据
		messageConverter.setObjectMapper(new JacksonObjectMapper());
		//将上面的消息转换器对象追加到mvc框架的转换器集合中，0表示优先级，数字越小优先级越高
		converters.add(0, messageConverter);
	}
}
