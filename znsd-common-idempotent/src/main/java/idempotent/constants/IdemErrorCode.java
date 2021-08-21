package idempotent.constants;

import com.tao.common.core.common.exception.ICode;
import com.tao.common.core.utils.StringUtil;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum IdemErrorCode implements ICode {
	/***/
	IDEM_ANNOTATION_INVALID("idem.0000", "注解参数异常！{0}"),
	/***/
	IDEM_METHOD_INVALID("idem.0001", "注解不支持当前方法！{0}"),
	/***/
	IDEM_EXPRESS_INVALID("idem.0002", "表达式解析异常！{0}"),
	/***/
	IDEM_LOG_ERROR("idem.0003", "幂等数据插入异常！{0}"),
	/***/
	IDEM_DATA_ERROR("idem.0004", "幂等数据转换异常！{0}"),
	;

	private String code;
	private String message;

	IdemErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getMessage(String... args) {
		return StringUtil.format(message, Arrays.asList(args).toArray());
	}
}
