package ${cfg.EnumPath}; 

import com.tao.common.api.util.IReqEnum;

/**
 * <p>
 * ${table.comment!}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public enum ${entity}Enum implements IReqEnum {

<#-- ----------  BEGIN 字段循环遍历  ---------->
	TABLE_NAME("${table.name}", "${table.comment!}"),
<#list table.fields as field>
    ${field.propertyName?upper_case}("${field.name}","${field.comment}"),
</#list>
	;
<#------------  END 字段循环遍历  ---------->
	/** 代码 */
    private  String enumCode;
    /** 名称 */
    private  String enumName;
    
    ${entity}Enum(String enumCode, String enumName) {
        this.enumCode = enumCode;
        this.enumName = enumName;
    }
    
    public String getCode() {
        return enumCode;
    }

    public String getName() {
        return enumName;
    }



    @Override
    public String getTableName() {
        return TABLE_NAME.getCode();
    }

    public void setEnumCode(String enumCode) {
        this.enumCode = enumCode;
    }

    @Override
    public String getFieldName() {
        return this.getCode();
    }
}
