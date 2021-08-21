package com.tao.common.excel.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.tao.common.core.common.TowTuple;
import com.tao.common.core.utils.StringUtil;
import com.tao.common.excel.model.excelimport.ExcelField;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.tao.common.excel.listener.E3plusCellWriteHandler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel导入工具类 - 基于Swagger注解进行Excel导入处理
 * 
 * @author jun.chen
 *
 */
@Slf4j
public class ExcelImportUtil {

	public static <T> void writeToResp(HttpServletResponse response, List<T> data, ExcelTypeEnum excelType,
			Class<T> clazz, String fileName) {
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
			response.setHeader("Content-disposition",
					"attachment;filename="
							+ new String(URLEncoder.encode(fileName, "UTF-8").getBytes("UTF-8"), "ISO8859-1")
							+ excelType.getValue());
			response.setHeader("Cache-Control", "no-cache");// 设置头
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		writeToOutputStream(os, data, clazz, fileName, 0);
	}

	public static <T> void writeToOutputStream(OutputStream os, List<T> data, Class<T> clazz, String fileName,
			int sheetNo) {
		ExcelWriterSheetBuilder writerSheet = EasyExcel.write(os).inMemory(Boolean.TRUE).sheet(sheetNo, fileName)
				.relativeHeadRowIndex(2).useDefaultStyle(false);
		List<String> columns = getColumns(clazz);
		writerSheet.needHead(Boolean.FALSE).includeColumnFiledNames(columns)
				.registerWriteHandler(new E3plusCellWriteHandler(clazz)).doWrite(data);

	}

	public static <T> List<String> getColumns(Class<T> clazz) {
		List<String> columns = new ArrayList<>();
		ExcelField[] excelFields = getExcelFields(clazz);
		for (int i = 0; i < excelFields.length; i++) {
			Field field = excelFields[i].getField();
			columns.add(field.getName());
		}
		return columns;
	}
	
	public static <T> List<TowTuple<String, String>> getNotNulField(Class<T> clazz) {
		List<TowTuple<String, String>> fieldArray = new ArrayList<>();
		ExcelField[] fields = ExcelImportUtil.getExcelFields(clazz);
		for (ExcelField excelField : fields) {
			ApiModelProperty property = excelField.getProperty();
			if (property.required()) {
				Field field = excelField.getField();
				fieldArray.add(new TowTuple<String, String>(field.getName(), property.value()));
			}
		}
		return fieldArray;
	}

	public static <T> T getDemoData(Class<T> clazz) {
		JSONObject json = new JSONObject();
		ExcelField[] excelFields = getExcelFields(clazz);
		for (int i = 0; i < excelFields.length; i++) {
			ApiModelProperty property = excelFields[i].getProperty();
			Field field = excelFields[i].getField();
			json.put(field.getName(), property.example());
		}

		return JSON.parseObject(json.toJSONString(), clazz);
	}

	/**
	 * 设置excel 样式 （第一行格式）
	 *
	 * @param workbook
	 * @param cellStyle
	 * @param sheet
	 * @param fields
	 * @param flag      true：数据导出 false：模版导出
	 */
	public static void setWorkBook(Workbook workbook, Sheet sheet, Class<?> clazz) {
		ExcelField[] excelFields = getExcelFields(clazz);
		Row row_hidden = sheet.createRow(0);
		row_hidden.setZeroHeight(true);
		// 多添加一列存储关联对象信息
		Cell classCell = row_hidden.createCell(excelFields.length);
		classCell.setCellValue(clazz.getTypeName());
		// 写入excel的表头（创建第一行）
		Row row = sheet.createRow(1);
		// 设置列宽、表头、数据类型
		for (int i = 0; i < excelFields.length; i++) {

			ApiModelProperty property = excelFields[i].getProperty();
			Field field = excelFields[i].getField();

			Cell hiddenCell = row_hidden.createCell(i);
			hiddenCell.setCellValue(field.getName());
			// 创建第一行
			Cell cell = row.createCell(i);
			// 设置表头名称
			String titleName = property.value() + (property.required() ? " [ * ]" : "");
			cell.setCellValue(titleName);
			// 设置提示信息
			String notes = property.notes() + (property.required() ? ",必填！" : "");
			if (StringUtil.isNotEmptyOrNull(titleName)) {
				setComment(sheet, cell, notes);
			}
			setColumnStyle(workbook, sheet, field, i);
		}
	}

	public static ExcelField[] getExcelFields(Class<?> clazz) {
		ApiModel model = clazz.getDeclaredAnnotation(ApiModel.class);
		if (null == model) {
			// TODO 抛出异常

		}
		// 抽取注解的字段
		Field[] fields = clazz.getDeclaredFields();
		List<ExcelField> properties = new ArrayList<>();
		addProperties(fields, properties);

		Class<?> superclass = clazz.getSuperclass();
		Field[] superFields = superclass.getDeclaredFields();
		addProperties(superFields, properties);
		// 按照 position 进排序
		Collections.sort(properties, Comparator.comparing(a -> a.getProperty().position()));
		return properties.toArray(new ExcelField[0]);
	}

	private static void addProperties(Field[] fields, List<ExcelField> properties) {
		for (Field field : fields) {
			if (field.isAnnotationPresent(ApiModelProperty.class)) {
				ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
				if (annotation.hidden()) {
					continue;
				}
				ExcelField excelField = new ExcelField();
				excelField.setField(field);
				excelField.setProperty(annotation);
				properties.add(excelField);
			}
		}
	}

	/**
	 * 设置默认样式
	 * 
	 * @param workbook
	 * @param cell
	 * @param field
	 */
	private static void setColumnStyle(Workbook workbook, Sheet sheet, Field field, int index) {
		CellStyle style = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		if (field.getType().isAssignableFrom(Date.class)) {
			style.setDataFormat(format.getFormat("yyyy-MM-dd HH:mm:ss"));
		} else {
			// 设置单元格格式为"文本"
			style.setDataFormat(format.getFormat("@"));
		}
		sheet.setDefaultColumnStyle(index, style);
		sheet.setColumnWidth(index, 20 * 256);
	}

	/**
	 * 设置列标题批注
	 * 
	 * @param sheet
	 * @param cell
	 * @param notes
	 */
	private static void setComment(Sheet sheet, Cell cell, String notes) {
		if (StringUtil.isEmptyOrNull(notes)) {
			return;
		}

		Drawing<?> p = sheet.getDrawingPatriarch();
		if (null == p) {
			p = sheet.createDrawingPatriarch();
		}
		// 前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
		Comment comment = null;
		RichTextString notsStrig = null;
		if (sheet instanceof XSSFSheet) {
			comment = p.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
			notsStrig = new XSSFRichTextString(notes);
		} else {
			comment = p.createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
			notsStrig = new HSSFRichTextString(notes);
		}
		// 输入批注信息
		comment.setString(notsStrig);
		// 添加作者,选中B5单元格,看状态栏
		comment.setAuthor("default");
		// 将批注添加到单元格对象中
		cell.setCellComment(comment);
	}
}
