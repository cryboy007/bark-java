package com.tao.common.excel.listener;

import com.tao.common.excel.utils.ExcelImportUtil;
import org.apache.poi.ss.usermodel.Sheet;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;

public class E3plusCellWriteHandler extends AbstractRowWriteHandler {

	private Class<?> clazz;

	public E3plusCellWriteHandler(Class<?> clazz) {
		this.clazz = clazz;
	}

	
	@Override
	public void beforeRowCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Integer rowIndex,
			Integer relativeRowIndex, Boolean isHead) {
		// 设置首行隐藏
		if (0 == relativeRowIndex) {
			Sheet cachedSheet = writeSheetHolder.getCachedSheet();
			ExcelImportUtil.setWorkBook(cachedSheet.getWorkbook(), cachedSheet, clazz);
		}
		super.beforeRowCreate(writeSheetHolder, writeTableHolder, rowIndex, relativeRowIndex, isHead);
	}

}
