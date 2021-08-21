package idempotent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

/**
 * 幂等切面 ， 该切面要在分布式锁逻辑之后@see DistributeLock <br>
 * 暂时不支持重入
 * 
 * @author LeoChan
 * @date 2021-03-22
 */
@Order(2)
@Aspect
public class IdempotentMethodAspect {

	@Autowired
	private IdempotentMethodUtil util;
	
	/**
	 * 环绕通知： 注意:Spring AOP的环绕通知会影响到AfterThrowing通知的 运行,不要同时使用
	 *
	 * 环绕通知非常强大，可以决定目标方法是否执行，什么时候执行，执行时是否需要替换方法参数，执行完毕是否需要替换返回值。
	 * 环绕通知第一个参数必须是org.aspectj.lang.ProceedingJoinPoint类型
	 * 
	 * @throws Throwable
	 */
	@Around(value = "@annotation(method)")
	public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint, IdempotentMethod method) throws Throwable {
		return util.operate(proceedingJoinPoint, method);
	}
	
}
