package idempotent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baison.e3plus.common.bscore.utils.KeyUtils;
import com.baison.e3plus.common.bscore.utils.ObjectUtil;
import com.baison.e3plus.common.bscore.utils.StringUtil;
import com.baison.e3plus.common.cncore.common.FourTuple;
import com.baison.e3plus.common.cncore.common.exception.BizCode;
import com.baison.e3plus.common.cncore.common.exception.BizException;
import com.baison.e3plus.common.idempotent.constants.IdemErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
public class IdempotentMethodUtil {

	private static final String SQL_TABLE_INSERT = "insert into %s(`id`,`uk`) values (?,?)";
	private static final String SQL_TABLE_INSERT_WITH_DATA = "insert into %s(`id`,`uk`,`result_data`,`result_data_type`) values (?,?,?,?)";
	private static final String SQL_TABLE_CHECK = "select count(id) from %s where uk = ?";
	private static final String SQL_TABLE_QUERY = "select `result_data`,`result_data_type` from %s where uk = ? limit 1";
	private static final String SQL_TABLE_REMOVE = "delete from %s where id = ?";

	@Autowired
	private JdbcTemplate template;

	private ExpressionParser parser = new SpelExpressionParser();

	public Object operate(ProceedingJoinPoint proceedingJoinPoint, IdempotentMethod method) throws Throwable {
		// 判断执行过程中是否有异常
		boolean hasError = false;
		String recordKey = "";
		FourTuple<Boolean, String, Object, String> beforeResult = doBefore(proceedingJoinPoint, method);
		// 表示已经成功转换了上次成功结果
		if (beforeResult.getFrist()) {
			return beforeResult.getThird();
		}
		recordKey = beforeResult.getFour();
		log.info("@Around环绕通知：" + proceedingJoinPoint.getSignature().toString());
		try {
			Object resultData = proceedingJoinPoint.proceed();
			String dataStr = "";
			String typeName = Void.class.getTypeName();
			if (null != resultData) {
				dataStr = JSON.toJSONString(resultData);
				typeName = resultData.getClass().getTypeName();
			}
			String recordKeyAfterSave = insertData(false, beforeResult.getSecond(), method, dataStr, typeName);
			if (StringUtil.isNotEmptyOrNull(recordKeyAfterSave)) {
				recordKey = recordKeyAfterSave;
			}
			return resultData; // 可以加参数

		} catch (Throwable throwable) {
			hasError = true;
			throw throwable;
		} finally {
			// 当存在异常的时候将幂等记录删除
			if (hasError && StringUtil.isNotEmptyOrNull(recordKey)) {
				String removeSQL = String.format(SQL_TABLE_REMOVE, method.table());
				template.update(removeSQL, recordKey);
			}
			log.info("@Around环绕通知执行结束");
		}

	}

