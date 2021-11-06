package com.tao.common;

import com.tao.common.generator.AbstractCodeGenerator;
import com.tao.common.generator.BinaryTuple;

/**
 * @ClassName DemoGenerator
 * @Author tao.he
 * @Since 2021/10/24 20:17
 */
public class DemoGenerator extends AbstractCodeGenerator {

    private static final String PARENT_PACKAGE = "com.znsd.wms";

    private static final String PROJECT_PATH = "/znsd-wms";

    private static final String URL = "jdbc:mysql://119.91.255.96:3306/nacos-dev?characterEncoding=UTF-8&useSSL=true&requireSSL=false&zeroDateTimeBehavior=convertToNull&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true";
    private static final String USER = "root";
    private static final String PWD = "jwdlh@2021";

    private static final String TABLE_PREFIX = "nacos_";
    private static final String[] tables = new String[] { "config_info" };

    public static void main(String[] args) {
        // TODO 切换复制过去的 this
        DemoGenerator generator = new DemoGenerator();
        generator.execute();
    }

    @Override
    protected String getParentPackage() {
        return PARENT_PACKAGE;
    }

    @Override
    protected String getProjectPathName() {
        return PROJECT_PATH;
    }

    @Override
    protected String getDataBaseURL() {
        return URL;
    }

    @Override
    protected String getDataBaseUSER() {
        return USER;
    }

    @Override
    protected String getDataBasePWD() {
        return PWD;
    }

    @Override
    protected BinaryTuple<String, String[]> getTables() {
        return new BinaryTuple<String, String[]>(TABLE_PREFIX, tables);
    }
}
