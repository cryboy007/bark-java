package com.tao.common.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import lombok.Getter;

@Getter
public enum ResourceMappingEnum {
	/** model配置 */
	E3PLUS_MODEL(PropertyType.Entity, "%s", "model"),
	/** 实体枚举对象 */
	E3PLUS_MODEL_ENUM(PropertyType.EntiyEnum, "%sEnum", "enums"),
	/** model对应的默认dto,默认字段和model一致 */
	E3PLUS_DTO(PropertyType.Dto, "%sDto", "dto.portal"),
	/** 查询DTO,默认字段和model一致 */
	E3PLUS_DTO_QUERY(PropertyType.QueryDto, "%sQueryDto", "dto.portal"),
	/** 数据访问对象配置 */
	E3PLUS_DAO(PropertyType.Dao, "%sDao", "dao"),
	/** Mapper配置 */
	E3PLUS_MAPPER(PropertyType.Mapper, "%sMapper", "mapper"),
	/** 服务接口类配置 */
	E3PLUS_SERVICE(PropertyType.Service, "%sService", "service"),
	/** 服务实现类配置 */
	E3PLUS_SERVICE_IMPL(PropertyType.ServiceImpl, "%sServiceImpl", "service.impl"),
	/** 控制层配置 */
	E3PLUS_CONTROLLER(PropertyType.Controller, "%sController", "controller" + StringPool.DOT + "portal");

	private static final String PATH_RESOURCE = "/src/main/resources/";
	private static final String PATH_JAVA = "/src/main/java/";

	/** 对象类型 */
	private PropertyType type;

	/** 固定文件名称后缀 */
	private String name_suffix;
	/** 自己所在包 */
	private String custom_package;

	private ResourceMappingEnum(PropertyType type, String name_suffix, String custom_package) {
		this.type = type;
		this.name_suffix = name_suffix;
		this.custom_package = custom_package;
	}

	public String getFilePath(String projectPath, String parent_package, String entityName) {
		return projectPath + type.getResource_path() + gainPackagePath(parent_package, custom_package)
				+ StringPool.SLASH + entityName + ("".equals(name_suffix) ? type.toString() : name_suffix)
				+ type.getFile_suffix();
	}

	interface IPathHandler {
		String getApiPath();

		String getServicePath();
	}

	private String gainPackagePath(String parent, String childPackage) {
		String path = parent + StringPool.DOT + childPackage;
		return path.replaceAll("\\" + StringPool.DOT, StringPool.SLASH);
	}

	/** 自定义生成当前支持的类型枚举 */
	@Getter
	enum PropertyType {
		/** 实体 */
		Entity("entity.java.ftl"),
		/** 实体枚举对象 */
		EntiyEnum("entityEnum.java.ftl"),
		/** 返回的DTO */
		Dto("dto.java.ftl"),
		/** 查询DTO */
		QueryDto("queryDto.java.ftl"),
		/** 数据访问对象 */
		Dao("dao.java.ftl"),
		/** mybatis xml */
		Mapper(ConstVal.XML_SUFFIX, PATH_RESOURCE, "mapper.ftl"),
		/** 服务接口 */
		Service("service.java.ftl"),
		/** 服务实现 */
		ServiceImpl("serviceImpl.java.ftl"),
		/** 控制层 */
		Controller("controller.java.ftl");

		/** 文件后缀名 */
		private String file_suffix;
		/** 资源所在路径 */
		private String resource_path;
		/** 模板文件路径 */
		private String template;

		private PropertyType(String template) {
			this(ConstVal.JAVA_SUFFIX, PATH_JAVA, template);
		}

		private PropertyType(String file_suffix, String resource_path, String template) {
			this.file_suffix = file_suffix;
			this.resource_path = resource_path;
			this.template = "/templates_default/" + template;
		}

	}

}