	/**
	 * 方法执行前置幂等控制<br>
	 * <br>
	 * 1、检查注解配置合法性<br>
	 * 2、初始化SPEL的ctx对象<br>
	 * 3、组装unique_key<br>
	 * 4、将unique_key插入幂等表<br>
	 * 
	 * @param pjd
	 * @param method
	 */
	private FourTuple<Boolean, String, Object, String> doBefore(JoinPoint pjd, IdempotentMethod method) {
		// 检查注解配置合法性
		String[] uks = method.uk();
		long count = Arrays.asList(uks).stream().filter(a -> StringUtil.isEmptyOrNull(a.trim())).count();
		if (count > 0) {
			throw new BizException(IdemErrorCode.IDEM_ANNOTATION_INVALID, "uk表达式不允许为空！");
		}
		String table = method.table().trim();
		if (StringUtil.isEmptyOrNull(table)) {
			throw new BizException(IdemErrorCode.IDEM_ANNOTATION_INVALID, "table不允许为空！");
		}

		// 初始化SPEL的ctx对象
		EvaluationContext ctx = initContextVariable(pjd);

		// 组装unique_key
		String[] keys = new String[uks.length];
		for (int i = 0; i < uks.length; i++) {
			String ukVal = "";
			String ukExpress = uks[i].trim();
			try {
				ukVal = parser.parseExpression(ukExpress).getValue(ctx, String.class);
			} catch (Exception e) {
			}
			if (StringUtils.isEmpty(ukVal)) {
				throw new BizException(IdemErrorCode.IDEM_EXPRESS_INVALID, String.format("表达式：%s,取值为空！", ukExpress));
			}
			keys[i] = ukVal;
		}
		String unionKey = String.join("#", keys);
		FourTuple<Boolean, String, Object, String> resultTuple = new FourTuple<>(false, unionKey, "", "");
		// 判断数据是否存在
		Integer existsCount = template.queryForObject(String.format(SQL_TABLE_CHECK, table), new Object[] { unionKey },
				Integer.class);
		Object obj = null;
		if (existsCount > 0) {
			if (method.storeData()) {
				Map<String, Object> queryDataMap = template.queryForMap(String.format(SQL_TABLE_QUERY, table),
						new Object[] { unionKey });
				JSONObject dataMap = new JSONObject(queryDataMap);
				String dataType = dataMap.getString("result_data_type");
				try {
					String dataStr = dataMap.getString("result_data");
					if ((!Void.class.getTypeName().equals(dataType)) && StringUtil.isNotEmptyOrNull(dataStr)) {
						obj = JSON.parseObject(dataStr, ObjectUtil.getClass(dataType));
					}
					// 返回成功对象
					resultTuple.setFrist(true);
					resultTuple.setThird(obj);
				} catch (Exception e) {
					throw new BizException(IdemErrorCode.IDEM_DATA_ERROR, e.getMessage());
				}
				return resultTuple;
			} else {
				throw new BizException(BizCode.IDEMPONT_PASS);
			}
		}

		String recordKey = insertData(true, unionKey, method, "", "");
		resultTuple.setFour(recordKey);
		return resultTuple;
	}

	public String insertData(boolean isBefore, String unionKey, IdempotentMethod method, String data, String dataType) {
		// 当前是接口之前且需要存储数据时当前不处理
		if (!((isBefore && !method.storeData()) || (method.storeData() && !isBefore))) {
			return "";
		}
		// 不需要存储返回结果的直接插入
		String table = method.table().trim();
		// 将unique_key插入幂等表
		String insertSQL = String.format(SQL_TABLE_INSERT, table);
		String key = KeyUtils.getSnakeflakeStringKey();
		Object[] args = new String[] { key, unionKey };
		try {
			if (method.storeData()) {
				insertSQL = String.format(SQL_TABLE_INSERT_WITH_DATA, table);
				args = new String[] { key, unionKey, data, dataType };
			}
			template.update(insertSQL, args);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			String errorMsg = e.getMessage().substring(e.getMessage().lastIndexOf(":") + 1);
			throw new BizException(IdemErrorCode.IDEM_LOG_ERROR, errorMsg);
		} finally {
			// 输出执行SQL
			log.debug(String.format("insert sql:%s values:[%s]", insertSQL,
					String.join(",", Arrays.asList(args).toArray(new String[0]))));
		}
		return key;
	}

	/**
	 * 初始化EvaluationContext
	 * 
	 * @param joinPoint
	 * @return
	 */
	private EvaluationContext initContextVariable(JoinPoint joinPoint) {
		EvaluationContext ctx = new StandardEvaluationContext();
		Object[] paramValues = joinPoint.getArgs();
		if (null == paramValues || paramValues.length == 0) {
			throw new BizException(IdemErrorCode.IDEM_METHOD_INVALID, "当前方法没有参数，无法进行幂等控制!");
		}
		String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
		for (int i = 0; i < paramNames.length; i++) {
			ctx.setVariable(paramNames[i], paramValues[i]);
		}
		return ctx;
	}

}
