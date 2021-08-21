package com.tao.common.excel.excelimport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.tao.common.excel.model.excelimport.ExcelModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用Excel模板check
 */
@Slf4j
public class CheckExcelTemplates {

	public static <T> List<ExcelModel> readClassValues(Class<T> clz) {
		List<ExcelModel> list = new ArrayList<ExcelModel>();
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields) {
			boolean fieldHasAnno = field.isAnnotationPresent(ExcelProperty.class);
			if (fieldHasAnno) {
				ExcelProperty fieldAnno = field.getAnnotation(ExcelProperty.class);
				String[] vaString = fieldAnno.value();
				int index = fieldAnno.index();
				ExcelModel excelModel = new ExcelModel();
				excelModel.setValues(vaString[0]);
				excelModel.setIndex(index);
				list.add(excelModel);
			}
		}
		return list;
	}

	public static List<String> readExcelHeader(MultipartFile file) {
		List<String> list = new ArrayList<String>();
		boolean isExcel2003 = ExcelImportUtils.checkExcelFormat(file.getOriginalFilename());
		try (Workbook wb = ExcelImportUtils.createWorkbook(isExcel2003, file)) {

			Sheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				Cell cell = row.getCell(i);
				list.add(i + "_" + String.valueOf(cell));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return list;
	}

	public static <T> boolean dataVerification(MultipartFile file, Class<T> clz) {
		List<ExcelModel> readList = readClassValues(clz);
		List<String> beanList = readExcelHeader(file);
		List<Boolean> check = new ArrayList<Boolean>();// 用于check数据
		boolean flag = false;
		for (ExcelModel excelModel : readList) {
			if (beanList.contains(String.valueOf(excelModel.getIndex() + "_" + excelModel.getValues()))) {
				check.add(true);
			}
		}
		if (readList.size() == check.size()) {
			flag = true;
		}
		return flag;
	}

	public static <T> void checkTemplate(MultipartFile file, Class<T> clz) {
		boolean flag = dataVerification(file, clz);
		if (!flag) {
			throw new RuntimeException("该导入模板不正确，请重新下载！");
		}
	}
}