/**
 * Copyright 2008 - 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email javachenpeng@yahoo.com
 * @version 0.1.2
 */
package loon.action.avg.drama;

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.Session;
import loon.utils.ArrayMap;
import loon.utils.ArrayMap.Entry;
import loon.utils.CollectionUtils;
import loon.utils.HelperUtils;
import loon.utils.MathUtils;
import loon.utils.StrBuilder;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.parse.StrTokenizer;
import loon.utils.res.TextResource;

/**
 * 一个非常简单的脚本解释器,用来跨平台实现avg游戏脚本解析,以统一Loon内部的简单脚本格式,
 * 同时避免一些第三方框架的跨平台问题,更复杂的脚本需求请使用 @see RocScript
 */
public class Command extends Conversion implements LRelease {

	// 脚本缓存
	private static ArrayMap _scriptLazy;

	// 脚本数据缓存
	private static ArrayMap _scriptContext;

	// 函数列表
	private static ArrayMap _functions;

	// 变量列表
	private static ArrayMap _setEnvironmentList;

	// 条件分支列表
	private static ArrayMap _conditionEnvironmentList;

	// 读入连续数据
	private StrBuilder _readBuffer;

	// 缓存脚本名
	private String _cacheCommandName;

	// 注释标记中
	private boolean _flaging = false;

	// 判断标记中
	private boolean _ifing = false;

	// 函数标记中
	private boolean _functioning = false;

	// 分支标记
	private boolean _elseflag = false;

	private boolean _esleover = false;

	private boolean _backIfBool = false;

	private boolean _isClose;

	private String _executeCommand = null;

	private String _nowPosFlagName = null;

	private boolean _addCommand;

	private boolean _isInnerCommand;

	private boolean _isRead;

	private boolean _isCall;

	private boolean _isCache;

	private boolean _if_bool;

	private boolean _elseif_bool;

	private Command _innerCommand;

	private TArray<String> _temps;

	private TArray<String> _printTags;

	private TArray<String> _randTags;

	private int _scriptSize;

	private int _offsetPos;

	// 脚本数据列表
	private String[] _scriptList;

	// 脚本名
	private String _scriptName;

	/**
	 * 构造函数，载入指定脚本文件
	 * 
	 * @param fileName
	 */
	public Command(final String fileName) {
		createCache(false);
		formatCommand(fileName);
	}

	/**
	 * 构造函数，载入指定list脚本
	 * 
	 * @param resource
	 */
	public Command(final String fileName, final String[] res) {
		createCache(false);
		formatCommand("function", res);
		_scriptName = fileName;
	}

	public static void createCache(final boolean free) {
		if (free) {
			if (_scriptContext == null) {
				_scriptContext = new ArrayMap(1000);
			} else {
				_scriptContext.clear();
			}
			if (_functions == null) {
				_functions = new ArrayMap(20);
			} else {
				_functions.clear();
			}
			if (_setEnvironmentList == null) {
				_setEnvironmentList = new ArrayMap(20);
			} else {
				_setEnvironmentList.clear();
			}
			if (_conditionEnvironmentList == null) {
				_conditionEnvironmentList = new ArrayMap(30);
			} else {
				_conditionEnvironmentList.clear();
			}
		} else {
			if (_scriptContext == null) {
				_scriptContext = new ArrayMap(1000);
			}
			if (_functions == null) {
				_functions = new ArrayMap(20);
			}
			if (_setEnvironmentList == null) {
				_setEnvironmentList = new ArrayMap(20);
			}
			if (_conditionEnvironmentList == null) {
				_conditionEnvironmentList = new ArrayMap(30);
			}
		}
	}

	public Command formatCommand(final String fileName) {
		return formatCommand(fileName, Command.includeFile(fileName));
	}

	public Command formatCommand(final CommandLink cmd) {
		String context = cmd.getValue();
		String key = "key" + context.length() + context.charAt(0) + LSystem.DOT + context.charAt(context.length() - 1);
		return formatCommand(key, Command.includeString(key, context));
	}

