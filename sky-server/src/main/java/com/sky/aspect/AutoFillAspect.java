package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/*
 * 第二步：自定义切面类 AutoFillAspect
 *
 * 实现公共字段自动填充处理逻辑
 */
@Aspect    // 表示当前类是一个切面类
@Component    // 表示当前类是一个组件类，会被Spring IOC容器扫描到并管理
@Slf4j    // 表示当前类使用日志记录功能
public class AutoFillAspect {
	/*
	 * 切入点
	 *
	 * @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)") 表示：对象com.sky.mapper包及其子包下所有方法，并且被AutoFill注解标注的方法 实施拦截
	 */
	@Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
	private void autoFillPointCut() {
		/*
		 * 通知类型包括：前置通知、后置通知、环绕通知、异常通知、最终通知
		 * 切入点表达式：execution(返回值类型 包名.类名.方法名(参数列表))
		 *
		 * 通知方法：
		 * 1、前置通知：在目标方法执行前执行
		 * 2、后置通知：在目标方法执行后执行
		 * 3、环绕通知：在目标方法执行前后执行，并且可以阻止目标方法执行
		 * 4、异常通知：在目标方法执行异常时执行
		 * 5、最终通知：在目标方法执行后执行，无论目标方法执行是否异常，都会执行
		 */
	}


	/**
	 * 前置通知，在通知中进行公共字段的赋值
	 */
	@Before("autoFillPointCut()")
	public void autoFill(JoinPoint joinPoint) {
		log.info("开始进行公共字段自动填充...");
	}
}
