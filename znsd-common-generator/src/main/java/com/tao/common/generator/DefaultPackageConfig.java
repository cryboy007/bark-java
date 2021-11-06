package com.tao.common.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jun.chen
 *
 */
@Getter
public class DefaultPackageConfig extends PackageConfig {

	private String project_path;
	private String project_package_api;
	private String project_package_service;

	private String package_node_dto;

	public void setPackageNodeDto(String package_node_dto) {
		this.package_node_dto = package_node_dto;
	}

	/**
	 * 传给代码生成器cfg参数的Map，变量取值 cfg.key
	 *
	 * @return
	 */
	public Map<String, Object> getConfigMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("package_node_dto", getPackage_node_dto());
		return map;
	}

	public DefaultPackageConfig(GlobalConfig gc, String parent_package, String project_path, String project_package_api,
                                String project_package_service) {
		super.setParent(parent_package);
		this.project_path = project_path;
		this.project_package_api = project_package_api;
		this.project_package_service = project_package_service;

		for (ResourceMappingEnum mapping : ResourceMappingEnum.values()) {
			ResourceMappingEnum.PropertyType type = mapping.getType();
			String name_suffix = mapping.getName_suffix();
			String packagePath = getPackagePath(mapping);
			switch (type) {
			case Entity:
				super.setEntity(packagePath);
				break;
			case Dao:
				gc.setMapperName(name_suffix);
				super.setMapper(packagePath);
				break;
			case Mapper:
				super.setXml(null);
				break;
			case Service:
				gc.setServiceName(name_suffix);
				super.setService(packagePath);
				break;
			case ServiceImpl:
				gc.setServiceImplName(name_suffix);
				super.setServiceImpl(packagePath);
				break;
			case Controller:
				gc.setControllerName(name_suffix);
				super.setController(packagePath);
				break;
			default:
				break;
			}
		}
	}

	public String getFilePath(ResourceMappingEnum mapping, String entityName) {
		ResourceMappingEnum.PropertyType type = mapping.getType();
		BinaryTuple<String, String> servicePath = getServicePath(mapping);
		String rootPath = servicePath.getFirst() + type.getResource_path();
		String parent = getParent();
		if (type.equals(ResourceMappingEnum.PropertyType.Mapper)) {
			parent = "";
		}
		String packagePath = gainPackagePath(parent, getPackagePath(mapping));
		String fileName = getFileName(mapping, entityName);
		return rootPath + packagePath + StringPool.SLASH + fileName;
	}

	private String getPackagePath(ResourceMappingEnum mapping) {
		BinaryTuple<String, String> servicePath = getServicePath(mapping);
		String packagePath = servicePath.getSecond() + mapping.getCustom_package();
		if (mapping.getType().equals(ResourceMappingEnum.PropertyType.Mapper)) {
			packagePath = mapping.getCustom_package();
		}
		return packagePath;
	}

	private BinaryTuple<String, String> getServicePath(ResourceMappingEnum mapping) {
		ResourceMappingEnum.PropertyType type = mapping.getType();
		boolean isMapper = false;
		boolean isApi = false;
		switch (type) {
		case Mapper:
			isMapper = true;
			break;
		case EntiyEnum:
		case QueryDto:
		case Dto:
			isApi = true;
			break;
		default:
			break;
		}

		String projectName = getProject_package_service();
		if (isApi) {
			projectName = getProject_package_api();
		}

		String projectPackageName = projectName + StringPool.DOT;
		if (isMapper) {
			projectPackageName = "";
		}

		return new BinaryTuple<String, String>(project_path + "-" + projectName, projectPackageName);
	}

	private String getFileName(ResourceMappingEnum mapping, String entityName) {
		ResourceMappingEnum.PropertyType type = mapping.getType();
		return String.format(mapping.getName_suffix(), entityName) + type.getFile_suffix();
	}

	private String gainPackagePath(String parent, String childPackage) {
		String path = parent + StringPool.DOT + childPackage;
		return path.replaceAll("\\" + StringPool.DOT, StringPool.SLASH);
	}
}
