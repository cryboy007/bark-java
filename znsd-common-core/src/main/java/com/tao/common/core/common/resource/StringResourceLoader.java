package com.tao.common.core.common.resource;

import com.tao.common.core.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class StringResourceLoader implements ISupportResourceLoad {

	private static final Logger log = LoggerFactory.getLogger(StringResourceLoader.class);

	@Override
	public Integer getPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void load() {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] resources = resolver.getResources("classpath*:com/tao/**/*resource*.xml");

			for (Resource resource : resources) {
				log.info(resource.getDescription(), resource.getURL());
			}

			ResourceUtils.load(Arrays.asList(resources));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

}
