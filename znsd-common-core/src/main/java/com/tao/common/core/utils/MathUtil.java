package com.tao.common.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.util.NumberUtils;

public class MathUtil {

	public static int round(double v) {
		return IntegerUtil.parse(round(v, 0, BigDecimal.ROUND_HALF_UP));
	}

	public static double round(double v, int scale) {
		return round(v, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @see {@link BigDecimalUtil#round(Double, int, int)};
	 */
	public static double round(Double v, int scale, int roundingMode) {
		return BigDecimalUtil.round(v, scale, roundingMode);
	}

	/**
	 * #see {@link BigDecimalUtil#round(Double, int, RoundingMode)}
	 */
	public static double round(Double v, int scale, RoundingMode roundingMode) {
		return BigDecimalUtil.round(v, scale, roundingMode);
	}

	public static Double max(Object... values) {
		BigDecimal max = null;

		for (Object i : values) {
			if (i == null || StringUtil.isEmptyOrNull(i.toString())) {
				continue;
			}

			BigDecimal v = NumberUtils.parseNumber(i.toString(), BigDecimal.class);
			if (max == null || v.compareTo(max) > 0) {
				max = v;
			}
		}

		return max != null ? max.doubleValue() : null;
	}

	public static Double min(Object... values) {
		BigDecimal min = null;

		for (Object i : values) {
			if (i == null || StringUtil.isEmptyOrNull(i.toString())) {
				continue;
			}

			BigDecimal v = NumberUtils.parseNumber(i.toString(), BigDecimal.class);
			if (min == null || v.compareTo(min) < 0) {
				min = v;
			}
		}

		return min != null ? min.doubleValue() : null;
	}

	public static Double sum(Object... values) {
		BigDecimal sum = new BigDecimal(0);

		for (Object i : values) {
			if (i == null || StringUtil.isEmptyOrNull(i.toString())) {
				continue;
			}

			BigDecimal v = NumberUtils.parseNumber(i.toString(), BigDecimal.class);
			sum = sum.add(v);
		}

		return sum.doubleValue();
	}

//	@SuppressWarnings("unchecked")
//	public static <T extends Number> T sum(Class<T> clazz, Object... values) {
//		BigDecimal sum = new BigDecimal(0);
//
//		for (Object i : values) {
//			if (i == null || StringUtil.isEmptyOrNull(i.toString())) {
//				continue;
//			}
//
//			BigDecimal v = NumberUtils.parseNumber(i.toString(), BigDecimal.class);
//			sum = sum.add(v);
//		}
//
//		return (T) NumberUtils.parseNumber(sum.toString(), BigDecimal.class);
//	}

	public static Double avg(Object... values) {
		if (values.length == 0) {
			return 0d;
		}

		Double sum = sum(values);

		return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(values.length), 10, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 两个数相除
	 * 
	 * @param value1
	 *            除数
	 * @param value2
	 *            被除数
	 * @return
	 */
	public static Double divide(Double value1, Double value2, int scale, Integer roundingMode) {
		BigDecimal b1 = BigDecimal.valueOf(value1);
		BigDecimal b2 = BigDecimal.valueOf(value2);
		return b1.divide(b2, scale, roundingMode).doubleValue();
	}

	/**
	 * 两个数相除
	 * 
	 * @param value1
	 *            除数
	 * @param value2
	 *            被除数
	 * @return
	 */
	public static Double divide(Integer value1, Integer value2, int scale, Integer roundingMode) {
		BigDecimal b1 = BigDecimal.valueOf(value1);
		BigDecimal b2 = BigDecimal.valueOf(value2);
		return b1.divide(b2, scale, roundingMode).doubleValue();
	}

	/**
	 * 两个数相除
	 * 
	 * @param value1
	 *            除数
	 * @param value2
	 *            被除数
	 * @return
	 */
	public static Double divide(Object value1, Object value2, int scale, Integer roundingMode) {
		BigDecimal b1 = BigDecimal.valueOf(DoubleUtil.parse(value1));
		BigDecimal b2 = BigDecimal.valueOf(DoubleUtil.parse(value2));
		return b1.divide(b2, scale, roundingMode).doubleValue();
	}

	/**
	 * 两个数相乘
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double multiply(Double value1, Double value2) {
		BigDecimal b1 = BigDecimal.valueOf(value1);
		BigDecimal b2 = BigDecimal.valueOf(value2);
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 两个数相乘
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double multiply(Integer value1, Integer value2) {
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 两个数相乘
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double multiply(Object value1, Object value2) {
		BigDecimal b1 = BigDecimal.valueOf(DoubleUtil.parse(value1));
		BigDecimal b2 = BigDecimal.valueOf(DoubleUtil.parse(value2));
		return b1.multiply(b2).doubleValue();
	}
}
