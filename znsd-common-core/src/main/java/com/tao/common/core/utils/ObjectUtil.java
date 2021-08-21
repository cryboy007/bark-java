package com.tao.common.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tao.common.core.common.exception.ExceptionWapper;

public class ObjectUtil {

	private ObjectUtil() {
	}

	@SuppressWarnings("unchecked")
	public static <T> T clone(Object obj) {
		String para = serializeByObjectMapper(obj);
		return (T) deSrializeByObjectMapper(para, obj.getClass());
	}

	public static int compareValue(Object v1, Object v2) {
		int c = 0;

		if (v1 instanceof Number && v2 instanceof Number) {
			c = DoubleUtil.parse(v1).compareTo(DoubleUtil.parse(v2));
		} else if (v1 instanceof Date && v2 instanceof Date) {
			c = ((Date) v1).compareTo((Date) v2);
		} else {
			c = String.valueOf(v1).compareTo(String.valueOf(v2));
		}

		return c;
	}

	public static <T> T createNewInstance(Class<T> clazz) {
		try {
			T newObject = (T) (clazz.newInstance());
			return newObject;
		} catch (InstantiationException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IllegalAccessException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static <T> T createNewInstance(Class<T> clazz, Class<?>[] paraClazzs, Object... paras) {
		try {
			Constructor<T> cons = clazz.getConstructor(paraClazzs);
			T newObject = cons.newInstance(paras);
			return newObject;
		} catch (SecurityException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (NoSuchMethodException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IllegalArgumentException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (InstantiationException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IllegalAccessException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (InvocationTargetException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static <T> T createNewInstance(String className) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) ObjectUtil.getClass(className);
		return createNewInstance(cls);
	}

	public static <T> T createNewInstance(String className, Class<?>[] paraClazzs, Object... paras) {
		@SuppressWarnings("unchecked")
		Class<T> cls = (Class<T>) ObjectUtil.getClass(className);
		return createNewInstance(cls, paraClazzs, paras);
	}

	public static <T> T deSrializeByObjectMapper(String para, Class<T> clazz) {
		try {
			return JSON.parseObject(para, clazz, new Feature[] { Feature.OrderedField });
		} catch (JSONException e) {
			throw ExceptionWapper.createBapRunTimeException("ObjectUtil.deSrializeByObjectMapper",
					"JSON Parse error!,json:{0},parseClass:{1}", para, clazz.getName());
		}

	}

	public static <T> T deSrializeByObjectMapper(String para, TypeReference<T> typeReference) {
		return JSON.parseObject(para, typeReference);
	}

	public static boolean equals(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}

		if (obj1 == null || obj2 == null) {
			return false;
		}

		if (obj1.getClass().equals(obj2.getClass())) {
			return obj1.equals(obj2);
		}

		if (isNumber(obj1) && isNumber(obj2)) {
			return DoubleUtil.parse(obj1).equals(DoubleUtil.parse(obj2));
		}

		return false;
	}

	/**
	 * 格式化 代码和名称
	 * 
	 * @param code
	 * @param name
	 * @return
	 */
	public static String formatCodeName(String code, String name) {
		if (StringUtil.isEmptyOrNull(code)) {
			code = "";
		}

		if (StringUtil.isEmptyOrNull(name)) {
			name = "";
		}

		if (ObjectUtil.equals(code, name)) {
			return StringUtil.format("[{0}]", code);
		} else {
			return StringUtil.format("[{0}]{1}", code, name);
		}
	}

	public static Class<?> getClass(String className) {
		Class<?> cls = classMaps.get(className);
		if (cls == null) {
			try {
				cls = Class.forName(className);
				classMaps.put(className, cls);
			} catch (ClassNotFoundException e) {
				throw ExceptionWapper.createBapException(e, className);
			}
		}
		return cls;
	}

	public static NumberFormat getDefaultCurrencyFormat() {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(10);
		format.setGroupingUsed(true);
		return format;
	}

	public static NumberFormat getDefaultNumberFormat() {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(10);
		format.setGroupingUsed(false);
		return format;
	}

	public static Field getField(Class<?> clazz, String fieldName) {
		return getField(clazz, fieldName, true);
	}

	public static Field getField(Class<?> clazz, String fieldName, Boolean throwError) {
		try {
			return clazz.getField(fieldName);
		} catch (SecurityException e) {
			if (throwError) {
				throw ExceptionWapper.createBapException(e);
			}
			return null;
		} catch (NoSuchFieldException e) {
			if (throwError) {
				throw ExceptionWapper.createBapException(e);
			}
			return null;
		}

	}

	public static Field getField(String className, String fieldName) {
		Class<?> cls = ObjectUtil.getClass(className);
		return getField(cls, fieldName, true);
	}

	public static Object getFieldValue(Class<?> clazz, Field field, Object obj) {
		try {
			return field.get(obj == null ? clazz : obj);
		} catch (IllegalArgumentException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IllegalAccessException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static Object getFieldValue(Class<?> clazz, String fieldName, Object obj) {
		Field field = getField(clazz, fieldName);
		return getFieldValue(clazz, field, obj);
	}

	public static Object getFieldValue(String className, String fieldName) {
		return getField(className,fieldName);
	}

	public static Object getFieldValue(String className, String fieldName, Object obj) {
		Class<?> cls = getClass(className);
		Field field = getField(cls, fieldName);
		return getFieldValue(cls, field, obj);
	}

	public static Method getMethod(Class<?> clazz, String methodName, Boolean throwError, Class<?>... paraClazzs) {
		try {
			return clazz.getMethod(methodName, paraClazzs);
		} catch (SecurityException e) {
			if (throwError) {
				throw ExceptionWapper.createBapException(e);
			}
			else {
				return null;
			}
		} catch (NoSuchMethodException e) {
			if (throwError) {
				throw ExceptionWapper.createBapException(e);
			}
			else {
				return null;
			}
		}
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paraClazzs) {
		return getMethod(clazz, methodName, true, paraClazzs);
	}

	public static Method getMethod(String className, String methodName, Class<?>... paraClazzs) {
		Class<?> cls = ObjectUtil.getClass(className);
		return getMethod(cls, methodName, true, paraClazzs);
	}

	/**
	 * 获取数字的格式化字符串
	 * 
	 * @param scale
	 *            小数精度
	 * @return
	 */
	public static String getNumberFormat(int scale) {
		return getNumberFormat(scale, true, false, false, null, null);
	}

	/**
	 * 获取数字的格式化字符串
	 * 
	 * @param scale
	 *            小数精度
	 * @param thounsandSplit
	 *            显示千分位
	 * @param percent
	 *            显示百分号
	 * @return
	 */
	public static String getNumberFormat(int scale, boolean thounsandSplit, boolean percent) {
		return getNumberFormat(scale, true, thounsandSplit, percent, null, null);
	}

	/**
	 * 获取数字的格式化字符串
	 * 
	 * @param scale
	 *            小数精度
	 * @param appendZero
	 *            小数位数不足的位数用0补足
	 * @param thounsandSplit
	 *            显示千分位
	 * @param percent
	 *            显示百分号
	 * @param prefix
	 *            前缀
	 * @param suffix
	 *            后缀
	 * @return
	 */
	public static String getNumberFormat(int scale, boolean appendZero, boolean thounsandSplit, boolean percent,
			String prefix, String suffix) {
		StringBuilder format = new StringBuilder();

		if (!StringUtil.isEmptyOrNull(prefix)) {
			format.append(prefix);
		}

		if (thounsandSplit) {
			format.append("###,##");
		}

		format.append("0");

		if (percent) {
			scale = scale - 2;
		}

		if (scale > 0) {
			format.append(".");
			for (int i = 0; i < scale; i++) {
				if (appendZero) {
					format.append("0");
				} else {
					format.append("#");
				}
			}
		}

		if (scale < 0) {
			format.append(".");
		}

		if (percent) {
			format.append("%");
		}

		if (!StringUtil.isEmptyOrNull(suffix)) {
			format.append(suffix);
		}

		return format.toString();
	}

	/**
	 * @param scale
	 * @param allowNagtive
	 *            允许为负数
	 * 
	 *            如果scale>0，保留小数点后scale位数 如果scale<0,不管保留小数点后多少位都可以
	 * 
	 * */
	public static String getNumberPattern(int scale, boolean allowNagtive) {
		StringBuilder reg = new StringBuilder();
		if (allowNagtive) {
			reg.append("-?");
		}

		reg.append("\\d*");

		if (scale > 0) {
			reg.append("(\\.\\d{0,");
			reg.append(scale);
			reg.append("})?");
		}

		if (scale < 0) {// (\\.\\d+)?
			reg.append("(\\.\\d*)?");
		}

		return reg.toString();
	}

	public static Object invokeMethod(Class<?> clazz, String methodName, Class<?>[] paraClazzs, Object obj,
			Object... paras) {
		Method method=null;
		try {
			method = ObjectUtil.getMethod(clazz, methodName, paraClazzs);
			return invokeMethod(method, obj, paras);
		} catch(Exception e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static Object invokeMethod(Method method, Object obj, Object... paras) {
		try {
			return method.invoke(obj, paras);
		} catch (IllegalArgumentException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IllegalAccessException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (InvocationTargetException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static Object invokeMethod(String className, String methodName, Class<?>[] paraClazzs, Object obj,
			Object... paras) {
		Method method=null;
		try {
			method = ObjectUtil.getMethod(className, methodName, paraClazzs);
			return invokeMethod(method, obj, paras);
		} catch(Exception e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

	public static boolean isDate(Object date) {
		return date != null && isDateClass(date.getClass());
	}

	public static boolean isDateClass(Class<?> cls) {
		return Date.class.isAssignableFrom(cls);
	}

	public static boolean isNumber(Object num) {
		return num != null && isNumberClass(num.getClass());
	}

	public static boolean isNumber(String val) {
		char[] chars = val.toCharArray();
		int length = chars.length;
		if (length < 1) {
			return false;
		}
		int i = 0;
		if (length > 1 && chars[0] == '-') {
			i = 1;
		}

		for (; i < length; i++) {
			char ch = chars[i];

			if (!Character.isDigit(ch) && ch != '.') {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumberClass(Class<?> cls) {
		return Number.class.isAssignableFrom(cls);
	}

	public static <T> T json2Bean(JSONObject jsonObject, Class<T> clazz) {
		return JSONObject.toJavaObject(jsonObject, clazz);
	}

	public static String serializeByObjectMapper(Object obj) {
		SerializerFeature[] features = new SerializerFeature[] { SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteMapNullValue };

		return JSON.toJSONString(obj, features);
	}

	public static String serializeByObjectMapper(Object obj, SerializerFeature[] features) {
		return JSON.toJSONString(obj, features);
	}

	private static Map<String, Class<?>> classMaps = new ConcurrentHashMap<String, Class<?>>();
}
