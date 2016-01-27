package loon.action.avg.drama;

import loon.utils.ArrayMap;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class RocFunctions {

	// 系统函数
	final TArray<String> _system_functs = new TArray<String>(10);

	private ArrayMap _rocFunctions = new ArrayMap(10);

	public RocFunctions() {
		reset();
	}

	public void reset() {
		_system_functs.clear();
		_system_functs.add("trim");
		_system_functs.add("rtrim");
		_system_functs.add("isnumber");
		_system_functs.add("ischinese");
		_system_functs.add("indexof");
	}

	/**
	 * 添加自定义函数到脚本中
	 * 
	 * @param name
	 * @param rfunction
	 */
	public void add(String name, IRocFunction rfunction) {
		if (!StringUtils.isEmpty(name)) {
			String funName = name.trim().toLowerCase();
			_system_functs.add(funName);
			_rocFunctions.put(funName, rfunction);
		}
	}
	
	public void remove(String name){
		if (!StringUtils.isEmpty(name)) {
			String funName = name.trim().toLowerCase();
			_system_functs.remove(funName);
			_rocFunctions.remove(funName);
		}
	}

	public Object getValue(String name, String value) {
		if (name == null) {
			return value;
		}
		String key = name.trim().toLowerCase();
		if ("trim".equals(key)) {
			if (value.indexOf(",") == -1) {
				return StringUtils.trim(value);
			}
		} else if ("rtrim".equals(key)) {
			if (value.indexOf(",") == -1) {
				return StringUtils.rtrim(value);
			}
		} else if ("isnan".equals(key)) {
			if (value.indexOf(",") == -1) {
				return MathUtils.isNan(value);
			}
		} else if ("ischinese".equals(key)) {
			if (value.indexOf(",") == -1) {
				return StringUtils.isChinaLanguage(value.toCharArray());
			}
		} else if ("indexof".equals(key)) {
			if (value.indexOf(",") != -1) {
				String[] split = StringUtils.split(value, ',');
				if (split.length == 2) {
					return split[0].indexOf(split[1]) != -1;
				}
			}
		}
		for (int i = 0; i < _rocFunctions.size(); i++) {
			IRocFunction roc = (IRocFunction) _rocFunctions.get(i);
			if (roc != null) {
				Object o = roc.call(name, value);
				if (o != null) {
					return o;
				}
			}
		}
		return "unkown";
	}

}
