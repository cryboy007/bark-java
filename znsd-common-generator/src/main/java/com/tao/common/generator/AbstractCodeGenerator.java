package com.tao.common.generator;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;


@Slf4j
public abstract class AbstractCodeGenerator {

	protected abstract String getParentPackage();

	protected abstract String getProjectPathName();

	/**
	 * DTO 文件包路径
	 * 
	 * @return
	 */
	protected String getDtoProjectPath() {
		return "api.dto.portal";
	}

	private static final String API_PROJECT_PACKAGE = "api";
	private static final String SERVICE_PROJECT_PACKAGE = "service";

	private String getServiceProjectPath() {
		return getProjectPathName() + "-service";
	}

	protected abstract String getDataBaseURL();

	protected abstract String getDataBaseUSER();

	protected abstract String getDataBasePWD();

	protected abstract BinaryTuple<String, String[]> getTables();

	private String getTablePrefix() {
		return getTables().getFirst();
	}

	private String[] getTableArray() {
		return getTables().getSecond();
	}

	/**
	 * @return
	 */
	public String getProjectPath() {
		String projectPath = System.getProperty("user.dir");
		String serviceProjectName = getServiceProjectPath().substring(1);
		if (projectPath.endsWith(serviceProjectName)) {
			return projectPath.substring(0, projectPath.indexOf(serviceProjectName) - 1);
		} else {
			return projectPath;
		}
	}

	public void execute() {
		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		String projectPath = getProjectPath();
		File fileTemp = new File(projectPath + getServiceProjectPath() + "/src/main/java");
		System.out.println("file1.getAbsolutePath() = " + fileTemp.getAbsolutePath());
		gc.setOutputDir(fileTemp.getAbsolutePath());
		// 作者通过控制台输入
		gc.setAuthor(scanner("Author"));
		gc.setBaseResultMap(true);
		gc.setBaseColumnList(true);
		gc.setOpen(false);
		gc.setSwagger2(true);

		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setUrl(getDataBaseURL());
		dsc.setDriverName("com.mysql.cj.jdbc.Driver");
		dsc.setUsername(getDataBaseUSER());
		dsc.setPassword(getDataBasePWD());
		dsc.setTypeConvert(new MySqlTypeConvert() {
			@Override
			public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
				String t = fieldType.toUpperCase();
				if (t.contains("DATE") || t.contains("TIMESTAMP")) {
					return DbColumnType.DATE;
				}
				return super.processTypeConvert(globalConfig, fieldType);
			}
		});
		mpg.setDataSource(dsc);

		// 包配置
		DefaultPackageConfig packageConfig = new DefaultPackageConfig(gc, getParentPackage(), getProjectPathName(),
				API_PROJECT_PACKAGE, SERVICE_PROJECT_PACKAGE);
		packageConfig.setPackageNodeDto(getDtoProjectPath());
		mpg.setPackageInfo(packageConfig);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				Map<String, Object> map = this.getMap();
				if (null == map) {
					map = new HashMap<>();
					this.setMap(map);
				}
				map.put("PARENT_PACKAGE", getParentPackage());
				map.put("DAO_PACKAGE", String.format("%s.%s", getParentPackage(), packageConfig.getMapper()));
				map.put("EnumPath", String.format("%s.%s", getParentPackage(), "api.enums"));
				// 自定义配置信息传入生成器
				map.putAll(packageConfig.getConfigMap());
			}
		};

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();

		for (ResourceMappingEnum mapping : ResourceMappingEnum.values()) {
			String templatePath = mapping.getType().getTemplate();
			focList.add(new FileOutConfig(templatePath) {
				@Override
				public String outputFile(TableInfo tableInfo) {
					String filePath = packageConfig.getFilePath(mapping, tableInfo.getEntityName());
					log.info("Path:{},Template:{}", filePath, templatePath);
					// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
					return projectPath + filePath;
				}
			});
		}

		cfg.setFileOutConfigList(focList);

		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();
		// 自定义路径后，默认的设置null
		templateConfig.setEntity(null);
		templateConfig.setXml(null);
		templateConfig.setService(null);
		templateConfig.setMapper(null);
		templateConfig.setController(null);
		templateConfig.setServiceImpl(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();

		// 此处填写需要生成的表的表名数组
		strategy.setInclude(getTableArray());
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntityLombokModel(true);
		// @TableField注解
		strategy.setEntityTableFieldAnnotationEnable(true);
		// rest风格
		strategy.setRestControllerStyle(true);
		strategy.setControllerMappingHyphenStyle(true);
		// strategy.setLogicDeleteFieldName("enable");逻辑删除字段
		// 表名前缀,比如ord组的为ord_
		strategy.setTablePrefix(getTablePrefix());
		mpg.setStrategy(strategy);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.execute();

	}

	/**
	 * <p>
	 * 读取控制台内容
	 * </p>
	 */
	public static String scanner(String tip) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(System.in);
			StringBuilder help = new StringBuilder();
			help.append("请输入" + tip + ":");
			System.out.println(help.toString());
			if (scanner.hasNext()) {
				String ipt = scanner.next();
				if (StringUtils.isNotBlank(ipt)) {
					return ipt;
				}
			}
			throw new MybatisPlusException("请输入正确的" + tip + "！");
		} finally {
			if (null != scanner) {
				scanner.close();
			}
		}
	}

}