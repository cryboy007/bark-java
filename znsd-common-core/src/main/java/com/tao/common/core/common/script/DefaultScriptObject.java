package com.tao.common.core.common.script;

import javax.script.CompiledScript;

class DefaultScriptObject implements IScriptObject {

	private String scriptExpression;
	private CompiledScript compiledScript;

	public DefaultScriptObject(String scriptExpression, CompiledScript compiledScript) {
		this.scriptExpression = scriptExpression;
		this.compiledScript = compiledScript;
	}

	@Override
	public String getScriptExpression() {
		return scriptExpression;
	}

	@Override
	public CompiledScript getCompiledScript() {
		return compiledScript;
	}

}
