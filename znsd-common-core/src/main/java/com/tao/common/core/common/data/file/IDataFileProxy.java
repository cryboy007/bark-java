package com.tao.common.core.common.data.file;


import com.tao.common.core.common.data.model.IDataColumnModel;
import com.tao.common.core.common.data.model.IDataFileModel;

public interface IDataFileProxy<T extends IDataFileModel<? extends IDataColumnModel>> {

	/**
	 * 创建数据文件并写入数据
	 * 
	 * @param model
	 *            数据模型
	 * @param filePath
	 *            保存文件完整路径
	 */
	void createDataFile(T model, String filePath);

	/**
	 * 向已有的数据文件中添加数据，需要先执行
	 * {@link IDataFileProxy#createDataFile(IDataFileModel, String)}
	 * 
	 * @param model
	 * @param filePath
	 */
	void addDataToFile(T model, String filePath);

	/**
	 * 获取文件的完整路径，需要先执行
	 * {@link IDataFileProxy#createDataFile(IDataFileModel, String)}
	 * 
	 * @return
	 */
	String getFilePath();
}
