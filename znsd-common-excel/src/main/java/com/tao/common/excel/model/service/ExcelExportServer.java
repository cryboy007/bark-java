package com.tao.common.excel.model.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelExportEnum;
import cn.afterturn.easypoi.util.PoiExcelGraphDataUtil;
import cn.afterturn.easypoi.util.PoiPublicUtil;

public class ExcelExportServer extends BaseExportServer {
	 // 鏈�澶ц鏁�,瓒呰繃鑷姩澶歋heet
    private static int MAX_NUM = 60000;

    protected int createHeaderAndTitle(ExportParams entity, Sheet sheet, Workbook workbook,
                                       List<ExcelExportEntity> excelParams) {
        int rows = 0, fieldLength = getFieldLength(excelParams);
        if (entity.getTitle() != null) {
            rows += createHeaderRow(entity, sheet, workbook, fieldLength);
        }
        rows += createTitleRow(entity, sheet, workbook, rows, excelParams);
        sheet.createFreezePane(0, rows, 0, rows);
        return rows;
    }

    /**
     * 鍒涘缓 琛ㄥご鏀瑰彉
     */
    public int createHeaderRow(ExportParams entity, Sheet sheet, Workbook workbook,
                               int fieldWidth) {

        Row row = sheet.createRow(0);
        row.setHeight(entity.getTitleHeight());
        createStringCell(row, 0, entity.getTitle(),
                getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
        for (int i = 1; i <= fieldWidth; i++) {
            createStringCell(row, i, "",
                    getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, fieldWidth));
        if (entity.getSecondTitle() != null) {
            row = sheet.createRow(1);
            row.setHeight(entity.getSecondTitleHeight());
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(HorizontalAlignment.RIGHT);
            createStringCell(row, 0, entity.getSecondTitle(), style, null);
            for (int i = 1; i <= fieldWidth; i++) {
                createStringCell(row, i, "",
                        getExcelExportStyler().getHeaderStyle(entity.getHeaderColor()), null);
            }
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, fieldWidth));
            return 2;
        }
        return 1;
    }

    public void createSheet(Workbook workbook, ExportParams entity, Class<?> pojoClass,
                            Collection<?> dataSet) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel export start ,class is {}", pojoClass);
            LOGGER.debug("Excel version is {}",
                    entity.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }
        if (workbook == null || entity == null || pojoClass == null || dataSet == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        try {
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            // 寰楀埌鎵�鏈夊瓧娈�
            Field[] fileds = PoiPublicUtil.getClassFields(pojoClass);
            ExcelTarget etarget = pojoClass.getAnnotation(ExcelTarget.class);
            String targetId = etarget == null ? null : etarget.value();
            getAllExcelField(entity.getExclusions(), targetId, fileds, excelParams, pojoClass,
                    null, null);
            //鑾峰彇鎵�鏈夊弬鏁板悗,鍚庨潰鐨勯�昏緫鍒ゆ柇灏变竴鑷翠簡
            createSheetForMap(workbook, entity, excelParams, dataSet);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e.getCause());
        }
    }

    public void createSheetForMap(Workbook workbook, ExportParams entity,
                                  List<ExcelExportEntity> entityList, Collection<?> dataSet) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel version is {}",
                    entity.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }
        if (workbook == null || entity == null || entityList == null || dataSet == null) {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
        super.type = entity.getType();
        if (type.equals(ExcelType.XSSF)) {
            MAX_NUM = 1000000;
        }
        if (entity.getMaxNum() > 0) {
            MAX_NUM = entity.getMaxNum();
        }
        Sheet sheet = null;
        try {
            sheet = workbook.createSheet(entity.getSheetName());
        } catch (Exception e) {
            // 閲嶅閬嶅巻,鍑虹幇浜嗛噸鍚嶇幇璞�,鍒涘缓闈炴寚瀹氱殑鍚嶇ОSheet
            sheet = workbook.createSheet();
        }
        insertDataToSheet(workbook, entity, entityList, dataSet, sheet);
    }

    protected void insertDataToSheet(Workbook workbook, ExportParams entity,
                                     List<ExcelExportEntity> entityList, Collection<?> dataSet,
                                     Sheet sheet) {
        try {
            dataHanlder = entity.getDataHanlder();
            if (dataHanlder != null && dataHanlder.getNeedHandlerFields() != null) {
                needHanlderList = Arrays.asList(dataHanlder.getNeedHandlerFields());
            }
            // 鍒涘缓琛ㄦ牸鏍峰紡
            setExcelExportStyler((IExcelExportStyler) entity.getStyle()
                    .getConstructor(Workbook.class).newInstance(workbook));
            Drawing patriarch = PoiExcelGraphDataUtil.getDrawingPatriarch(sheet);
            List<ExcelExportEntity> excelParams = new ArrayList<ExcelExportEntity>();
            if (entity.isAddIndex()) {
                excelParams.add(indexExcelEntity(entity));
            }
            excelParams.addAll(entityList);
            sortAllParams(excelParams);
            int index = entity.isCreateHeadRows()
                    ? createHeaderAndTitle(entity, sheet, workbook, excelParams) : 0;
            int titleHeight = index;
            setCellWith(excelParams, sheet);
            short rowHeight = entity.getHeight() > 0 ? entity.getHeight() : getRowHeight(excelParams);
            setCurrentIndex(1);
            Iterator<?> its = dataSet.iterator();
            List<Object> tempList = new ArrayList<Object>();
            while (its.hasNext()) {
                Object t = its.next();
                index += createCells(patriarch, index, t, excelParams, sheet, workbook, rowHeight);
                tempList.add(t);
                if (index >= MAX_NUM) {
                    break;
                }
            }
            if (entity.getFreezeCol() != 0) {
                sheet.createFreezePane(entity.getFreezeCol(), 0, entity.getFreezeCol(), 0);
            }

            mergeCells(sheet, excelParams, titleHeight);

            its = dataSet.iterator();
            for (int i = 0, le = tempList.size(); i < le; i++) {
                its.next();
                its.remove();
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("List data more than max ,data size is {}",
                        dataSet.size());
            }
            // 鍙戠幇杩樻湁鍓╀綑list 缁х画寰幆鍒涘缓Sheet
            if (dataSet.size() > 0) {
                createSheetForMap(workbook, entity, entityList, dataSet);
            } else {
                // 鍒涘缓鍚堣淇℃伅
                addStatisticsRow(getExcelExportStyler().getStyles(true, null), sheet);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e.getCause());
        }
    }

    /**
     * 鍒涘缓琛ㄥご
     */
    private int createTitleRow(ExportParams title, Sheet sheet, Workbook workbook, int index,
                               List<ExcelExportEntity> excelParams) {
        Row row = sheet.createRow(index);
        int rows = getRowNums(excelParams);
        row.setHeight((short) 450);
        Row listRow = null;
        if (rows == 2) {
            listRow = sheet.createRow(index + 1);
            listRow.setHeight((short) 450);
        }
        int cellIndex = 0;
        int groupCellLength = 0;
        CellStyle titleStyle = getExcelExportStyler().getTitleStyle(title.getColor());
        for (int i = 0, exportFieldTitleSize = excelParams.size(); i < exportFieldTitleSize; i++) {
            ExcelExportEntity entity = excelParams.get(i);
            // 鍔犲叆鎹簡groupName鎴栬�呯粨鏉熷氨锛屽氨鎶婁箣鍓嶇殑閭ｄ釜鎹㈣
            if (StringUtils.isBlank(entity.getGroupName()) || !entity.getGroupName().equals(excelParams.get(i - 1).getGroupName())) {
                if(groupCellLength > 1){
                    sheet.addMergedRegion(new CellRangeAddress(index, index, cellIndex - groupCellLength, cellIndex - 1));
                }
                groupCellLength = 0;
            }
            if (StringUtils.isNotBlank(entity.getGroupName())) {
                createStringCell(row, cellIndex, entity.getGroupName(), titleStyle, entity);
                createStringCell(listRow, cellIndex, entity.getName(), titleStyle, entity);
                groupCellLength++;
            } else if (StringUtils.isNotBlank(entity.getName())) {
                createStringCell(row, cellIndex, entity.getName(), titleStyle, entity);
            }
            if (entity.getList() != null) {
                List<ExcelExportEntity> sTitel = entity.getList();
                if (StringUtils.isNotBlank(entity.getName())) {
                    sheet.addMergedRegion(new CellRangeAddress(index, index, cellIndex, cellIndex + sTitel.size() - 1));
                }
                for (int j = 0, size = sTitel.size(); j < size; j++) {
                    createStringCell(rows == 2 ? listRow : row, cellIndex, sTitel.get(j).getName(),
                            titleStyle, entity);
                    cellIndex++;
                }
                cellIndex--;
            } else if (rows == 2 && StringUtils.isBlank(entity.getGroupName())) {
                createStringCell(listRow, cellIndex, "", titleStyle, entity);
                sheet.addMergedRegion(new CellRangeAddress(index, index + 1, cellIndex, cellIndex));
            }
            cellIndex++;
        }
        if (groupCellLength > 1) {
            sheet.addMergedRegion(new CellRangeAddress(index, index, cellIndex - groupCellLength, cellIndex - 1));
        }
        return rows;

    }
}
