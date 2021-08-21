package com.tao.common.core.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "分页数据")
@Data
public class Page<T> extends BasePage {
    @ApiModelProperty(value = "页数")
    private int pages;
    @ApiModelProperty(value = "总条数")
    private long total;
    @ApiModelProperty(value = "数据")
    protected List<T> list;
}
