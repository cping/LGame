package loon.action.avg.drama;

import loon.LSystem;
import loon.utils.ArrayMap;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class RocFunctions {

	public final static int JUMP_TYPE = -443;

	// 系统函数
	protected final TArray<String> _system_functs = new TArray<String>();

	private final ArrayMap _rocFunctions = new ArrayMap();

	public RocFunctions() {
		reset();
	}

	public RocFunctions reset() {
		_system_functs.clear();
		_system_functs.add("trim");
		_system_functs.add("rtrim");
		_system_functs.add("isnumber");
		_system_functs.add("ischinese");
		_system_functs.add("isjanpanse");
		_system_functs.add("iskorean");
		_system_functs.add("indexof");
		_system_functs.add("jump");
		return this;
	}

	/**
	 * 添加自定义函数到脚本中
	 * 
	 * @param name
	 * @param rfunction
	 * @return
	 */
	public RocFunctions add(String name, IRocFunction rfunction) {
		if (!StringUtils.isEmpty(name)) {
			String funName = name.trim().toLowerCase();
			_system_functs.add(funName);
			_rocFunctions.put(funName, rfunction);
		}
		return this;
	}

	public RocFunctions remove(String name) {
		if (!StringUtils.isEmpty(name)) {
			String funName = name.trim().toLowerCase();
			_system_functs.remove(funName);
			_rocFunctions.remove(funName);
		}
		return this;
	}

	public Object getValue(RocScript script, String name, String value) {
		if (name == null) {
			return value;
		}
		String key = name.trim().toLowerCase();
		if ("trim".equals(key)) {
			if (value.indexOf(LSystem.COMMA) == -1) {
				return StringUtils.trim(value);
			}
		} else if ("rtrim".equals(key)) {
			if (value.indexOf(LSystem.COMMA) == -1) {
				return StringUtils.rtrim(value);
			}
		} else if ("isnan".equals(key)) {
			if (value.indexOf(LSystem.COMMA) == -1) {
				return MathUtils.isNan(value);
			}
		} else if ("ischinese".equals(key)) {
			if (value.indexOf(LSystem.COMMA) == -1) {
				return StringUtils.isChinaLanguage(value);
			}
		} else if ("isjapanese".equals(key)) {
			if (value.indexOf(LSystem.COMMA) == -1) {
				return StringUtils.isJapanLanguage(value);
			}
		} else if ("iskorean".equals(key)) {
			if (value.indexOf(LSystem.COMMA) == -1) {
				return StringUtils.isKoreanLanguage(value);
			}
		} else if ("indexof".equals(key)) {
			if (value.indexOf(LSystem.COMMA) != -1) {
				String[] split = StringUtils.split(value, LSystem.COMMA);
				if (split.length == 2) {
					return split[0].indexOf(split[1]) != -1;
				}
			}
		} else if ("jump".equals(key)) {
			script._sleep = JUMP_TYPE;
		}
		IRocFunction roc = (IRocFunction) _rocFunctions.get(key);
		if (roc != null) {
			String[] args = StringUtils.split(value, LSystem.COMMA);
			Object o = roc.call(args);
			if (o != null) {
				return o;
			}
		}
		return name;
	}

}
