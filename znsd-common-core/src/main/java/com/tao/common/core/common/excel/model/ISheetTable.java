package com.tao.common.core.common.excel.model;


import com.tao.common.core.common.data.model.IDataFileModel;

/**
 * 描述一个网格数据格式的sheet模型
 * 
 * @author hangwen
 *
 */
public interface ISheetTable extends IDataFileModel<ISheetColumn> {

	/**
	 * 获取sheet的一些参数对象（如excel导入模板需要设置模板的id），这些值将写到第一行且被隐藏
	 * 
	 * @return
	 */
	Object getParameter();

	/**
	 * sheet标题，允许在一个sheet中存在多个Table
	 * 
	 * @return
	 */
	String getSheetTitle();

	/**
	 * 起始行索引
	 * 
	 * @return
	 */
	int getStartRowIndex();

	void setParameter(Object parameter);

	void setSheetTitle(String title);

	void setStartRowIndex(int startRowIndex);

}
