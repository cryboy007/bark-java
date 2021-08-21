package com.tao.common.core.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.cglib.beans.BeanMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Map工具类
 * 持续完善
 * @author feng.leng
 *
 */
public class MapUtil {

	/**
     * 将对象装换为map
     *
     * @param bean
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * 将map装换为javabean对象
     *
     * @param map
     * @param bean
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean) {
        BeanMap beanMap = BeanMap.create(bean);
        for(Entry<String, Object> entry:map.entrySet()) {
        	Class propertyType = beanMap.getPropertyType(entry.getKey());
        	if (propertyType == null){
        	    continue;
            }
        	if(propertyType.equals(BigDecimal.class)) {
        		beanMap.put(entry.getKey(), BigDecimal.valueOf(Double.valueOf(entry.getValue().toString())));
        	}else if(propertyType.equals(Long.class)){
        		beanMap.put(entry.getKey(), Long.valueOf(entry.getValue().toString()));
        	}else {
        		beanMap.put(entry.getKey(), entry.getValue());
        	}
        }
        
        return bean;
    }

    /**
     * 将List<T>转换为List<Map<String, Object>>
     *
     * @param objList
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
        List<Map<String, Object>> list = Lists.newArrayList();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0, size = objList.size(); i < size; i++) {
                bean = objList.get(i);
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 将List<Map<String,Object>>转换为List<T>;
     * 将带有下划线key，改编为驼峰式key;
     *
     * @param maps
     * @param clazz
     * @return
     * @throws Exception 
     */
    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps, Class<T> clazz)
            throws Exception {
        List<T> list = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(maps)) {
            Map<String, Object> map = null;
            for (int i = 0, size = maps.size(); i < size; i++) {
                map = maps.get(i);
                list.add(mapToMapToObject(map, clazz));
            }
        }
        return list;
    }
    
    /**
	 *	map转map转对象,将带有下划线key，改编为驼峰式key
	 *  @return Map<?,?>
	 *  @param map
	 *  @return
	 * @throws Exception 
	 *	
	 */
	public static  <T> T mapToMapToObject(Map<String, Object> map,Class<T> beanClass) throws Exception{
		
		Map<String, Object> mapp=new HashMap<>();
		//map遍历
		 for (Entry<?, ?> entry : map.entrySet()) {
	           //处理下划线
	            if(entry.getKey().toString().contains("_")){
	            	Object value = entry.getValue();
	            	String[] arr = entry.getKey().toString().split("_");
	            	StringBuffer sb = new StringBuffer();
	            	sb.append(arr[0]);
	            	for (int i=1;i<arr.length;i++){
	            		arr[i]=arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);	 
	            	//合并字符串
	            		sb.append(arr[i]);
	            	}
	            	String key = sb.toString();
	            	mapp.put(key, value);
	            }else {
	            	//不做处理部分
	            	String key = entry.getKey().toString();
	            	Object value = entry.getValue();
	            	mapp.put(key, value);
				}
	        }
		 
		 T bean = beanClass.newInstance();
		 return mapToBean(mapp, bean);
	}
    
}
