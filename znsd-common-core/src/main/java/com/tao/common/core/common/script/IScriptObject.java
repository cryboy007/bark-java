package com.tao.common.core.common.script;

import javax.script.CompiledScript;

public interface IScriptObject {
	String getScriptExpression();

	CompiledScript getCompiledScript();
}
