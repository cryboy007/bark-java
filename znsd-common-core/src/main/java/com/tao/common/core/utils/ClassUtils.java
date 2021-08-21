package com.tao.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* @ClassName: ClassUtils 
* @Description: TODO java反射机制帮助类
* @author jun.wang@baisonmail.com 
* @date  2017-11-16 13:28:44 
*
 */
public class ClassUtils {

	private static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
	/**
	 * 
	* @Title: getGetMethod 
	* @Description: TODO 获取get方法
	* @param objectClass
	* @param fieldName 属性
	* @return
	* @throws
	 */
	public static Method getGetMethod(Class<?> objectClass, String fieldName) {
		StringBuffer sb = new StringBuffer();

		sb.append("get");
		// 截取
		sb.append(fieldName.substring(0, 1).toUpperCase());

		sb.append(fieldName.substring(1));

		try {

			return objectClass.getMethod(sb.toString());

		} catch (Exception e) {
//			logger.warn(e.getMessage(),e);
		}

		return null;

	}

	/**
	 * 
	* @Title: getSetMethod 
	* @Description: TODO 获取set方法
	* @param objectClass
	* @param fieldName 属性
	* @return
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static Method getSetMethod(Class<?> objectClass, String fieldName, Class[] parameterTypes) {

		try {

			StringBuffer sb = new StringBuffer();

			sb.append("set");

			sb.append(fieldName.substring(0, 1).toUpperCase());

			sb.append(fieldName.substring(1));

			Method method = objectClass.getMethod(sb.toString(), parameterTypes);

			return method;

		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}

		return null;

	}

	/**
	 * 
	* @Title: invokeSet 
	* @Description: TODO 调用set
	* @param o
	* @param fieldName
	* @param value
	* @throws
	 */
	public static void invokeSet(Object o, String fieldName, Object value) {

		if (o == null) {
			return;
		}

		try {

			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[1];

			Field field = o.getClass().getDeclaredField(fieldName);

			parameterTypes[0] = field.getType();

			Method method = getSetMethod(o.getClass(), fieldName, parameterTypes);

			if (method == null) {
				return;
			}

			if (parameterTypes[0] == Integer.class) {
				if (value instanceof Integer) {
					value = Integer.parseInt(value.toString());
				} else {
					Float f = Float.parseFloat(value.toString());
					value = f.intValue();
				}

			} else if (parameterTypes[0] == Float.class) {
				value = Float.parseFloat(value.toString());
			} else if (parameterTypes[0] == Double.class) {
				value = Double.parseDouble(value.toString());
			} else if (parameterTypes[0] == Boolean.class) {
				value = Boolean.parseBoolean(value.toString());
			} else if(parameterTypes[0] == Long.class) {
				value = Long.parseLong(value.toString());
			} else if(parameterTypes[0] == String.class) {
				value = value.toString();
			} else {
				return;
			}

			method.invoke(o, new Object[] { value });

		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}

	}

	/**
	 * 
	* @Title: invokeGet 
	* @Description: TODO 调用get
	* @param o
	* @param fieldName
	* @return
	* @throws
	 */
	public static Object invokeGet(Object o, String fieldName) {

		if (o == null) {
			return null;
		}

		Method method = getGetMethod(o.getClass(), fieldName);
		if (method == null) {
			return null;
		}

		try {
			return method.invoke(o, new Object[0]);
		} catch (Exception e) {
//			logger.warn(e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * 
	* @Title: getDeclaredFields 
	* @Description: TODO 获取类中所有的属性
	* @param obj
	* @return
	* @throws
	 */
	public static Field[] getDeclaredFields(Object obj) {
		if (obj == null) {
			return null;
		}

		return obj.getClass().getDeclaredFields();
	}
	
	/**
	* @Title: getAllFields 
	* @Description: 获取对象本身及父类中的所有属性
	* @author jun.wang@baisonmail.com
	* @param @param clazz
	* @param @return    设定文件 
	* @return List<Field>    返回类型 
	* @throws
	 */
	public static List<Field> getAllFields(Object obj) {
		List<Field> fieldList = new ArrayList<Field>();
		Field[] field = getDeclaredFields(obj);
		fieldList.addAll(Arrays.asList(field));
		if (obj!= null) {
			getAllFields(obj.getClass(), fieldList);
		}
		return fieldList;
	}
	
	private static void getAllFields(Class<?> clazz, List<Field> fieldList) {
		if(clazz != Object.class) {
			Class<?> clazzs = clazz.getSuperclass();
			Field[] field = clazzs.getDeclaredFields();
			fieldList.addAll(Arrays.asList(field));
			getAllFields(clazzs, fieldList);
		}
	}
	
	/**
	* @Title: validateFieldIsExist 
	* @Description: 验证对象是否存在
	* @author jun.wang@baisonmail.com
	* @param @param fieldName
	* @param @param obj
	* @param @return    设定文件 
	* @return boolean    返回类型 
	* @throws
	 */
	public static boolean validateFieldIsExist(String fieldName, Object obj) {
		// 获取对象中的属性[]
		List<Field> sourceBillField = getAllFields(obj);
		for (Field field : sourceBillField) {
			String sourceFieldName = field.getName();
			if (fieldName.equals(sourceFieldName)) {
				return true;
			}
		}
		return false;
	}

}