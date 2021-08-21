package com.tao.common.core.utils;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.validation.constraints.NotNull;

import com.tao.common.core.common.exception.ExceptionWapper;
import com.tao.common.core.common.local.LocalManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;


/**
 * 资源工具类
 * 
 * @author hangwen
 * 
 */
public final class ResourceUtils {

	private static Logger log = LoggerFactory.getLogger(ResourceUtils.class);

	/**
	 * 获取指定key的文本资源，找不到会抛出异常
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return getValue(key, true);
	}

	/**
	 * 获取指定key的文本资源，附加参数，找不到会抛出异常
	 * 
	 * @param key
	 * @param paras
	 * @return
	 */
	public static String get(String key, Object... paras) {
		return MessageFormat.format(get(key), paras);
	}

	public static Image getRemoteImage(String urlPath) {
		try {
			ImageIcon imageIcon = getRemoteImageIcon(urlPath);
			if (null != imageIcon) {
				return imageIcon.getImage();
			}
			return null;
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public static ImageIcon getRemoteImageIcon(String urlPath) {
		URL url = null;

		try {
			url = new URL(urlPath);
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
			return null;
		}

		return new ImageIcon(url);
	}

	/**
	 * 获取指定key的文本资源
	 * 
	 * @param key
	 * @param throwExp 找不到的话是否需要抛出异常
	 * @return
	 */
	public static String getValue(String key, boolean throwExp) {
		String local = LocalManager.getCurrentLocal();
		return getValue(local, key, throwExp);
	}

	/**
	 * 初始化调用,加载文本资源
	 * 
	 * @param resourceList
	 */
	public synchronized static void load(List<Resource> resourceList) {
		if (isLoaded) {
			return;
		}

		for (Resource resource : resourceList) {
			if (null == resource) {
				continue;
			}
			String filename = resource.getFilename();
			if (null == filename || StringUtil.isEmptyOrNull(resource.getFilename())) {
				continue;
			}
			String local = getResourceLocal(filename);

			Map<String, String> map = MAP_RESOURCE.get(local);
			if (map == null) {
				map = new HashMap<String, String>();
				MAP_RESOURCE.put(local, map);

				if (local.equals(LocalManager.DefaultLocal.getLocal())) {
					DefaultLocalMap = map;
				}
			}

			Document doc = loadXmlDocument(resource);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> nodes = root.elements("item");
			for (Element node : nodes) {
				String key = node.attributeValue("key");
				map.put(key, node.getText().trim());
			}
		}

		isLoaded = true;
	}

	private static String getResourceLocal(@NotNull String filename) {
		String[] split = filename.replaceAll("\\.xml", "").split("\\.");
		if (split.length == 2) {
			return split[1];
			// return new
			// StringBuffer().append(split[1]).append("_").append(split[2]).toString();
		}

		return LocalManager.DefaultLocal.getLocal();
	}

	private static String getValue(String local, String key, boolean throwExp) {
		Map<String, String> loacalMap = MAP_RESOURCE.get(local);

		if (loacalMap == null) {
			loacalMap = DefaultLocalMap;
		}

		if (loacalMap == null) {
			return null;
		}

		String value = loacalMap.get(key);
		if (StringUtil.isEmptyOrNull(value) && loacalMap != DefaultLocalMap) {
			value = DefaultLocalMap.get(key);
		}

		if (StringUtil.isEmptyOrNull(value) && throwExp) {
			throw ExceptionWapper.createBapRunTimeException("ResourceUtils.getValue", "key:{0} 不存在!", key);
		}

		return value == null ? "" : value;
	}

	private static Document loadXmlDocument(Resource resource) {
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("UTF-8");

		Document doc = null;
		try {
			doc = saxReader.read(resource.getURL());
		} catch (DocumentException e) {
			throw ExceptionWapper.createBapException(e);
		} catch (IOException e) {
			throw ExceptionWapper.createBapException(e);
		}

		return doc;
	}

	private final static Map<String, Map<String, String>> MAP_RESOURCE = new HashMap<String, Map<String, String>>();
	private static Map<String, String> DefaultLocalMap = null;

	private static boolean isLoaded = false;

}