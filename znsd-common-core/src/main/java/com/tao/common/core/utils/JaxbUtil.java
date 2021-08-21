package com.tao.common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

public class JaxbUtil {
	private static Logger log = LoggerFactory.getLogger(JaxbUtil.class);
	/**
	 * JavaBean转换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @param writer
	 * @return
	 */
	public static String convertToXml(Object obj) {
		return convertToXml(obj, "UTF-8");
	}

	/**
	 * JavaBean转换成xml 默认编码UTF-8
	 * 
	 * @param obj
	 * @param writer
	 * @return
	 */
	public static String convertToXml(String groupName, List<?> objs) {
		return convertToXml(groupName, objs, "utf-8");
	}

	/**
	 * JavaBean转换成xml 默认编码UTF-8
	 * @param objs
	 * @return
	 */
	public static String convertToXml(List<?> objs) {
		return convertToXml(objs, "utf-8");
	}
	/**
	 * JavaBean转换成xml
	 * @param objs
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(List<?> objs, String encoding) {
		String result = StringUtil.format("<?xml version=\"1.0\" encoding=\"{0}\" ?>\n", encoding);
		try {
			for (Object obj : objs) {
				JAXBContext context = JAXBContext.newInstance(obj.getClass());
				Marshaller marshaller = context.createMarshaller();
				// 决定是否在转换成xml时同时进行格式化（即按标签自动换行，否则即是一行的xml）
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				// xml的编码方式
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
				// 是否省略xml头信息
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

				StringWriter writer = new StringWriter();
				marshaller.marshal(obj, writer);
				result = result + writer.toString() + "\n";
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		return result;
	}
	/**
	 * JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			// 决定是否在转换成xml时同时进行格式化（即按标签自动换行，否则即是一行的xml）
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// xml的编码方式
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			// 是否省略xml头信息
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		return result;
	}

	/**
	 * xml转换成JavaBean
	 * 
	 * @param xml
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T converyToJavaBean(String xml, Class<T> c) {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		return t;
	}

	public static String convertToXml(String groupName, List<?> objs, String encoding) {
		return convertToXml(groupName, objs, encoding, true);
	}
	
	/**
	 * JavaBean转换成xml
	 * 
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(String groupName, List<?> objs, String encoding, boolean isFormat) {
		String changeLine = "\n";
		if (!isFormat) {
			changeLine = "";
		}
		String result = StringUtil.format(
				"<?xml version=\"1.0\" encoding=\"{0}\" ?>" + changeLine + "<" + groupName + ">" + changeLine,
				encoding);
		try {
			for (Object obj : objs) {
				JAXBContext context = JAXBContext.newInstance(obj.getClass());
				Marshaller marshaller = context.createMarshaller();
				// 决定是否在转换成xml时同时进行格式化（即按标签自动换行，否则即是一行的xml）
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, isFormat);
				// xml的编码方式
				marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
				// 是否省略xml头信息
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

				StringWriter writer = new StringWriter();
				marshaller.marshal(obj, writer);
				result = result + writer.toString() + changeLine;
			}
			result = result + "</" + groupName + ">";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

		return result;
	}
}
