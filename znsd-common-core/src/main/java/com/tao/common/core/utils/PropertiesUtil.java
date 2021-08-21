package com.tao.common.core.utils;

import com.tao.common.core.common.exception.ExceptionWapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;


import java.util.Properties;

/**
 * 属性文件工具类
 * 
 * @author hangwen
 *
 */
public class PropertiesUtil {
	public static Properties load(InputStream inputStream) {
		return load(new Properties(), inputStream);
	}

	public static Properties load(Properties props, InputStream inputStream) {
		try {
			if (inputStream != null) {
				props.load(inputStream);
			}

			return props;
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public static Properties load(String... propertiesPaths) {
		Properties props = new Properties();

		for (String propertiesPath : propertiesPaths) {
			if (!propertiesPath.startsWith("/")) {
				propertiesPath = "/" + propertiesPath;
			}

			InputStream inputStream = Properties.class.getResourceAsStream(propertiesPath);
			props = load(props, inputStream);
		}

		return props;

	}

	/**
	 * 更新属性文件
	 * 
	 * @param filePath
	 * @param properties
	 */
	public static void updatePropertiesFile(String filePath, Map<String, String> properties) {
		Properties pro = new Properties();


		try(	FileInputStream in = new FileInputStream(filePath); FileOutputStream out = new FileOutputStream(filePath)) {

			pro.load(in);

			for (Entry<String, String> propValue : properties.entrySet()) {
				if (propValue == null || propValue.getValue() == null) {
					continue;
				}
				pro.setProperty(propValue.getKey(), propValue.getValue());
			}

			pro.store(out, null);
		} catch (FileNotFoundException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}
	}

}
