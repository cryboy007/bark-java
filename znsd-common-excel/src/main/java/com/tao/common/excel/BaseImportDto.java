package com.tao.common.excel;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseImportDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "", required = false, notes = "错误信息", position = 9999)
	private String errorMessage;

}
