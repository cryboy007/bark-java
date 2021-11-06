package com.tao.common.excel;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.tao.common.excel.listener.DefaultImportListener;
import com.tao.common.excel.model.excelimport.ExcelField;
import com.tao.common.excel.service.IExcelImportService;
import com.tao.common.excel.utils.ExcelImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.tao.common.core.utils.CollectionUtil;
import com.tao.common.core.utils.DateUtil;
import com.tao.common.core.utils.KeyUtils;
import com.tao.common.core.utils.StringUtil;
import com.tao.common.core.common.TowTuple;
import com.tao.common.core.common.exception.BizException;
import com.tao.common.core.common.message.Result;
import com.tao.common.core.tool.ftp.FTPUtil;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel导入抽象Controller
 * 
 * @author jun.chen
 *
 * @param <T>
 * @param <A>
 */
@Slf4j
public abstract class AbstractImportController<T extends BaseImportDto, A extends IExcelImportService<T>> {

	@Autowired
	private A importService;

	@Autowired
	private FTPUtil ftpUtil;

	private static final String EXCEL_ERROR_PATH = "/Excel/Error/{0}/{1}";

	private String getErrorExcelPath() {
		String typeName = getTClazz().getTypeName();
		String format = DateUtil.format(DateUtil.getToday(), DateUtil.DEF_DATE_FMT);
		return StringUtil.format(EXCEL_ERROR_PATH, typeName.substring(typeName.lastIndexOf(".") + 1), format);
	}

	/**
	 * 获取T.class
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getTClazz() {
		Type type = getClass().getGenericSuperclass();
		Class<T> result = null;
		if (type instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) type;
			result = (Class<T>) pType.getActualTypeArguments()[0];
		}
		return result;
	}

	// 默认Excel类型
	private ExcelTypeEnum getExcelType() {
		return importService.getExcelType();
	}

	/** 导入模板名称 */
	protected String getExcelName() {
		return importService.getExcelName();
	}

	@GetMapping(value = "/template/download")
	public void download(HttpServletResponse response) {
		Class<T> clazz = getTClazz();
		ExcelImportUtil.writeToResp(response, Arrays.asList(ExcelImportUtil.getDemoData(clazz)), getExcelType(), clazz,
				getExcelName());
	}

	@ApiOperation(value = "Excel导入", httpMethod = "POST")
	@PostMapping(value = "/import")
	@Transactional(rollbackFor = Exception.class)
	public Result<?> excelImport(@RequestParam("file") MultipartFile file) {
		Class<T> clazz = getTClazz();
		ExcelField[] fields = ExcelImportUtil.getExcelFields(clazz);
		// 使用阿里Excel工具对Excel进行处理
		try {
			EasyExcel.read(file.getInputStream(), clazz, getListener(0, clazz, fields)).useDefaultListener(true).sheet()
					.headRowNumber(2).doRead();
		} catch (Exception e) {
			if (e instanceof BizException) {
				BizException bizException = (BizException) e;
				return Result.error(bizException.getCode(), bizException.getData(), bizException.getMessage());
			} else if (e instanceof ExcelAnalysisException) {
				ExcelAnalysisException exception = (ExcelAnalysisException) e;
				return Result.error("ExcelAnalysisException", "Excel解析异常：" + exception.getMessage());
			} else {
				return Result.error("Error", e.getMessage());
			}
		}
		return Result.success();
	}

