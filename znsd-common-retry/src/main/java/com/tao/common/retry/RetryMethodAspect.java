package com.tao.common.retry;

import com.tao.common.core.utils.ObjectUtil;
import com.tao.common.retry.dto.ArgsDTO;
import com.tao.common.retry.dto.RetryArgsDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.tao.common.retry.stream.RetryOutputService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

@Order(1)
@Slf4j
@Aspect
@Component
public class RetryMethodAspect {

    @Autowired
    private RetryOutputService retryOutputService;

    private static final ThreadLocal<ArgsDTO> threadArgs = new ThreadLocal<>();

    /**
     * 重试方法的参数预存储，防止被方法修改。
     *
     * @param joinPoint
     */
    @Before(value = "@annotation(com.tao.common.retry.NeedRetryMethod)")
    public void doBefore(JoinPoint joinPoint) {
        ArgsDTO argsDTO = new ArgsDTO();
        argsDTO.setData(joinPoint.getArgs());
        Class[] classes = new Class[joinPoint.getArgs().length];
        if (null != joinPoint.getArgs()) {
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                if (null != joinPoint.getArgs()[i]) {
                    classes[i] = joinPoint.getArgs()[i].getClass();
                }
            }
        }
        argsDTO.setClazzes(classes);
        threadArgs.set(ObjectUtil.clone(argsDTO));
    }

    /**
     * 重试异常的处理
     *
     * @param joinPoint
     * @param ex
     */
    @AfterThrowing(value = "@annotation(com.tao.common.retry.NeedRetryMethod)", throwing = "ex")
    public void doAfterThrowing(JoinPoint joinPoint, Exception ex) {
        if (ex instanceof NeedRetryException) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            RetryArgsDTO retryArgsDTO = new RetryArgsDTO();
            retryArgsDTO.setClazz(joinPoint.getTarget().getClass());
            retryArgsDTO.setMethodName(joinPoint.getSignature().getName());
            retryArgsDTO.setArgsClazzes(methodSignature.getParameterTypes());
            retryArgsDTO.setArgs(threadArgs.get());
            NeedRetryMethod needRetryMethod = methodSignature.getMethod().getDeclaredAnnotation(NeedRetryMethod.class);
            retryArgsDTO.setMaxRetryTimes(needRetryMethod.maxRetryTimes());
            retryArgsDTO.setStackTrace(getStackTrace(ex));
            retryArgsDTO.setErrorMsg(ex.getMessage());
            if (StringUtils.hasText(needRetryMethod.billNo())) {
                retryArgsDTO.setBillNo(parseKey(needRetryMethod.billNo(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs()));
            }
            retryArgsDTO.setName(needRetryMethod.name());
            retryOutputService.sendMsg(retryArgsDTO);
        }
        threadArgs.remove();
    }

    /**
     * 重试方法的参数预存储，防止被方法修改。
     *
     * @param joinPoint
     */
    @AfterReturning(value = "@annotation(com.tao.common.retry.NeedRetryMethod)")
    public void doAfterReturning(JoinPoint joinPoint) {
        threadArgs.remove();
    }

    public static String getStackTrace(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return e.getMessage() + "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "Failed to get stack trace.";
        }
    }

    /**
     * 获取key
     * key 定义在注解上，支持SPEL表达式
     *
     * @return
     */
    private String parseKey(String key, Method method, Object[] args) {

		if (StringUtils.isEmpty(key) || null == method) {
			return null;
		}

        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);

        if(null == paraNameArr) {
        	return null;
        }
        
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

}
