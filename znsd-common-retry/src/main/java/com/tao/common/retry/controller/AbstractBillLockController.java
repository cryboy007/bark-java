package com.tao.common.retry.controller;

import com.tao.common.core.common.exception.BizException;
import com.tao.common.core.common.message.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import com.tao.common.retry.NeedRetryException;

/**
 * 单据操作前置网控锁
 * 
 * @author jun.chen
 *
 */
@Slf4j
public abstract class AbstractBillLockController {

	@Autowired
	private JdbcTemplate template;

	public static final String UPDATE_SQL_LOCK = "update %s set processing = '1' "
			+ "where (processing = '0' or processing is null) and id = ?";
	public static final String UPDATE_SQL_UNLOCK = "update %s set processing = '0' where processing = '1' and id = ?";

	//凡是使用该乐观锁,都需实现getTableName
	public abstract String getTableName();

	private void operateSQL(String billId, String updateSql) {
		String sql = String.format(updateSql, getTableName());
		try {
			int update = template.update(sql, billId);
			if (update != 1) {
				throw new BizException("lock.0001", "当前锁定中！请稍后再试");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			String errorMsg = e.getMessage().substring(e.getMessage().lastIndexOf(":") + 1);
			throw new BizException("lock.0000", errorMsg);
		}

	}

	public interface IOperateHelper<T> {
		Result<T> operate();
	}

	/**
	 * 单据锁定操作，默认操作成功后解锁
	 * 
	 * @param <T>
	 * @param billId
	 * @param helper
	 * @return
	 */
	public <T> Result<T> opreate(String billId, IOperateHelper<T> helper) {
		return opreate(billId, true, helper);
	}

	/**
	 * 单据锁定操作，操作成功后指定是否解锁
	 * 
	 * @param <T>
	 * @param billId          单据主键
	 * @param unlockOnSuccess 是否在操作成功后默认解锁
	 * @param helper          原方法调用
	 * @return
	 */
	public <T> Result<T> opreate(String billId, boolean unlockOnSuccess, IOperateHelper<T> helper) {
		Result<T> operate = Result.success();
		operateSQL(billId, UPDATE_SQL_LOCK);
		try {
			operate = helper.operate();
			if (unlockOnSuccess) {
				// 成功操作解锁
				operateSQL(billId, UPDATE_SQL_UNLOCK);
			}
		} catch (Exception e) {
			// 不是重试异常，需要释放锁。
			if (!(e instanceof NeedRetryException)) {
				operateSQL(billId, UPDATE_SQL_UNLOCK);
			}
			throw e;
		}

		return operate;
	}
}
