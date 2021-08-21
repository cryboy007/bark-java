package com.tao.common.core.common.local;


import com.tao.common.core.utils.StringUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalManager {

	public final static BS2Local zh_CN = new BS2Local(java.util.Locale.SIMPLIFIED_CHINESE.toString(), "简体中文", 0, true);
	public final static BS2Local zh_TW = new BS2Local(java.util.Locale.TRADITIONAL_CHINESE.toString(), "繁體中文", 1);
	public final static BS2Local en_US = new BS2Local(java.util.Locale.US.toString(), "English", 2);
	public final static BS2Local other = new BS2Local("other", "Other", 3);

	public final static BS2Local DefaultLocal = zh_CN;

	private static Map<String, BS2Local> locals = new LinkedHashMap<String, BS2Local>();

	private static final ThreadLocal<String> localThreadLocal = new ThreadLocal<String>();

	static {
		locals.put(zh_CN.toString(), zh_CN);
		locals.put(zh_TW.toString(), zh_TW);
		locals.put(en_US.toString(), en_US);
		locals.put(other.toString(), other);
	}

	public static BS2Local getLocal(String local) {
		return locals.get(local);
	}

	public static Collection<BS2Local> getLocals() {
		return locals.values();
	}

	public static void setCurrentLocal(String local) {
		localThreadLocal.set(local);
	}

	public static String getCurrentLocal() {
		String local = localThreadLocal.get();
		return StringUtil.isEmptyOrNull(local) ? DefaultLocal.getLocal() : local;
	}
	
	public static void initThreadCache() {
		
		localThreadLocal.remove();
		
	}
	
}
