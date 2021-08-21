package com.tao.common.excel.excelimport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.tao.common.core.common.other.ServiceUtils;
import com.tao.common.core.common.redis.BS2RedisPool;
import com.tao.common.core.utils.ObjectUtil;
import com.tao.common.core.utils.StringUtil;
import com.tao.common.excel.model.excelimport.ExcelModel;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExcelImportUtils {
	private static Logger log = LoggerFactory.getLogger(ExcelImportUtils.class);
	public static final String FileName = "fileName";
	public static final String SheetNo = "sheetNo";
	public static final String ClassName = "className";

	private static int maxExpireTime = 30 * 60;

	@SuppressWarnings("unchecked")
	public static <T> List<T> readListFrom(InputStream is, Class<T> clz) {
		SyncReadListener tmpListener = new SyncReadListener();
		ReadSheet readSheet = new ReadSheet();
		readSheet.setHeadRowNumber(2);
		readSheet.setClazz(clz);
		EasyExcel.read(is).registerReadListener(tmpListener).build().read(readSheet);
		return (List<T>) tmpListener.getList();
	}

	public static <T> void writeListTo(HttpServletResponse response, List<T> data, Class<T> clz,
			List<String> simpleHead, String fileName, int sheetNo) {
		ExcelWriter write = null;
		OutputStream os = null;
		try {
			os = response.getOutputStream();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		fileName = toUtf8String(fileName + "." + ExcelTypeEnum.XLSX);
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		response.setContentType("application/msexcel;charset=UTF-8");// 设置类型
		response.setHeader("Pragma", "No-cache");// 设置头
		response.setHeader("Cache-Control", "no-cache");// 设置头
		response.setDateHeader("Expires", 0);// 设置日期头
		WriteSheet writeSheet = new WriteSheet();
		writeSheet.setNeedHead(true);
		writeSheet.setSheetNo(sheetNo);
		List<List<String>> head = simpleHead.stream().map(a -> Arrays.asList(a)).collect(Collectors.toList());
		writeSheet.setHead(head);
		write = EasyExcel.write(os).build();
		write.write(data, writeSheet);
		write.finish();

	}

	public static <T> void excelDownload(Class<T> clz, HttpServletResponse response, String sheetName)
			throws IOException {
		List<T> listExcel = new ArrayList<T>();
		// 头的策略
		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		// 背景设置为红色
		headWriteCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setFontHeightInPoints((short) 10);
		headWriteCellStyle.setWriteFont(headWriteFont);
		// 内容的策略
		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
		WriteFont contentWriteFont = new WriteFont();
		// 字体大小
		contentWriteFont.setFontHeightInPoints((short) 10);
		contentWriteCellStyle.setWriteFont(contentWriteFont);

		// 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
		HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle,
				contentWriteCellStyle);
		// 注删样式并写入excel
		EasyExcel.write(response.getOutputStream(), clz).registerWriteHandler(horizontalCellStyleStrategy)
				.sheet(sheetName).doWrite(listExcel);

	}

	/**
	 * 校验Workbook
	 * 
	 * @param wb
	 */
	public static void checkWorkbook(Workbook wb) {
		if (null == wb) {
			throw new RuntimeException("Workbook is null");
		}
	}

	public static boolean checkImportExcel(@NotNull MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		if (null == originalFilename) {
			throw new RuntimeException("filename is null！");
		}
		boolean isExcel2003 = ExcelImportUtils.checkExcelFormat(originalFilename);
		Workbook wb = ExcelImportUtils.createWorkbook(isExcel2003, file);
		boolean notNull = ExcelImportUtils.checkExcelIsNull(wb);
		return notNull;
	}

	public static String getFileName(@NotNull MultipartFile file) {
		String fileName = file.getOriginalFilename();
		if (null == fileName) {
			throw new RuntimeException("filename is null！");
		}
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		fileName = fileName + "导入失败日志";
		return fileName;
	}

	public static List<LinkedHashMap<String, Object>> getDataList(@NotNull MultipartFile file) {
		List<LinkedHashMap<String, Object>> list = new ArrayList<>();
		String originalFilename = file.getOriginalFilename();
		if (null == originalFilename) {
			throw new RuntimeException("filename is null！");
		}
		boolean isExcel2003 = ExcelImportUtils.checkExcelFormat(originalFilename);
		Workbook wb = ExcelImportUtils.createWorkbook(isExcel2003, file);
		boolean notNull = ExcelImportUtils.checkExcelIsNull(wb);
		if (notNull) {
			int sheetnum = wb.getNumberOfSheets();
			getFinalList(list, sheetnum, wb, file);
		} else {
			throw new RuntimeException("Excle列表为空！");
		}
		return list;
	}

	public static int getSheetNum(@NotNull MultipartFile file) {
		String originalFilename = file.getOriginalFilename();
		if (null == originalFilename) {
			throw new RuntimeException("filename is null！");
		}
		boolean isExcel2003 = ExcelImportUtils.checkExcelFormat(originalFilename);
		Workbook wb = ExcelImportUtils.createWorkbook(isExcel2003, file);
		checkWorkbook(wb);
		int sheetnum = wb.getNumberOfSheets();
		return sheetnum;
	}

	public static List<LinkedHashMap<String, Object>> getFinalList(List<LinkedHashMap<String, Object>> list,
			int sheetnum, Workbook wb, MultipartFile file) {
		for (int i = 0; i < sheetnum; i++) {
			Sheet sheet = wb.getSheetAt(i);
			Row namerow = sheet.getRow(0);// 表头数据
			Row headrow = sheet.getRow(1);// 表头数据
			checkHeader(headrow);
			int rowNum = sheet.getPhysicalNumberOfRows();// 总行数
			log.info(file.getName() + "共：" + rowNum + " 行！");
			// int colNum = headrow.getPhysicalNumberOfCells();// 总列数()
			int colNum = namerow.getPhysicalNumberOfCells();
			// 判断工作表是否为空
			if (rowNum == 0) {
				continue;
			}
			assembleData(rowNum, list, sheet, colNum);
		}
		return list;
	}

	public static <T> int getTotalNum(List<T> list) {
		return list.size();
	}

	public static List<LinkedHashMap<String, Object>> assembleData(int rowNum, List<LinkedHashMap<String, Object>> list,
			Sheet sheet, int colNum) {
		for (int j = 1; j <= rowNum; j++) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			Row row = sheet.getRow(j);
			if (null != row) {
				for (int k = 0; k < colNum; k++) {
					map.put(String.valueOf(k + 1), ReadExcelXlsxUtil.getStringCellvalue(row.getCell(k)));
				}
				list.add(map);
				continue;
			}
		}
		return list;
	}

	/**
	 * Excel头部校验
	 */
	public static void checkHeader(Row headrow) {
		if (null == headrow) {
			throw new RuntimeException("Excel头部校验失败！");
		}
	}

	/**
	 * 判断Excel文件的版本
	 */
	public static boolean checkExcelFormat(String filename) {
		if (StringUtil.isEmptyOrNull(filename)) {
			throw new RuntimeException("filename is null！");
		}

		boolean isExcel2003 = true;
		if (filename.matches("^.+\\.(?i)(xlsx)$")) {
			isExcel2003 = false;
		}
		return isExcel2003;
	}

	/**
	 * Workbook
	 */
	public static Workbook createWorkbook(boolean isExcel2003, MultipartFile file) {
		Workbook wb = null;
		try {
			InputStream fis = file.getInputStream();
			if (isExcel2003) {
				wb = new HSSFWorkbook(fis);
			} else {
				wb = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			log.error("");
		}
		return wb;
	}

	/**
	 * 判断Excel是否为空
	 */
	public static boolean checkExcelIsNull(Workbook wb) {
		if (null != wb && null != wb.getSheetAt(0).getRow(0)) {
			return true;
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]+)?$");
		return pattern.matcher(str).matches();
	}

	public static String get32UUID() {
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}

	public static Object get(String... keys) {
		JedisCommands client = null;
		try {
			client = ServiceUtils.getService(BS2RedisPool.class).getSource();
			String key = genKey(keys);
			return client.get(key);
		} finally {
			if (client != null && client instanceof Jedis) {
				((Jedis) client).close();
			}
		}
	}

	public static void add(Object value, String... keys) {
		JedisCommands client = null;
		try {
			client = ServiceUtils.getService(BS2RedisPool.class).getSource();
			String key = genKey(keys);
			if (value != null) {
				client.set(key, ObjectUtil.serializeByObjectMapper(value));
				client.expire(key, maxExpireTime);
			}
		} finally {
			if (client != null && client instanceof Jedis) {
				((Jedis) client).close();
			}
		}
	}

	public static <T> List<T> strToBean(String jsonString, Class<T> beanClass) {
		List<T> list = JSON.parseArray(jsonString, beanClass);
		return list;
	}

	public static String genKey(String... keys) {
		Object[] args = new Object[keys.length];
		int i = 0;
		for (Object key : keys) {
			args[i++] = key;
		}
		return StringUtil.getKey(args);
	}

	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	public static <T> void holdRedis(List<T> data, String uuid, MultipartFile file, Class<T> clz) {
		add(data, uuid);
		add(getFileName(file), uuid + FileName);
		add(getSheetNum(file), uuid + SheetNo);
		add(clz, uuid + ClassName);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void excelWrite(String uuid, HttpServletResponse response) {
		String className = String.valueOf(get(uuid + ClassName));
		if (ObjectUtils.isEmpty(className)) {
			throw new RuntimeException("错误信息有效期半小时，已过期！");
		}
		className = className.replace("\"", "");
		try {
			Class clz = Class.forName(className);
			List<String> head = getField(clz);
			List<T> readList = ExcelImportUtils.strToBean(String.valueOf(ExcelImportUtils.get(uuid)), clz);
			writeListTo(response, readList, clz, head, String.valueOf(get(uuid + ExcelImportUtils.FileName)),
					Integer.parseInt(String.valueOf(get(uuid + ExcelImportUtils.SheetNo))));
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		}
	}

	public static <T> void getClassValues(Class<T> clz) {
		boolean clzHasAnno = clz.isAnnotationPresent(ExcelProperty.class);
		if (clzHasAnno) {
			ExcelProperty annotation = clz.getAnnotation(ExcelProperty.class);
			String[] values = annotation.value();
			int index = annotation.index();
			log.info(Arrays.asList(values).toString() + index);
		}
	}

	public static <T> List<String> getField(Class<T> clz) {
		List<ExcelModel> list = new ArrayList<ExcelModel>();
		List<String> strList = new ArrayList<String>();
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields) {
			boolean fieldHasAnno = field.isAnnotationPresent(ExcelProperty.class);
			if (fieldHasAnno) {
				ExcelProperty fieldAnno = field.getAnnotation(ExcelProperty.class);
				String[] values = fieldAnno.value();
				int index = fieldAnno.index();
				ExcelModel excelModel = new ExcelModel();
				excelModel.setValues(Arrays.asList(values).toString());
				excelModel.setIndex(index);
				list.add(excelModel);
			}
		}
		if (null != list && list.size() > 0) {
			Collections.sort(list);
			list.forEach(li -> {
				strList.add(li.getValues());
			});
		}
		return strList;
	}

	public static String objectSerialiable(Object obj) {
		String serStr = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(obj);
			serStr = byteArrayOutputStream.toString("ISO-8859-1");
			serStr = java.net.URLEncoder.encode(serStr, "UTF-8");

			objectOutputStream.close();
			byteArrayOutputStream.close();
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return serStr;
	}

	public static Object objectDeserialization(String serStr) {
		Object newObj = null;
		try {
			String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			newObj = objectInputStream.readObject();
			objectInputStream.close();
			byteArrayInputStream.close();
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return newObj;
	}

	public static <T> void getMethod(Class<T> clz) {
		Method[] methods = clz.getDeclaredMethods();
		for (Method method : methods) {
			boolean methodHasAnno = method.isAnnotationPresent(ExcelProperty.class);
			if (methodHasAnno) {
				ExcelProperty methodAnno = method.getAnnotation(ExcelProperty.class);
				String[] values = methodAnno.value();
				int index = methodAnno.index();
				log.info(Arrays.asList(values).toString() + index);
			}
		}
	}
}
