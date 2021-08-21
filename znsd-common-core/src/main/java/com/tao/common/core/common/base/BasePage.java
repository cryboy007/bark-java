package com.tao.common.core.common.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author guangbing wu
 */
@Data
@ApiModel(value = "基础分页参数")
public class BasePage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final static int maxPageSize = 10000;
    @ApiModelProperty(value = "第几页")
    private int pageNum;

    @ApiModelProperty(value = "页的条数")
    private int pageSize;

    public int getPageSize() {
        if(pageSize > maxPageSize) {
            return maxPageSize;
        } else {
            return pageSize;
        }
    }
}
