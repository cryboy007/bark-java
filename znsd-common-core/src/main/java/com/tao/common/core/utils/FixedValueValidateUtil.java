package com.tao.common.core.utils;

import java.math.BigDecimal;
import java.util.Date;

public class FixedValueValidateUtil {

	public static boolean fixedValueValidateResult(Object inputValue, Object minValue, Object maxValue) {
		if("".equals(minValue)){
			minValue = null;
		}
		if("".equals(maxValue)){
			maxValue = null;
		}
		// 数字类型的如果没有设置数据范围，取数字默认最大最小范围
		if (inputValue instanceof Integer) {
			Integer value = IntegerUtil.parse(inputValue);
			Integer min = Integer.MIN_VALUE;
			Integer max = Integer.MAX_VALUE;

			if (minValue != null) {
				min = IntegerUtil.parse(minValue);
			}
			if (maxValue != null) {
				max = IntegerUtil.parse(maxValue);
			}
			if (!(min <= value && value <= max)) {
				return false;
			}
		} else if (inputValue instanceof Double) {
			BigDecimal minData = BigDecimal.valueOf(Integer.MIN_VALUE);
			BigDecimal maxData = BigDecimal.valueOf(Integer.MAX_VALUE);
			BigDecimal valueData = BigDecimal.valueOf(DoubleUtil.parse(inputValue));
			if (minValue != null) {
				minData = BigDecimal.valueOf(DoubleUtil.parse(minValue));
			}
			if (maxValue != null) {
				maxData = BigDecimal.valueOf(DoubleUtil.parse(maxValue));
			}
			
		    if(minData != null && maxData == null){
				if (!(minData.compareTo(valueData) <= 0)) {
					return false;
				}
		    }else if(minData == null && maxData != null){
				if (!(valueData.compareTo(maxData) <= 0)) {
					return false;
				}
		    }else if(minData != null && maxData != null){
				if (!(minData.compareTo(valueData) <= 0 && (valueData.compareTo(maxData) <= 0))) {
					return false;
				}
		    }
		} else if (inputValue instanceof Long || inputValue instanceof Date) {
			Long value = 0L;
			if(inputValue instanceof Date){
				Date input = (Date) inputValue;			
				value = input.getTime();
			}else{
			    value = LongUtil.parse(inputValue);
			}
			 Long min = null;
			 Long max = null;
			if (minValue != null) {
				Date minDate = DateUtil.convertToDate(minValue,DateUtil.DEF_DATE_FMT);
				if(minDate == null){
					min = LongUtil.parse(minValue);
				}else{
					min = minDate.getTime();
				}
			}
			if (maxValue != null) {
				Date maxDate = DateUtil.convertToDate(maxValue,DateUtil.DEF_DATE_FMT);
				if(maxDate == null){
					max = LongUtil.parse(maxValue);
				}else{
					max = maxDate.getTime();
				}
			}
			if (min != null && max != null) {
				if (!(min <= value && value <= max)) {
					return false;
				}
			} else if (min != null && max == null) {
				if (!(min <= value)) {
					return false;
				}
			} else if (min == null && max != null) {
				if (!(value <= max)) {
					return false;
				}
			} 
		}
		return true;
	}
}
