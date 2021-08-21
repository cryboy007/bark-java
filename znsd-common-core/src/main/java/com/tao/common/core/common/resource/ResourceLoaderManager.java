package com.tao.common.core.common.resource;

import com.baison.e3plus.common.bscore.linq.ISelector;
import com.baison.e3plus.common.bscore.linq.LinqUtil;
import com.baison.e3plus.common.bscore.other.ServiceUtils;
import com.baison.e3plus.common.bscore.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 扫描本地资源 配合ISupportResource接口使用 具体参考ISupportResource的各个函数说明
 * 
 * @author hangwen
 * 
 */
public class ResourceLoaderManager {

	public static void load() {
		load(new String[0]);
	}

	public static void load(ISupportResourceLoad... supportResources) {
		ResourceLoaderManager loder = new ResourceLoaderManager(Arrays.asList(supportResources));
		loder.loadFile();
	}

	public static void load(String... beanNames) {
		List<ISupportResourceLoad> supportResources = null;

		if (beanNames == null || beanNames.length == 0) {
			supportResources = ServiceUtils.getBeans(ISupportResourceLoad.class);
		} else {
			supportResources = LinqUtil.select(Arrays.asList(beanNames), new ISelector<String, ISupportResourceLoad>() {

				@Override
				public ISupportResourceLoad select(String beanName) {
					return null;
				}
			});
		}

		Collections.sort(supportResources, new Comparator<ISupportResourceLoad>() {

			@Override
			public int compare(ISupportResourceLoad o1, ISupportResourceLoad o2) {
				return o1.getPriority().compareTo(o2.getPriority());
			}
		});

		ResourceLoaderManager loder = new ResourceLoaderManager(supportResources);
		loder.loadFile();
	}

	private List<ISupportResourceLoad> supportResources;
	
	private static final Logger log = LoggerFactory.getLogger(ResourceLoaderManager.class);

	public ResourceLoaderManager(List<ISupportResourceLoad> supportResources) {
		this.supportResources = supportResources;
	}

	/**
	 * 开始加载配置文件
	 */
	public void loadFile() {
		// 先临时处理
		for (ISupportResourceLoad iSupportResourceLoad : supportResources) {
			String name = iSupportResourceLoad.getClass().getName();
			log.info(StringUtil.format("{0} starting load……", name));

			iSupportResourceLoad.load();

			log.info(StringUtil.format("{0} loaded", name));
		}
	}
}
