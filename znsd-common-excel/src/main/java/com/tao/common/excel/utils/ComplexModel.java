package com.tao.common.excel.utils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ComplexModel {
	private List<KeyVuleModel> keyVuleModels = new ArrayList<KeyVuleModel>();
	private List<TableModel> tables = new ArrayList<TableModel>();

	public List<KeyVuleModel> getKeyVuleModels() {
		return keyVuleModels;
	}

	public void setKeyVuleModels(List<KeyVuleModel> keyVuleModels) {
		this.keyVuleModels = keyVuleModels;
	}

	public List<TableModel> getTables() {
		return tables;
	}

	public void setTables(List<TableModel> tables) {
		this.tables = tables;
	}
}
