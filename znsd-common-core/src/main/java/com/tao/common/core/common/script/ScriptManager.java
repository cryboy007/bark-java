package com.tao.common.core.common.script;

import com.tao.common.core.common.exception.BAPException;
import com.tao.common.core.common.exception.ExceptionWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.util.Map;

public class ScriptManager {

	public static final String JRUBY = "jruby";

	public static final String JYTHON = "python";

	public static final String GROOVY = "groovy";

	private static ScriptEngineManager manager;
	
	private static final Logger log = LoggerFactory.getLogger(ScriptManager.class);

	static {
		manager = new ScriptEngineManager();
	}

	public static IScriptObject createScriptObject(String scriptExpression, CompiledScript compiledScript) {
		return new DefaultScriptObject(scriptExpression, compiledScript);
	}

	public static ScriptEngineManager getScriptEngineManager() {
		return manager;
	}

	public static CompiledScript compile(String scriptType, String script) {
		ScriptEngine engine = manager.getEngineByName(scriptType);
		Compilable compilingEngine = (Compilable) engine;

		try {
			return compilingEngine.compile(script);
		} catch (ScriptException e) {
			BAPException ex = ExceptionWapper.createBapException("bs2.common.ScriptManager.compile-001", e, script);

			log.error(ex.getMessage(), ex);

			throw ex;
		}
	}

	public static Object eval(String scriptType, String script, Map<String, Object> bindings) {
		ScriptEngine engine = manager.getEngineByName(scriptType);

		try {
			return engine.eval(script, new SimpleBindings(bindings));
		} catch (ScriptException e) {
			BAPException ex = ExceptionWapper.createBapException("bs2.common.ScriptManager.eval-001", e,
					e.getMessage(), script, bindings);

			log.error(ex.getMessage(), ex);

			throw ex;
		}
	}

	public static Object eval(IScriptObject scriptObject, Map<String, Object> bindings) {
		try {
			return scriptObject.getCompiledScript().eval(new SimpleBindings(bindings));
		} catch (ScriptException e) {
			BAPException ex = ExceptionWapper.createBapException("bs2.common.ScriptManager.eval-001", e,
					e.getMessage(), scriptObject.getScriptExpression(), bindings);

			log.error(ex.getMessage(), ex);

			throw ex;
		}
	}

	public static Object eval(CompiledScript compiledScript, Bindings bindings) {
		try {
			return compiledScript.eval(bindings);
		} catch (ScriptException e) {
			throw ExceptionWapper.createBapRunTimeException("ScriptManager.eval-001", "compiledScript eval error:{0}",
					e.getMessage());
		}
	}

	public static Object evalWithGroovy(String script, Map<String, Object> bindings) {
		return eval(GROOVY, script, bindings);
	}

	public static CompiledScript compileWithGroovy(String script) {
		return compile(GROOVY, script);
	}
}