	public Command formatCommand(final String name, final String[] res) {
		if (res == null || res.length == 0) {
			return this;
		}
		if (!"function".equalsIgnoreCase(name)) {
			if (_functions != null) {
				_functions.clear();
			}
		}
		if (_conditionEnvironmentList != null) {
			_conditionEnvironmentList.clear();
		}
		if (_setEnvironmentList != null) {
			_setEnvironmentList.put(V_SELECT_KEY, "-1");
		}
		if (_readBuffer == null) {
			_readBuffer = new StrBuilder(256);
		} else {
			_readBuffer.setLength(0);
		}
		this._scriptName = name;
		this._scriptList = res;
		this._scriptSize = res.length;
		this._offsetPos = 0;
		this._flaging = false;
		this._ifing = false;
		this._isCache = true;
		this._elseflag = false;
		this._backIfBool = false;
		this._functioning = false;
		this._esleover = false;
		this._backIfBool = false;
		this._addCommand = false;
		this._isInnerCommand = false;
		this._isRead = false;
		this._isCall = false;
		this._isCache = false;
		this._if_bool = false;
		this._elseif_bool = false;
		return this;
	}

	private void setDefaultIF(final boolean flag) {
		_conditionEnvironmentList.put(_nowPosFlagName, flag);
		_esleover = _elseflag = flag;
		_addCommand = false;
	}

	private boolean setupIF(final String commandString, final String nowPosFlagName, final ArrayMap setEnvironmentList,
			final ArrayMap conditionEnvironmentList) {
		boolean result = false;
		conditionEnvironmentList.put(nowPosFlagName, result);
		try {
			final TArray<String> temps = commandSplit(commandString);
			int size = temps.size;
			Object valueA = null;
			Object valueB = null;
			String condition = null;
			if (size <= 4) {
				valueA = temps.get(1);
				valueB = temps.get(3);
				valueA = setEnvironmentList.get(valueA) == null ? valueA : setEnvironmentList.get(valueA);
				valueB = setEnvironmentList.get(valueB) == null ? valueB : setEnvironmentList.get(valueB);
				condition = temps.get(2);
			} else {
				int count = 0;
				final StrBuilder sbr = new StrBuilder();
				for (int i = 0; i < temps.size; i++) {
					String res = temps.get(i);
					if (count > 0) {
						if (!isCondition(res)) {
							sbr.append(res);
						} else {
							valueA = sbr.toString();
							valueA = String.valueOf(exp.parse(valueA));
							sbr.setLength(0);
							condition = res;
						}
					}
					count++;
				}
				valueB = sbr.toString();
			}
			// 非纯数字
			if (!MathUtils.isNan((String) valueB)) {
				try {
					// 尝试四则运算公式匹配
					valueB = exp.parse(valueB);
				} catch (Throwable e) {
				}
			}
			// 无法判定
			if (valueA == null || valueB == null) {
				conditionEnvironmentList.put(nowPosFlagName, result);
			} else {
				final Object conditionResult = HelperUtils.eval(condition, valueA, valueB).get();
				if (conditionResult != null) {
					if (conditionResult instanceof Boolean) {
						conditionEnvironmentList.put(nowPosFlagName,
								result = ((Boolean) conditionResult).booleanValue());
					} else {
						final String v = HelperUtils.toStr(conditionResult);
						conditionEnvironmentList.put(nowPosFlagName, result = StringUtils.toBoolean(v));
					}
				} else {
					conditionEnvironmentList.put(nowPosFlagName, result);
				}
			}
		} catch (Throwable ex) {
			LSystem.error("Command parse exception", ex);
		}
		_esleover = _elseflag = result;
		_addCommand = false;
		return result;
	}

	/**
	 * 打开脚本缓存
	 * 
	 */
	public void openCache() {
		_isCache = true;
	}

	/**
	 * 关闭脚本缓存
	 * 
	 */
	public void closeCache() {
		_isCache = false;
	}

	/**
	 * 当前脚本行缓存名
	 * 
	 * @return
	 */
	public String nowCacheOffsetName(final String cmd) {
		return (_scriptName + FLAG + _offsetPos + FLAG + cmd).toLowerCase();
	}

	/**
	 * 重启脚本缓存
	 * 
	 */
	public static void resetCache() {
		if (_scriptContext != null) {
			_scriptContext.clear();
		}
	}

	public boolean isRead() {
		return _isRead;
	}

	public Command setRead(boolean r) {
		this._isRead = r;
		return this;
	}