	/**
	 * 初始化导入监听
	 * 
	 * @param checkRow 检测数据所在行
	 * @param fields   单据字段
	 * @return
	 */
	private DefaultImportListener<T> getListener(int checkRow, Class<T> clazz, ExcelField[] fields) {
		return new DefaultImportListener<T>(new DefaultImportListener.ImportHelper<T>() {

			/** 是否存在异常标识 */
			private boolean hasError = false;
			private List<TowTuple<String, String>> notNulFields = ExcelImportUtil.getNotNulField(clazz);

			@Override
			public void checkHeadMap(Map<Integer, String> headMap, ReadRowHolder rowHolder) {
				Integer rowIndex = rowHolder.getRowIndex();
				// 指定检验行，当导入模板和当前导入接口不匹配提示异常
				if (rowIndex == checkRow) {
					StringBuilder messageBuilder = new StringBuilder();
					String classVal = headMap.get(fields.length);
					if (!clazz.getTypeName().equals(classVal)) {
						messageBuilder.append("导入模板与当前接口不匹配！");
					} else {
						List<String> errorList = new ArrayList<>();
						for (int i = 0; i < fields.length; i++) {
							ExcelField excelField = fields[i];
							Field beanField = excelField.getField();
							String fieldVal = headMap.get(i);
							if (!beanField.getName().equals(fieldVal)) {
								errorList.add(String.format("Excel第%s列与参数列[%s]不匹配", i, beanField.getName()));
							}
						}
						if (CollectionUtil.isNotEmpty(errorList)) {
							messageBuilder.append("导入失败！").append(String.join(",", errorList)).append(";");
						}
					}
					if (messageBuilder.length() > 0) {
						throw new ExcelAnalysisException(messageBuilder.toString());
					}
				}
			}

			@Override
			public void dataCheck(T data, ReadRowHolder rowHolder) {
				BaseImportDto msgDto = (BaseImportDto) data;
				// 重新导入默认清空异常信息
				msgDto.setErrorMessage(null);

				// 行数据必填校验
				List<String> errorMsg = new ArrayList<>();
				if (CollectionUtil.isNotEmpty(notNulFields)) {
					JSONObject parseObject = JSON.parseObject(JSON.toJSONString(data));
					for (TowTuple<String, String> notNulField : notNulFields) {
						if (null == parseObject.get(notNulField.getFrist())) {
							errorMsg.add(notNulField.getSecond());
						}
					}
				}
				if (CollectionUtil.isNotEmpty(errorMsg)) {
					String notNullMsg = String.join("、", errorMsg);
					msgDto.setErrorMessage(String.format("[%s]必填,不能为空！", notNullMsg));
					if (!hasError) {
						hasError = true;
					}
					return;
				}

				TowTuple<Boolean, String> checkResult = importService.checkRowData(data);
				// 失败时对异常进行赋值
				if (!checkResult.getFrist()) {
					msgDto.setErrorMessage(checkResult.getSecond());
					if (!hasError) {
						hasError = true;
					}
				}
			}

			private String getTempPath(String fileName, String suffix) {
				return System.getProperty("java.io.tmpdir") + File.separator + fileName + "_"
						+ KeyUtils.getSnakeflakeStringKey() + suffix;
			}

			@Override
			public void executeSave(AnalysisContext context, List<T> datas) {
				if (hasError) {
					String errorExcelPath = getErrorExcelPath();
					File tempFile = null;
					FileOutputStream outputStream;
					String errorMsg = "Excel导入异常";
					boolean uploadFlag = false;
					String nginxFileUrl = ftpUtil.getNginxUrl() + errorExcelPath;
					try {
						tempFile = new File(getTempPath(getExcelName(), getExcelType().getValue()));
						outputStream = new FileOutputStream(tempFile);
						ExcelImportUtil.writeToOutputStream(outputStream, datas, clazz, getExcelName(), 0);
						outputStream.close();
						// 写入ftp
						boolean uploadFile = ftpUtil.uploadFile(errorExcelPath, tempFile.getName(),
								new FileInputStream(tempFile));
						if (!uploadFile) {
							errorMsg = "Excel上传文件服务器失败！";
						} else {
							uploadFlag = true;
						}
						nginxFileUrl = nginxFileUrl + "/" + tempFile.getName();
					} catch (Exception e) {
						errorMsg = String.format("数据写入文件异常：%s", e.getMessage());
					} finally {
						// 删除临时文件
						if (null != tempFile) {
							tempFile.delete();
						}
					}
					BizException bizException = new BizException("ImportError", errorMsg);
					if (uploadFlag) {
						bizException.setData(nginxFileUrl);
					}
					throw bizException;
				}
				// 处理DTO填充
				Map<String, List<String>> dtoMap = getDTOMap(fields);
				if (CollectionUtil.isNotEmpty(datas)) {
					for (Entry<String, List<String>> entry : dtoMap.entrySet()) {
						String key = entry.getKey();
						List<String> fieldNames = entry.getValue();
						for (T group : datas) {
							try {
								fillDto(key, fieldNames, group);
							} catch (Exception e) {
								log.error(e.getMessage());
							}
						}
					}
				}
				importService.handelData(datas);
			}

		});
	}

	private void fillDto(String key, List<String> fieldNames, T data) throws Exception {
		PropertyDescriptor dtoPd = new PropertyDescriptor(key, data.getClass());
		Method getMethod = dtoPd.getReadMethod();
		if (null == getMethod) {
			return;
		}
		Object dto = getMethod.invoke(data);
		for (String fieldName : fieldNames) {
			if (fieldName.contains("_")) {
				continue;
			}
			PropertyDescriptor pd = new PropertyDescriptor(fieldName, dto.getClass());
			Method setMethod = pd.getWriteMethod();
			// invoke是执行set方法
			if (setMethod != null) {
				PropertyDescriptor methodPd = new PropertyDescriptor(fieldName, data.getClass());
				Method fieldMethod = methodPd.getReadMethod();
				if (null == fieldMethod) {
					continue;
				}
				Object invoke = fieldMethod.invoke(data);
				if (null == invoke) {
					continue;
				}
				if (setMethod.getParameterTypes()[0].getTypeName().contains("BigDecimal")) {
					setMethod.invoke(dto, BigDecimal.valueOf(Double.valueOf(String.valueOf(fieldMethod.invoke(data)))));
				} else {
					setMethod.invoke(dto, fieldMethod.invoke(data));
				}
			}
		}
	}

	private Map<String, List<String>> getDTOMap(ExcelField[] fields) {
		List<ExcelField> properties = Arrays.asList(fields).stream()
				.filter(a -> StringUtil.isNotEmptyOrNull(a.getProperty().dataType())).collect(Collectors.toList());
		return properties.stream().collect(Collectors.groupingBy(a -> a.getProperty().dataType(),
				Collectors.mapping(a -> a.getField().getName(), Collectors.toList())));
	}

}
