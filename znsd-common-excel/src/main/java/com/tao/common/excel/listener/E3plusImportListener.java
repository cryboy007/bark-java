package com.tao.common.excel.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;

public class E3plusImportListener<T> extends AnalysisEventListener<T> {

	private ImportHelper<T> helper;
	private List<T> datas = new ArrayList<>();

	public E3plusImportListener(ImportHelper<T> helper) {
		this.helper = helper;
	}

	@Override
	public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
		helper.checkHeadMap(headMap, context.readRowHolder());
	}

	@Override
	public void invoke(T data, AnalysisContext context) {
		helper.dataCheck(data, context.readRowHolder());
		datas.add(data);
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		helper.executeSave(context, datas);
	}

	public interface ImportHelper<T> {
		// Excel表头检查
		void checkHeadMap(Map<Integer, String> headMap, ReadRowHolder rowHolder);

		// 数据检查,写入异常
		void dataCheck(T data, ReadRowHolder rowHolder);

		// Excel解析完成后的数据处理
		void executeSave(AnalysisContext context, List<T> datas);
	}

}
