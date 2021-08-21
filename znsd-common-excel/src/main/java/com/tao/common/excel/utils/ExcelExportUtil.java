package com.tao.common.excel.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tao.common.excel.model.service.ExcelExportServer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.ExcelBatchExportServer;
import cn.afterturn.easypoi.excel.export.template.ExcelExportOfTemplateUtil;

public class ExcelExportUtil {

    private ExcelExportUtil() {
    }

    /**
     * @param entity
     *            琛ㄦ牸鏍囬灞炴��
     * @param pojoClass
     *            Excel瀵硅薄Class
     * @param dataSet
     *            Excel瀵硅薄鏁版嵁List
     */
    public static Workbook exportBigExcel(ExportParams entity, Class<?> pojoClass,
                                          Collection<?> dataSet) {
        ExcelBatchExportServer batachServer = ExcelBatchExportServer
            .getExcelBatchExportServer(entity, pojoClass);
        return batachServer.appendData(dataSet);
    }

    public static void closeExportBigExcel() {
        ExcelBatchExportServer batachServer = ExcelBatchExportServer.getExcelBatchExportServer(null,
            null);
        batachServer.closeExportBigExcel();
    }

    /**
     * @param entity
     *            琛ㄦ牸鏍囬灞炴��
     * @param pojoClass
     *            Excel瀵硅薄Class
     * @param dataSet
     *            Excel瀵硅薄鏁版嵁List
     */
    public static Workbook exportExcel(ExportParams entity, Class<?> pojoClass,
                                       Collection<?> dataSet) {
        Workbook workbook = getWorkbook(entity.getType(),dataSet.size());
        new ExcelExportServer().createSheet(workbook, entity, pojoClass, dataSet);
        return workbook;
    }

    private static Workbook getWorkbook(ExcelType type, int size) {
        if (ExcelType.HSSF.equals(type)) {
            return new HSSFWorkbook();
        } else if (size < 100000) {
            return new XSSFWorkbook();
        } else {
            return new SXSSFWorkbook();
        }
    }

    /**
     * 鏍规嵁Map鍒涘缓瀵瑰簲鐨凟xcel
     * @param entity
     *            琛ㄦ牸鏍囬灞炴��
     * @param entityList
     *            Map瀵硅薄鍒楄〃
     * @param dataSet
     *            Excel瀵硅薄鏁版嵁List
     */
    public static Workbook exportExcel(ExportParams entity, List<ExcelExportEntity> entityList,
                                       Collection<? extends Map<?, ?>> dataSet) {
        Workbook workbook = getWorkbook(entity.getType(),dataSet.size());;
        new ExcelExportServer().createSheetForMap(workbook, entity, entityList, dataSet);
        return workbook;
    }

    /**
     * 涓�涓猠xcel 鍒涘缓澶氫釜sheet
     * 
     * @param list
     *            澶氫釜Map key title 瀵瑰簲琛ㄦ牸Title key entity 瀵瑰簲琛ㄦ牸瀵瑰簲瀹炰綋 key data
     *            Collection 鏁版嵁
     * @return
     */
    public static Workbook exportExcel(List<Map<String, Object>> list, ExcelType type) {
        Workbook workbook = getWorkbook(type,0);
        for (Map<String, Object> map : list) {
            ExcelExportServer server = new ExcelExportServer();
            server.createSheet(workbook, (ExportParams) map.get("title"),
                (Class<?>) map.get("entity"), (Collection<?>) map.get("data"));
        }
        return workbook;
    }

    /**
     * 瀵煎嚭鏂囦欢閫氳繃妯℃澘瑙ｆ瀽,涓嶆帹鑽愯繖涓簡,鎺ㄨ崘鍏ㄩ儴閫氳繃妯℃澘鏉ユ墽琛屽鐞�
     * 
     * @param params
     *            瀵煎嚭鍙傛暟绫�
     * @param pojoClass
     *            瀵瑰簲瀹炰綋
     * @param dataSet
     *            瀹炰綋闆嗗悎
     * @param map
     *            妯℃澘闆嗗悎
     * @return
     */
    @Deprecated
    public static Workbook exportExcel(TemplateExportParams params, Class<?> pojoClass,
                                       Collection<?> dataSet, Map<String, Object> map) {
        return new ExcelExportOfTemplateUtil().createExcleByTemplate(params, pojoClass, dataSet,
            map);
    }

    /**
     * 瀵煎嚭鏂囦欢閫氳繃妯℃澘瑙ｆ瀽鍙湁妯℃澘,娌℃湁闆嗗悎
     * 
     * @param params
     *            瀵煎嚭鍙傛暟绫�
     * @param map
     *            妯℃澘闆嗗悎
     * @return
     */
    public static Workbook exportExcel(TemplateExportParams params, Map<String, Object> map) {
        return new ExcelExportOfTemplateUtil().createExcleByTemplate(params, null, null, map);
    }

    /**
     * 瀵煎嚭鏂囦欢閫氳繃妯℃澘瑙ｆ瀽鍙湁妯℃澘,娌℃湁闆嗗悎
     * 姣忎釜sheet瀵瑰簲涓�涓猰ap,瀵煎嚭鍒板,key鏄痵heet鐨凬UM
     * @param params
     *            瀵煎嚭鍙傛暟绫�
     * @param map
     *            妯℃澘闆嗗悎
     * @return
     */
    public static Workbook exportExcel(Map<Integer, Map<String, Object>> map,
                                       TemplateExportParams params) {
        return new ExcelExportOfTemplateUtil().createExcleByTemplate(params, map);
    }

}
