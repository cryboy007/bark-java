package com.tao.common.excel.model.excelimport;

import java.io.Serializable;
import java.lang.reflect.Field;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExcelField implements Serializable {

	private static final long serialVersionUID = 1L;

	private Field field;
	private ApiModelProperty property;

}
