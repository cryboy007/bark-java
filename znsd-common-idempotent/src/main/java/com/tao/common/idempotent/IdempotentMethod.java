package com.tao.common.idempotent;

import java.lang.annotation.*;


/**
 * 幂等注解是基于数据表及事务实现的，要求与当前需要控制的业务表在同一个事务中
 * 使用流程如下
 *
 * <pre>
 * 一、创建table指定的业务幂等表，该表必须与所控业务同库。DDL参考如下<br>
 * -- 默认建标语句
 * CREATE TABLE `idem_control_order` (
 * `id` bigint(20) NOT NULL,
 * `uk` varchar(120) NOT NULL,
 * PRIMARY KEY (`id`),
 * UNIQUE KEY `unique_uk` (`uk`)
 *)  COMMENT='订单幂等控制默认表';
 *
 * -- 带结果数据存储的简表 需要storeData() 赋值true
 * CREATE TABLE `idem_control_order` (
 * `id` bigint(20) NOT NULL,
 * `uk` varchar(120) NOT NULL,
 * `result_data` varchar(1000),
 * `result_data_type` varchar(128),
 * PRIMARY KEY (`id`),
 * UNIQUE KEY `unique_uk` (`uk`)
 *)  COMMENT='订单幂等控制默认表 - 支持原方法结果返回';
 *
 *二、在service 方法增加注解即可
 *   &nbsp;&nbsp;&nbsp;&nbsp;@IdempotentMethod(table = "idem_control_order", uk = {"#orderReq.getBillNo()"})
 *	public void orderCreate(OrderReq orderReq) {
 *	     //do business
 *	}
 * </pre>
 * @author LeoChan
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface IdempotentMethod {

	/**
	 * 幂等控制表，使用者必须指定，建议按照模块划分 <br>
	 * DDL语句如上
	 * <br>
	 */
	String table();

	/**
	 * uk 幂等据此配置判断是否唯一<br>
	 * <br>
	 * 支持使用SPEL解析，格式: #表达式<br>
	 * 表达式为代码块中能够执行的方法。<br>
	 * 具体示例如下：<br>
	 * demo:uk = {"\"orderCreate\"","#orderReq.getBillNo()",#"abc".substring(0)}
	 * <br>
	 * 对json或对象取属性:#json.getString("key")<br>
	 * 对abc截取: #"abc".substring(0)
	 * 指定普通字符串:\"orderCreate\"
	 */
	String[] uk();
	
	/**
	 * 是否存储成功的返回对象，默认false
	 * 
	 * @return
	 */
	boolean storeData() default false;

}
