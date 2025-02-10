package com.sky.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HttpClientTest {
	/**
	 * 测试通过httpclient发送GET方式的请求（需要提前把sky-server启动起来，这里才能请求成功）
	 */
	@Test
	public void testGet() throws Exception {
		// 1.创建HttpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 2.创建请求对象
		HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");

		// 3.发送请求
		CloseableHttpResponse response = httpClient.execute(httpGet);

		//	4.获取服务端返回的状态码
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("服务端返回的状态码为：" + statusCode);

		// 5.获取服务端返回的数据
		HttpEntity entity = response.getEntity();
		String body = EntityUtils.toString(entity);
		System.out.println("服务端返回的数据为：" + body);

		// 6.关闭资源
		response.close();
		httpClient.close();
	}


	/**
	 * 测试通过httpclient发送POST方式的请求
	 */
	@Test
	public void testPost() throws Exception {
		// 1.创建HttpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 2.创建请求对象
		HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");

		// 构造参数
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", "admin");
		jsonObject.put("password", "123456");

		StringEntity entity = new StringEntity(jsonObject.toString());

		// 指定请求编码方式
		entity.setContentEncoding("utf-8");

		// 指定请求的数据格式
		entity.setContentType("application/json");

		httpPost.setEntity(entity);

		// 3.发送请求
		CloseableHttpResponse response = httpClient.execute(httpPost);

		// 4.解析返回结果
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("服务端返回的状态码为：" + statusCode);

		HttpEntity entity1 = response.getEntity();
		String body = EntityUtils.toString(entity1);
		System.out.println("服务端返回的数据为：" + body);

		// 5.关闭资源
		response.close();
		httpClient.close();
	}
}
