package com.tao.common.core.common.data.model;

import java.util.List;

public interface IDataFileModel<T extends IDataColumnModel> {

	void setName(String name);
	
	/**
	 * 名称
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * 表头集合
	 * 
	 * @return
	 */
	List<T> getColumns();
	
	/**
	 * 获取table数据，必须是兼容2维数组的对象
	 * 
	 * @return
	 */
	Object getData();
	
	void setData(Object data);
}
