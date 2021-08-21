package com.tao.common.core.common.exception;

/**
 * 空异常 因为jdk bug id 4499199 无法在模态窗体中 通过线程捕获未处理异常 所以我们在通信层对服务端异常进行了主动处理 然后抛出空异常
 * 以阻止后续的代码执行 相应的 在UncaughtExceptionHandler中 无视此异常
 * 
 * @author hangwen
 * 
 */
public class EmptyException extends BAPException {

	private static final long serialVersionUID = 6054070922964222864L;

}
