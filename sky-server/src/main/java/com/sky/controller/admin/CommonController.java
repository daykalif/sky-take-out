package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/*
 * 通用接口
 *
 * @RestController 是一个组合注解，它是 @Controller 和 @ResponseBody 的结合体。
 * 在 Spring MVC 应用程序中使用该注解，可以将类标记为控制器，并且该控制器中的所有处理方法返回的对象会自动序列化为 JSON、XML 等格式的数据，
 * 直接作为 HTTP 响应体返回给客户端，而不是返回视图名称。
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
	@Autowired
	private AliOssUtil aliOssUtil;

	/**
	 * 文件上传
	 *
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	@ApiOperation("文件上传")
	public Result<String> upload(MultipartFile file) {    // MultipartFile 是 SpringMVC 提供的一个接口，用于封装上传的文件信息。 参数名必须与表单中上传文件的 name 属性值一致。
		log.info("文件上传：{}", file);

		try {
			// 获取原始文件名
			String originalFilename = file.getOriginalFilename();
			// 获取文件后缀
			String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
			// 生成新文件名
			String newFileName = UUID.randomUUID().toString() + suffix;
			// 上传文件到OSS
			String filepath = aliOssUtil.upload(file.getBytes(), newFileName);
			return Result.success(filepath);
		} catch (IOException e) {
			log.error("文件上传失败：{}", e);
		}
		return Result.error(MessageConstant.UPLOAD_FAILED);
	}
}