	/**
	 * 返回当前的读入数据集合
	 * 
	 * @return
	 */
	public String[] getReads() {
		final String result = StringUtils.replace(_readBuffer.toString(), SELECTS_TAG, LSystem.EMPTY);
		return StringUtils.split(result, FLAG_CHAR);
	}

	/**
	 * 返回指定索引的读入数据
	 * 
	 * @param index
	 * @return
	 */
	public String getRead(int index) {
		try {
			return getReads()[index];
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 截取第一次出现的指定标记
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static String getNameTag(final String messages, final String startString, final String endString) {
		TArray<String> results = getNameTags(messages, startString, endString);
		return (results == null || results.size == 0) ? null : results.get(0);
	}

	/**
	 * 截取指定标记内容为list
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static TArray<String> getNameTags(final String messages, final String startString, final String endString) {
		return Command.getNameTags(messages.toCharArray(), startString.toCharArray(), endString.toCharArray());
	}

	/**
	 * 截取指定标记内容为list
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static TArray<String> getNameTags(final char[] messages, final char[] startString, final char[] endString) {
		int dlength = messages.length;
		int slength = startString.length;
		int elength = endString.length;
		TArray<String> tagList = new TArray<String>(10);
		boolean lookup = false;
		int lookupStartIndex = 0;
		int lookupEndIndex = 0;
		int length;
		StrBuilder sbr = new StrBuilder(100);
		for (int i = 0; i < dlength; i++) {
			char tag = messages[i];
			if (tag == startString[lookupStartIndex]) {
				lookupStartIndex++;
			}
			if (lookupStartIndex == slength) {
				lookupStartIndex = 0;
				lookup = true;
			}
			if (lookup) {
				sbr.append(tag);
			}
			if (tag == endString[lookupEndIndex]) {
				lookupEndIndex++;
			}
			if (lookupEndIndex == elength) {
				lookupEndIndex = 0;
				lookup = false;
				length = sbr.length();
				if (length > 0) {
					tagList.add(sbr.substring(1, sbr.length() - elength));
					sbr.setLength(0);
				}
			}
		}
		return tagList;
	}

	/**
	 * 注入选择变量
	 * 
	 * @param type
	 */
	public Command select(int type) {
		if (_innerCommand != null) {
			_innerCommand.setVariable(V_SELECT_KEY, String.valueOf(type));
		}
		return setVariable(V_SELECT_KEY, String.valueOf(type));
	}

	public String getSelect() {
		return (String) getVariable(V_SELECT_KEY);
	}

	/**
	 * 插入变量
	 * 
	 * @param key
	 * @param v
	 */
	public Command setVariable(final String key, final Object vl) {
		_setEnvironmentList.put(key, vl);
		return this;
	}

	/**
	 * 插入变量集合
	 * 
	 * @param vars
	 */
	public Command setVariables(final ArrayMap vars) {
		_setEnvironmentList.putAll(vars);
		return this;
	}

	/**
	 * 返回变量集合
	 * 
	 * @return
	 */
	public ArrayMap getVariables() {
		return _setEnvironmentList;
	}

	public Object getVariable(final String key) {
		return _setEnvironmentList.get(key);
	}

	/**
	 * 删除变量
	 * 
	 * @param key
	 */
	public void removeVariable(final String key) {
		_setEnvironmentList.remove(key);
	}

	/**
	 * 判定脚本是否允许继续解析
	 * 
	 * @return
	 */
	public boolean next() {
		return (_offsetPos < _scriptSize);
	}

	/**
	 * 跳转向指定索引位置
	 * 
	 * @param offset
	 * @return
	 */
	public boolean gotoIndex(final int offset) {
		boolean result = offset < _scriptSize && offset > -1;
		if (result) {
			_offsetPos = offset;
		}
		return result;
	}

	/**
	 * 跳转向指定索引位置
	 * 
	 * @param gotoFlag
	 * @return
	 */
	public boolean gotoIndex(final String gotoFlag) {
		int idx = -1;
		for (int i = 0; i < _scriptSize; i++) {
			final String line = _scriptList[i];
			final Object varName = _setEnvironmentList.get(line);
			if (line.equals(gotoFlag)) {
				idx = i;
				break;
			} else if (varName != null && gotoFlag.equals((String) varName)) {
				idx = i;
				break;
			}
		}
		if (idx != -1) {
			_offsetPos = idx;
		}
		return idx == -1;

	}

	public int getIndex() {
		return _offsetPos;
	}

	/**
	 * 批处理执行脚本，并返回可用list结果
	 * 
	 * @return
	 */
	public TArray<String> batchToList() {
		final TArray<String> reslist = new TArray<String>(_scriptSize);
		for (; next();) {
			final String execute = doExecute();
			if (execute != null) {
				reslist.add(execute);
			}
		}
		return reslist;
	}

	/**
	 * 批处理执行脚本，并返回可用string结果
	 * 
	 * @return
	 */
	public String batchToString() {
		final StrBuilder resString = new StrBuilder(_scriptSize * 10);
		for (; next();) {
			final String execute = doExecute();
			if (execute != null) {
				resString.append(execute);
				resString.append(LSystem.LF);
			}
		}
		return resString.toString();
	}

	private void setupSET(final String cmd) {
		if (cmd.startsWith(SET_TAG)) {
			final TArray<String> temps = commandSplit(cmd);
			final int len = temps.size;
			String result = null;
			if (len == 4) {
				result = temps.get(3).toString();
			} else if (len > 4) {
				StrBuilder sbr = new StrBuilder(len);
				for (int i = 3; i < temps.size; i++) {
					sbr.append(temps.get(i));
				}
				result = sbr.toString();
			}
			if (result != null) {
				// 替换已有变量字符
				for (int i = 0; i < _setEnvironmentList.size(); i++) {
					Entry entry = _setEnvironmentList.getEntry(i);
					if (!(StringUtils.startsWith(result, LSystem.DOUBLE_QUOTES)
							&& StringUtils.endsWith(result, LSystem.DOUBLE_QUOTES))) {
						result = StringUtils.replaceMatch(result, (String) entry.getKey(), (String) entry.getValue());
					}
				}
				// 当为普通字符串时
				if (StringUtils.startsWith(result, LSystem.DOUBLE_QUOTES)
						&& StringUtils.endsWith(result, LSystem.DOUBLE_QUOTES)) {
					_setEnvironmentList.put(temps.get(1), result.substring(1, result.length() - 1));
				} else if (StringUtils.isChinaLanguage(result) || StringUtils.isEnglishAndNumeric(result)) {
					_setEnvironmentList.put(temps.get(1), result);
				} else {
					// 当为数学表达式时
					_setEnvironmentList.put(temps.get(1), exp.parse(result));
				}
			}
			_addCommand = false;
		}
	}

	/**
	 * 随机数处理
	 * 
	 */
	private void setupRandom(final String cmd) {
		String text = cmd.trim();
		// 随机数判定
		if (text.indexOf(RAND_TAG) != -1) {
			_randTags = Command.getNameTags(text, RAND_TAG + BRACKET_LEFT_TAG, BRACKET_RIGHT_TAG);
			if (_randTags != null) {
				for (int i = 0; i < _randTags.size; i++) {
					String key = _randTags.get(i);
					Object vl = _setEnvironmentList.get(key);
					// 已存在变量
					if (vl != null) {
						text = StringUtils.replaceMatch(text,
								(RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG), vl.toString());
						// 设定有随机数生成范围
					} else if (MathUtils.isNan(key)) {
						text = StringUtils.replaceMatch(text,
								(RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG),
								String.valueOf(GLOBAL_RAND.nextInt(Integer.parseInt(key))));
						// 无设定
					} else {
						text = StringUtils.replaceMatch(text,
								(RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG),
								String.valueOf(GLOBAL_RAND.nextInt()));
					}
				}
			}
		}
	}

	private void innerCallTrue() {
		_isCall = true;
		_isInnerCommand = true;
	}

	private void innerCallFalse() {
		_isCall = false;
		_isInnerCommand = false;
		_innerCommand = null;
	}

	/**
	 * 逐行执行脚本命令
	 * 
	 * @return
	 */
	public String doExecute() {
		if (_isClose) {
			return null;
		}
		this._executeCommand = null;
		this._addCommand = true;
		this._isInnerCommand = (_innerCommand != null);
		this._if_bool = false;
		this._elseif_bool = false;
		try {
			// 执行call命令
			if (_isInnerCommand && _isCall) {
				setVariables(_innerCommand.getVariables());
				if (_innerCommand.next()) {
					return _innerCommand.doExecute();
				} else {
					innerCallFalse();
					return _executeCommand;
				}
				// 执行内部脚本
			} else if (_isInnerCommand && !_isCall) {
				setVariables(_innerCommand.getVariables());
				if (_innerCommand.next()) {
					return _innerCommand.doExecute();
				} else {
					_innerCommand = null;
					_isInnerCommand = false;
					return _executeCommand;
				}
			}

			_nowPosFlagName = String.valueOf(_offsetPos);
			final int length = _conditionEnvironmentList.size();
			if (length > 0) {
				Object ifResult = _conditionEnvironmentList.get(length - 1);
				if (ifResult != null) {
					_backIfBool = ((Boolean) ifResult).booleanValue();
				}
			}

			// 空指向判定
			if (_scriptList == null) {
				resetCache();
				return _executeCommand;
			} else if (_scriptList.length - 1 < _offsetPos) {
				resetCache();
				return _executeCommand;
			}

			// 获得全行命令
			final String cmd = _scriptList[_offsetPos].trim();

			// 清空脚本缓存
			if (cmd.startsWith(RESET_CACHE_TAG)) {
				resetCache();
				return _executeCommand;
			}

			if (_isCache) {
				// 获得缓存命令行名
				_cacheCommandName = nowCacheOffsetName(cmd);
				// 读取缓存的脚本
				Object cache = _scriptContext.get(_cacheCommandName);
				if (cache != null) {
					return (String) cache;
				}
			}

			// 注释中
			if (_flaging) {
				_flaging = !(cmd.startsWith(FLAG_LS_E_TAG) || cmd.endsWith(FLAG_LS_E_TAG));
				return _executeCommand;
			}

			if (!_flaging) {
				// 全局注释
				if (cmd.startsWith(FLAG_LS_B_TAG) && !cmd.endsWith(FLAG_LS_E_TAG)) {
					_flaging = true;
					return _executeCommand;
				} else if (cmd.startsWith(FLAG_LS_B_TAG) && cmd.endsWith(FLAG_LS_E_TAG)) {
					return _executeCommand;
				}
			}

			// 执行随机数标记
			setupRandom(cmd);

			// 执行获取变量标记
			setupSET(cmd);

			// 结束脚本中代码段标记
			if (cmd.endsWith(END_TAG)) {
				_functioning = false;
				return _executeCommand;
			}

			// 标注脚本中代码段标记
			if (cmd.startsWith(BEGIN_TAG)) {
				_temps = commandSplit(cmd);
				if (_temps.size == 2) {
					_functioning = true;
					_functions.put(_temps.get(1), new String[0]);
					return _executeCommand;
				}
			}

			// 开始记录代码段
			if (_functioning) {
				final int size = _functions.size() - 1;
				String[] function = (String[]) _functions.get(size);
				final int index = function.length;
				function = CollectionUtils.expand(function, 1);
				function[index] = cmd;
				_functions.set(size, function);
				return _executeCommand;
			}

			// 执行代码段调用标记
			if (((!_elseflag && !_ifing) || (_elseflag && _ifing)) && cmd.startsWith(CALL_TAG) && !_isCall) {
				_temps = commandSplit(cmd);
				if (_temps.size == 2) {
					final String functionName = _temps.get(1);
					final String[] funs = (String[]) _functions.get(functionName);
					if (funs != null) {
						_innerCommand = new Command(_scriptName + FLAG + functionName, funs);
						_innerCommand.closeCache();
						_innerCommand.setVariables(getVariables());
						innerCallTrue();
						return null;
					}
				}
			}

			if (!_if_bool && !_elseif_bool) {
				// 获得循序结构条件
				_if_bool = cmd.startsWith(IF_TAG);
				_elseif_bool = cmd.startsWith(ELSE_TAG);

			}

			// 条件判断a
			if (_if_bool) {
				setupIF(cmd, _nowPosFlagName, _setEnvironmentList, _conditionEnvironmentList);
				_ifing = true;
				// 条件判断b
			} else if (_elseif_bool) {
				final String[] value = StringUtils.split(cmd, LSystem.SPACE);
				if (!_backIfBool && !_elseflag) {
					// 存在if判断
					if (value.length > 1 && IF_TAG.equals(value[1])) {
						setupIF(StringUtils.replace(cmd, ELSE_TAG, LSystem.EMPTY).trim(), _nowPosFlagName,
								_setEnvironmentList, _conditionEnvironmentList);
						// 单纯的else
					} else if (value.length == 1 && ELSE_TAG.equals(value[0])) {
						if (!_esleover) {
							setDefaultIF(true);
						}
					}
				} else {
					_elseflag = false;
					_addCommand = false;
					_conditionEnvironmentList.put(_nowPosFlagName, Boolean.valueOf(false));

				}
			}

			// 分支结束
			if (cmd.startsWith(IF_END_TAG)) {
				_conditionEnvironmentList.clear();
				_backIfBool = false;
				_addCommand = false;
				_ifing = false;
				_if_bool = false;
				_elseif_bool = false;
				_esleover = false;
				return null;
			}
			if (_backIfBool) {
				// 加载内部脚本
				if (cmd.startsWith(INCLUDE_TAG)) {
					if (includeCommand(cmd)) {
						return null;
					}
				}
			} else if (cmd.startsWith(INCLUDE_TAG) && !_ifing && !_backIfBool && !_elseflag) {
				if (includeCommand(cmd)) {
					return null;
				}
			}
			// 选择项列表结束
			if (cmd.startsWith(OUT_TAG)) {
				_isRead = false;
				_addCommand = false;
				_executeCommand = (SELECTS_TAG + " " + _readBuffer.toString());
			}
			// 累计选择项
			if (_isRead) {
				_readBuffer.append(cmd);
				_readBuffer.append(FLAG);
				_addCommand = false;
			}
			// 选择项列表
			if (cmd.startsWith(IN_TAG)) {
				_readBuffer.setLength(0);
				_isRead = true;
				return _executeCommand;
			}

			// 输出脚本判断
			if (_addCommand && _ifing) {
				if (_backIfBool && _elseflag) {
					_executeCommand = cmd;
				}
			} else if (_addCommand) {
				_executeCommand = cmd;
			}

			if (cmd.startsWith(FLAG_SAVE_TAG)) {
				_temps = commandSplit(cmd);
				if (_temps != null && _temps.size == 2) {
					_executeCommand = cmd;
					saveCommand(null, null);
					return _executeCommand;
				}
			} else if (cmd.startsWith(FLAG_LOAD_TAG)) {
				_temps = commandSplit(cmd);
				if (_temps != null && _temps.size == 2) {
					_executeCommand = cmd;
					loadCommand(null, -1);
					return _executeCommand;
				}
			}

			// 替换脚本字符串内容
			if (_executeCommand != null) {
				_printTags = Command.getNameTags(_executeCommand, PRINT_TAG + BRACKET_LEFT_TAG, BRACKET_RIGHT_TAG);
				if (_printTags != null) {
					for (int i = 0; i < _printTags.size; i++) {
						String key = _printTags.get(i);
						Object vl = _setEnvironmentList.get(key);
						if (vl != null) {
							_executeCommand = StringUtils.replaceMatch(_executeCommand,
									(PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG), vl.toString());
						} else {
							_executeCommand = StringUtils.replaceMatch(_executeCommand,
									(PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG), key);
						}

					}

				}

				if (_isCache) {
					// 注入脚本缓存
					_scriptContext.put(_cacheCommandName, _executeCommand);
				}
			}
			// 跳转到指定脚本位置
			if (cmd.startsWith(GOTO_TAG)) {
				_temps = commandSplit(cmd);
				if (_temps != null && _temps.size == 2) {
					final String gotoFlag = _temps.get(1);
					// 如果是数字，跳转到指定行数
					if (MathUtils.isNan(gotoFlag)) {
						gotoIndex(MathUtils.ifloor(Float.parseFloat(gotoFlag)));
					} else {// 如果不是，跳转向指定标记
						gotoIndex(gotoFlag);
					}
				}
			}
		} catch (Throwable ex) {
			throw new LSysException("Command index " + _offsetPos + " read error !", ex);
		} finally {
			if (!_isInnerCommand) {
				_offsetPos++;
			}
		}

		return _executeCommand;
	}

	/**
	 * 获得脚本保存用名
	 * 
	 * @param name
	 * @return
	 */
	public final String getSaveName(final String name) {
		String newName = _scriptName + "_" + name;
		newName = StringUtils.replaceIgnoreCase(newName, "/", "$");
		newName = StringUtils.replaceIgnoreCase(newName, "\\", "$");
		return newName;
	}

	/**
	 * 保存游戏脚本数据(请注意，此处仅仅保存了脚本数据，并不主动为具体游戏保存任何额外的参数)
	 * 
	 * @param name
	 * @param other
	 */
	public final void saveCommand(String name, ArrayMap other) {
		_isRead = false;
		_addCommand = false;
		if (name == null && _temps != null && _temps.size > 0) {
			name = _temps.get(1);
		}
		Session session = new Session(getSaveName(name), false);
		for (int i = 0; i < _setEnvironmentList.size(); i++) {
			Entry entry = _setEnvironmentList.getEntry(i);
			session.add((String) entry.getKey(), (String) entry.getValue());
		}
		session.add("cmd_offsetPos", MathUtils.min(_offsetPos + 1, _scriptSize));
		session.add("cmd_cacheName", _cacheCommandName);
		session.add("cmd_nowPosFlagName", _nowPosFlagName);
		session.add("cmd_flaging", _flaging);
		session.add("cmd_ifing", _ifing);
		session.add("cmd_functioning", _functioning);
		session.add("cmd_elseflag", _elseflag);
		session.add("cmd_elseover", _esleover);
		session.add("cmd_backIfBool", _backIfBool);
		session.add("cmd_isInnerCommand", _isInnerCommand);
		session.add("cmd_isRead", _isRead);
		session.add("cmd_isCall", _isCall);
		session.add("cmd_if_bool", _if_bool);
		session.add("cmd_elseif_bool", _elseif_bool);
		if (other != null) {
			for (int i = 0; i < other.size(); i++) {
				Entry entry = other.getEntry(i);
				session.add((String) entry.getKey(), (String) entry.getValue());
			}
		}
		session.save();
	}

	/**
	 * 加载指定名称的脚本数据
	 * 
	 * @param name
	 */
	public final Command loadCommand(String name) {
		loadCommand(name, -1);
		return this;
	}

	public final Command loadCommand(String name, int line) {
		loadCommand(name, line, null);
		return this;
	}

	/**
	 * 加载指定名称的脚本数据，并跳向指定行(请注意，此处仅仅还原了脚本数据，并不主动为具体游戏恢复任何额外的参数)
	 * 
	 * @param name
	 * @param line
	 * @param other
	 * @return
	 */
	public final ArrayMap loadCommand(String name, int line, TArray<String> other) {
		_isRead = false;
		_addCommand = false;
		if (name == null && _temps != null && _temps.size > 0) {
			name = _temps.get(1);
		}
		final Session session = Session.load(getSaveName(name));
		if (session.getSize() > 0) {
			_setEnvironmentList.putAll(session.getRecords(0));
			int offsetLine = session.getInt("cmd_offsetPos", _offsetPos);
			if (offsetLine == _offsetPos) {
				gotoIndex(_offsetPos + 1);
			} else {
				gotoIndex(offsetLine);
			}
			_cacheCommandName = session.get("cmd_cacheName");
			_nowPosFlagName = session.get("cmd_nowPosFlagName");
			_flaging = session.getBoolean("cmd_flaging");
			_ifing = session.getBoolean("cmd_ifing");
			_functioning = session.getBoolean("cmd_functioning");
			_elseflag = session.getBoolean("cmd_elseflag");
			_esleover = session.getBoolean("cmd_elseover");
			_backIfBool = session.getBoolean("cmd_backIfBool");
			_isInnerCommand = session.getBoolean("cmd_isInnerCommand");
			_isRead = session.getBoolean("cmd_isRead");
			_isCall = session.getBoolean("cmd_isCall");
			_if_bool = session.getBoolean("cmd_if_bool");
			_elseif_bool = session.getBoolean("cmd_elseif_bool");
			if (other == null) {
				return null;
			} else {
				final int size = other.size;
				ArrayMap result = new ArrayMap(size);
				for (int i = 0; i < size; i++) {
					String otherName = other.get(i);
					result.put(otherName, session.get(otherName));
				}
				return result;
			}
		}
		return null;
	}

	public String[] getCommands() {
		return CollectionUtils.copyOf(_scriptList);
	}

	/**
	 * 载入其它脚本
	 * 
	 * @param cmd
	 * @return
	 */
	private final boolean includeCommand(String cmd) {
		_temps = commandSplit(cmd);
		final StrBuilder sbr = new StrBuilder();
		for (int i = 1; i < _temps.size; i++) {
			sbr.append(_temps.get(i));
		}
		final String fileName = sbr.toString();
		if (fileName.length() > 0) {
			_innerCommand = new Command(fileName);
			_isInnerCommand = true;
			return true;
		}
		return false;
	}

	/**
	 * 包含指定脚本内容
	 * 
	 * @param fileName
	 * @return
	 */
	public final static String[] includeFile(String fileName) {
		final String context = TextResource.get().loadText(fileName);
		if (StringUtils.isEmpty(context)) {
			throw new LSysException("The script file [" + fileName + "] not found !");
		}
		return includeString(fileName.trim().toLowerCase(), context);
	}

	/**
	 * 加载指定字符串为游戏脚本
	 * 
	 * @param context
	 * @return
	 */
	public final static String[] includeString(String key, String context) {
		if (StringUtils.isEmpty(context)) {
			throw new LSysException("The key [" + key + "] of data is empty !");
		}
		if (_scriptLazy == null) {
			_scriptLazy = new ArrayMap(100);
		} else if (_scriptLazy.size() > 10000) {
			_scriptLazy.clear();
		}
		final int capacity = 2000;
		String[] result = (String[]) _scriptLazy.get(key);
		if (result == null) {
			result = new String[capacity];
			int length = capacity;
			int index = 0;
			try {
				final StrTokenizer reader = new StrTokenizer(context, LSystem.NL);
				String record = null;
				for (; reader.hasMoreTokens();) {
					record = reader.nextToken().trim();
					if (record.length() > 0 && !record.startsWith(FLAG_L_TAG) && !record.startsWith(FLAG_C_TAG)
							&& !record.startsWith(FLAG_I_TAG)) {
						if (index >= length) {
							result = (String[]) CollectionUtils.expand(result, capacity);
							length += capacity;
						}
						result[index] = record;
						index++;
					}
				}
				result = CollectionUtils.copyOf(result, index);
			} catch (Throwable ex) {
				throw new LSysException("Command load error !", ex);
			}
			_scriptLazy.put(key, result);
			return result;
		} else {
			return CollectionUtils.copyOf(result);
		}

	}

	/**
	 * 过滤指定脚本文件内容为list
	 * 
	 * @param src
	 * @return
	 */
	public static TArray<String> commandSplit(final String src) {
		final String result = updateOperator(src);
		final String[] cmds = result.split(FLAG);
		return new TArray<String>(cmds);
	}

	/**
	 * 释放并清空全部缓存资源
	 * 
	 */
	public final static void releaseCache() {
		if (_setEnvironmentList != null) {
			_setEnvironmentList.clear();
			_setEnvironmentList = null;
		}
		if (_conditionEnvironmentList != null) {
			_conditionEnvironmentList.clear();
			_conditionEnvironmentList = null;
		}
		if (_functions != null) {
			_functions.clear();
			_functions = null;
		}
		if (_scriptContext != null) {
			_scriptContext.clear();
			_scriptContext = null;
		}
		if (_scriptLazy != null) {
			_scriptLazy.clear();
			_scriptLazy = null;
		}

	}

	public static void freeStatic() {
		_scriptLazy = null;
		_scriptContext = null;
		_functions = null;
		_setEnvironmentList = null;
		_conditionEnvironmentList = null;
	}

	public boolean isClosed() {
		return _isClose;
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue("command");
		builder.kv("script", _scriptList);
		return builder.toString();
	}

	@Override
	public void close() {
		this._isClose = true;
		if (_readBuffer != null) {
			_readBuffer = null;
		}
		if (_temps != null) {
			try {
				_temps.clear();
				_temps = null;
			} catch (Throwable e) {
			}
		}
		if (_printTags != null) {
			_printTags.clear();
			_printTags = null;
		}
		if (_randTags != null) {
			_randTags.clear();
			_randTags = null;
		}
		if (exp != null) {
			exp.close();
		}
	}
}
