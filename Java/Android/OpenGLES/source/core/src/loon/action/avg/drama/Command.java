package loon.action.avg.drama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import loon.core.LRelease;
import loon.core.LSystem;
import loon.core.resource.Resources;
import loon.core.store.Session;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;
import loon.utils.StringUtils;
import loon.utils.collection.ArrayMap;

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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Command extends Conversion implements Serializable, LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 脚本缓存
	private static HashMap scriptLazy;

	// 脚本数据缓存
	private static HashMap scriptContext;

	// 函数列表
	private static ArrayMap functions;

	// 变量列表
	private static HashMap setEnvironmentList;

	// 条件分支列表
	private static ArrayMap conditionEnvironmentList;

	// 读入连续数据
	private StringBuffer readBuffer;

	// 缓存脚本名
	private String cacheCommandName;

	// 注释标记中
	private boolean flaging = false;

	// 判断标记中
	private boolean ifing = false;

	// 函数标记中
	private boolean functioning = false;

	// 分支标记
	private boolean esleflag = false;

	private boolean esleover = false;

	private boolean backIfBool = false;

	private boolean isClose;

	private String executeCommand;

	private String nowPosFlagName;

	private boolean addCommand;

	private boolean isInnerCommand;

	private boolean isRead;

	private boolean isCall;

	private boolean isCache;

	private boolean if_bool;

	private boolean elseif_bool;

	private Command innerCommand;

	private List temps;

	private List printTags;

	private List randTags;

	private int scriptSize;

	private int offsetPos;

	// 脚本数据列表
	private String[] scriptList;

	// 脚本名
	private String scriptName;

	/**
	 * 构造函数，载入指定脚本文件
	 * 
	 * @param fileName
	 */
	public Command(String fileName) {
		createCache(false);
		formatCommand(fileName);
	}

	/**
	 * 构造函数，载入指定脚本文件
	 * 
	 * @param in
	 */
	public Command(InputStream in) {
		createCache(false);
		formatCommand(in);
	}

	/**
	 * 构造函数，载入指定list脚本
	 * 
	 * @param resource
	 */
	public Command(String fileName, String[] res) {
		createCache(false);
		formatCommand("function", res);
	}

	public static void createCache(boolean free) {
		if (free) {
			if (scriptContext == null) {
				scriptContext = new HashMap(1000);
			} else {
				scriptContext.clear();
			}
			if (functions == null) {
				functions = new ArrayMap(20);
			} else {
				functions.clear();
			}
			if (setEnvironmentList == null) {
				setEnvironmentList = new HashMap(20);
			} else {
				setEnvironmentList.clear();
			}
			if (conditionEnvironmentList == null) {
				conditionEnvironmentList = new ArrayMap(30);
			} else {
				conditionEnvironmentList.clear();
			}
		} else {
			if (scriptContext == null) {
				scriptContext = new HashMap(1000);
			}
			if (functions == null) {
				functions = new ArrayMap(20);
			}
			if (setEnvironmentList == null) {
				setEnvironmentList = new HashMap(20);
			}
			if (conditionEnvironmentList == null) {
				conditionEnvironmentList = new ArrayMap(30);
			}
		}
	}

	public void formatCommand(String fileName) {
		formatCommand(fileName, Command.includeFile(fileName));
	}

	public void formatCommand(InputStream in) {
		formatCommand("temp" + in.hashCode(), Command.includeFile(in));
	}

	public void formatCommand(String name, String[] res) {
		if (!"function".equalsIgnoreCase(name)) {
			if (functions != null) {
				functions.clear();
			}
		}
		if (conditionEnvironmentList != null) {
			conditionEnvironmentList.clear();
		}
		if (setEnvironmentList != null) {
			setEnvironmentList.put(V_SELECT_KEY, "-1");
		}
		if (readBuffer == null) {
			readBuffer = new StringBuffer(256);
		} else {
			readBuffer.delete(0, readBuffer.length());
		}
		this.scriptName = name;
		this.scriptList = res;
		this.scriptSize = res.length;
		this.offsetPos = 0;
		this.flaging = false;
		this.ifing = false;
		this.isCache = true;
		this.esleflag = false;
		this.backIfBool = false;
		this.functioning = false;
		this.esleover = false;
		this.backIfBool = false;
		this.addCommand = false;
		this.isInnerCommand = false;
		this.isRead = false;
		this.isCall = false;
		this.isCache = false;
		this.if_bool = false;
		this.elseif_bool = false;
	}

	private boolean setupIF(String commandString, String nowPosFlagName,
			HashMap setEnvironmentList, ArrayMap conditionEnvironmentList) {
		boolean result = false;
		conditionEnvironmentList.put(nowPosFlagName, Boolean.valueOf(false));
		try {
			List temps = commandSplit(commandString);
			int size = temps.size();
			Object valueA = null;
			Object valueB = null;
			String condition = null;
			if (size <= 4) {
				valueA = temps.get(1);
				valueB = temps.get(3);
				valueA = setEnvironmentList.get(valueA) == null ? valueA
						: setEnvironmentList.get(valueA);
				valueB = setEnvironmentList.get(valueB) == null ? valueB
						: setEnvironmentList.get(valueB);
				condition = (String) temps.get(2);
			} else {
				int count = 0;
				StringBuffer sbr = new StringBuffer();
				for (Iterator it = temps.iterator(); it.hasNext();) {
					String res = (String) it.next();
					if (count > 0) {
						if (!isCondition(res)) {
							sbr.append(res);
						} else {
							valueA = sbr.toString();
							valueA = String.valueOf(exp.parse(valueA));
							sbr.delete(0, sbr.length());
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
				} catch (Exception e) {
				}
			}
			// 无法判定
			if (valueA == null || valueB == null) {
				conditionEnvironmentList.put(nowPosFlagName,
						Boolean.valueOf(false));
			}

			// 相等
			if ("==".equals(condition)) {
				conditionEnvironmentList.put(
						nowPosFlagName,
						Boolean.valueOf(result = valueA.toString().equals(
								valueB.toString())));
				// 非等
			} else if ("!=".equals(condition)) {
				conditionEnvironmentList.put(
						nowPosFlagName,
						Boolean.valueOf(result = !valueA.toString().equals(
								valueB.toString())));
				// 大于
			} else if (">".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName,
						Boolean.valueOf(result = numberA > numberB));
				// 小于
			} else if ("<".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName,
						Boolean.valueOf(result = numberA < numberB));

				// 大于等于
			} else if (">=".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName,
						Boolean.valueOf(result = numberA >= numberB));
				// 小于等于
			} else if ("<=".equals(condition)) {
				float numberA = Float.parseFloat(valueA.toString());
				float numberB = Float.parseFloat(valueB.toString());
				conditionEnvironmentList.put(nowPosFlagName,
						Boolean.valueOf(result = numberA <= numberB));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 打开脚本缓存
	 * 
	 */
	public void openCache() {
		isCache = true;
	}

	/**
	 * 关闭脚本缓存
	 * 
	 */
	public void closeCache() {
		isCache = false;
	}

	/**
	 * 当前脚本行缓存名
	 * 
	 * @return
	 */
	public String nowCacheOffsetName(String cmd) {
		return (scriptName + FLAG + offsetPos + FLAG + cmd).toLowerCase();
	}

	/**
	 * 重启脚本缓存
	 * 
	 */
	public static void resetCache() {
		if (scriptContext != null) {
			scriptContext.clear();
		}
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	/**
	 * 返回当前的读入数据集合
	 * 
	 * @return
	 */
	public synchronized String[] getReads() {
		String result = readBuffer.toString();
		result = result.replaceAll(SELECTS_TAG, "");
		return StringUtils.split(result, FLAG);
	}

	/**
	 * 返回指定索引的读入数据
	 * 
	 * @param index
	 * @return
	 */
	public synchronized String getRead(int index) {
		try {
			return getReads()[index];
		} catch (Exception e) {
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
	public static String getNameTag(String messages, String startString,
			String endString) {
		List results = getNameTags(messages, startString, endString);
		return (results == null || results.size() == 0) ? null
				: (String) results.get(0);
	}

	/**
	 * 截取指定标记内容为list
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static List getNameTags(String messages, String startString,
			String endString) {
		return Command.getNameTags(messages.toCharArray(),
				startString.toCharArray(), endString.toCharArray());
	}

	/**
	 * 截取指定标记内容为list
	 * 
	 * @param messages
	 * @param startString
	 * @param endString
	 * @return
	 */
	public static List getNameTags(char[] messages, char[] startString,
			char[] endString) {
		int dlength = messages.length;
		int slength = startString.length;
		int elength = endString.length;
		List tagList = new ArrayList(10);
		boolean lookup = false;
		int lookupStartIndex = 0;
		int lookupEndIndex = 0;
		int length;
		StringBuffer sbr = new StringBuffer(100);
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
					sbr.delete(0, length);
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
	public void select(int type) {
		if (innerCommand != null) {
			innerCommand.setVariable(V_SELECT_KEY, String.valueOf(type));
		}
		setVariable(V_SELECT_KEY, String.valueOf(type));
	}

	public String getSelect() {
		return (String) getVariable(V_SELECT_KEY);
	}

	/**
	 * 插入变量
	 * 
	 * @param key
	 * @param value
	 */
	public void setVariable(String key, Object value) {
		setEnvironmentList.put(key, value);
	}

	/**
	 * 插入变量集合
	 * 
	 * @param vars
	 */
	public void setVariables(HashMap vars) {
		setEnvironmentList.putAll(vars);
	}

	/**
	 * 返回变量集合
	 * 
	 * @return
	 */
	public HashMap getVariables() {
		return setEnvironmentList;
	}

	public Object getVariable(String key) {
		return setEnvironmentList.get(key);
	}

	/**
	 * 删除变量
	 * 
	 * @param key
	 */
	public void removeVariable(String key) {
		setEnvironmentList.remove(key);
	}

	/**
	 * 判定脚本是否允许继续解析
	 * 
	 * @return
	 */
	public boolean next() {
		return (offsetPos < scriptSize);
	}

	/**
	 * 跳转向指定索引位置
	 * 
	 * @param offset
	 * @return
	 */
	public boolean gotoIndex(final int offset) {
		boolean result = offset < scriptSize && offset > 0
				&& offset != offsetPos;
		if (result) {
			offsetPos = offset;
		}
		return result;
	}

	public int getIndex() {
		return offsetPos;
	}

	/**
	 * 批处理执行脚本，并返回可用list结果
	 * 
	 * @return
	 */
	public List batchToList() {
		List reslist = new ArrayList(scriptSize);
		for (; next();) {
			String execute = doExecute();
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
		StringBuffer resString = new StringBuffer(scriptSize * 10);
		for (; next();) {
			String execute = doExecute();
			if (execute != null) {
				resString.append(execute);
				resString.append('\n');
			}
		}
		return resString.toString();
	}

	private void setupSET(String cmd) {
		if (cmd.startsWith(SET_TAG)) {
			List temps = commandSplit(cmd);
			int len = temps.size();
			String result = null;
			if (len == 4) {
				result = temps.get(3).toString();
			} else if (len > 4) {
				StringBuffer sbr = new StringBuffer(len);
				for (int i = 3; i < temps.size(); i++) {
					sbr.append(temps.get(i));
				}
				result = sbr.toString();
			}

			if (result != null) {
				// 替换已有变量字符
				Set set = setEnvironmentList.entrySet();
				for (Iterator it = set.iterator(); it.hasNext();) {
					Entry entry = (Entry) it.next();
					if (!(StringUtils.startsWith(result, '"') && StringUtils
							.endsWith(result, '"'))) {
						result = StringUtils.replaceMatch(result,
								(String) entry.getKey(), entry.getValue()
										.toString());
					}
				}
				// 当为普通字符串时
				if (StringUtils.startsWith(result, '"')
						&& StringUtils.endsWith(result, '"')) {
					setEnvironmentList.put(temps.get(1),
							result.substring(1, result.length() - 1));
				} else if (StringUtils.isChinaLanguage(result.toCharArray())
						|| StringUtils.isEnglishAndNumeric(result)) {
					setEnvironmentList.put(temps.get(1), result);
				} else {
					// 当为数学表达式时
					setEnvironmentList.put(temps.get(1), exp.parse(result));
				}
			}
			addCommand = false;
		}

	}

	/**
	 * 随机数处理
	 * 
	 */
	private void setupRandom(String cmd) {
		// 随机数判定
		if (cmd.indexOf(RAND_TAG) != -1) {
			randTags = Command.getNameTags(cmd, RAND_TAG + BRACKET_LEFT_TAG,
					BRACKET_RIGHT_TAG);
			if (randTags != null) {
				for (Iterator it = randTags.iterator(); it.hasNext();) {
					String key = (String) it.next();
					Object value = setEnvironmentList.get(key);
					// 已存在变量
					if (value != null) {
						cmd = StringUtils
								.replaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG
										+ key + BRACKET_RIGHT_TAG).intern(),
										value.toString());
						// 设定有随机数生成范围
					} else if (MathUtils.isNan(key)) {
						cmd = StringUtils
								.replaceMatch(
										cmd,
										(RAND_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG)
												.intern(),
										String.valueOf(GLOBAL_RAND
												.nextInt(Integer.parseInt(key))));
						// 无设定
					} else {
						cmd = StringUtils
								.replaceMatch(cmd, (RAND_TAG + BRACKET_LEFT_TAG
										+ key + BRACKET_RIGHT_TAG).intern(),
										String.valueOf(GLOBAL_RAND.nextInt()));
					}
				}
			}
		}
	}

	private void innerCallTrue() {
		isCall = true;
		isInnerCommand = true;
	}

	private void innerCallFalse() {
		isCall = false;
		isInnerCommand = false;
		innerCommand = null;
	}

	/**
	 * 逐行执行脚本命令
	 * 
	 * @return
	 */
	public synchronized String doExecute() {
		if (isClose) {
			return null;
		}
		this.executeCommand = null;
		this.addCommand = true;
		this.isInnerCommand = (innerCommand != null);
		this.if_bool = false;
		this.elseif_bool = false;

		try {
			// 执行call命令
			if (isInnerCommand && isCall) {
				setVariables(innerCommand.getVariables());
				if (innerCommand.next()) {
					return innerCommand.doExecute();
				} else {
					innerCallFalse();
					return executeCommand;
				}
				// 执行内部脚本
			} else if (isInnerCommand && !isCall) {
				setVariables(innerCommand.getVariables());
				if (innerCommand.next()) {
					return innerCommand.doExecute();
				} else {
					innerCommand = null;
					isInnerCommand = false;
					return executeCommand;
				}
			}

			nowPosFlagName = String.valueOf(offsetPos);
			int length = conditionEnvironmentList.size();
			if (length > 0) {
				Object ifResult = conditionEnvironmentList.get(length - 1);
				if (ifResult != null) {
					backIfBool = ((Boolean) ifResult).booleanValue();
				}
			}

			// 空指向判定
			if (scriptList == null) {
				resetCache();
				return executeCommand;
			} else if (scriptList.length - 1 < offsetPos) {
				resetCache();
				return executeCommand;
			}

			// 获得全行命令
			String cmd = scriptList[offsetPos];

			// 清空脚本缓存
			if (cmd.startsWith(RESET_CACHE_TAG)) {
				resetCache();
				return executeCommand;
			}

			if (isCache) {
				// 获得缓存命令行名
				cacheCommandName = nowCacheOffsetName(cmd);
				// 读取缓存的脚本
				Object cache = scriptContext.get(cacheCommandName);
				if (cache != null) {
					return (String) cache;
				}
			}

			// 注释中
			if (flaging) {
				flaging = !(cmd.startsWith(FLAG_LS_E_TAG) || cmd
						.endsWith(FLAG_LS_E_TAG));
				return executeCommand;
			}

			if (!flaging) {
				// 全局注释
				if (cmd.startsWith(FLAG_LS_B_TAG)
						&& !cmd.endsWith(FLAG_LS_E_TAG)) {
					flaging = true;
					return executeCommand;
				} else if (cmd.startsWith(FLAG_LS_B_TAG)
						&& cmd.endsWith(FLAG_LS_E_TAG)) {
					return executeCommand;
				}
			}

			// 执行随机数标记
			setupRandom(cmd);

			// 执行获取变量标记
			setupSET(cmd);

			// 结束脚本中代码段标记
			if (cmd.endsWith(END_TAG)) {
				functioning = false;
				return executeCommand;
			}

			// 标注脚本中代码段标记
			if (cmd.startsWith(BEGIN_TAG)) {
				temps = commandSplit(cmd);
				if (temps.size() == 2) {
					functioning = true;
					functions.put(temps.get(1), new String[0]);
					return executeCommand;
				}
			}

			// 开始记录代码段
			if (functioning) {
				int size = functions.size() - 1;
				String[] function = (String[]) functions.get(size);
				int index = function.length;
				function = (String[]) CollectionUtils.expand(function, 1);
				function[index] = cmd;
				functions.set(size, function);
				return executeCommand;
			}

			// 执行代码段调用标记
			if (((!esleflag && !ifing) || (esleflag && ifing))
					&& cmd.startsWith(CALL_TAG) && !isCall) {
				temps = commandSplit(cmd);
				if (temps.size() == 2) {
					String functionName = (String) temps.get(1);
					String[] funs = (String[]) functions.get(functionName);
					if (funs != null) {
						innerCommand = new Command(scriptName + FLAG
								+ functionName, funs);
						innerCommand.closeCache();
						innerCommand.setVariables(getVariables());
						innerCallTrue();
						return null;
					}
				}
			}

			if (!if_bool && !elseif_bool) {
				// 获得循序结构条件
				if_bool = cmd.startsWith(IF_TAG);
				elseif_bool = cmd.startsWith(ELSE_TAG);

			}

			// 条件判断a
			if (if_bool) {
				esleover = esleflag = setupIF(cmd, nowPosFlagName,
						setEnvironmentList, conditionEnvironmentList);
				addCommand = false;
				ifing = true;
				// 条件判断b
			} else if (elseif_bool) {
				String[] value = StringUtils.split(cmd, " ");
				if (!backIfBool && !esleflag) {
					// 存在if判断
					if (value.length > 1 && IF_TAG.equals(value[1])) {
						esleover = esleflag = setupIF(
								cmd.replaceAll(ELSE_TAG, "").trim(),
								nowPosFlagName, setEnvironmentList,
								conditionEnvironmentList);
						addCommand = false;
						// 单纯的else
					} else if (value.length == 1 && ELSE_TAG.equals(value[0])) {
						if (!esleover) {
							esleover = esleflag = setupIF("if 1==1",
									nowPosFlagName, setEnvironmentList,
									conditionEnvironmentList);
							addCommand = false;
						}
					}
				} else {
					esleflag = false;
					addCommand = false;
					conditionEnvironmentList.put(nowPosFlagName,
							Boolean.valueOf(false));

				}
			}

			// 分支结束
			if (cmd.startsWith(IF_END_TAG)) {
				conditionEnvironmentList.clear();
				backIfBool = false;
				addCommand = false;
				ifing = false;
				if_bool = false;
				elseif_bool = false;
				esleover = false;
				return null;
			}
			if (backIfBool) {
				// 加载内部脚本
				if (cmd.startsWith(INCLUDE_TAG)) {
					if (includeCommand(cmd)) {
						return null;
					}
				}
			} else if (cmd.startsWith(INCLUDE_TAG) && !ifing && !backIfBool
					&& !esleflag) {
				if (includeCommand(cmd)) {
					return null;
				}
			}
			// 选择项列表结束
			if (cmd.startsWith(OUT_TAG)) {
				isRead = false;
				addCommand = false;
				executeCommand = (SELECTS_TAG + " " + readBuffer.toString());
			}
			// 累计选择项
			if (isRead) {
				readBuffer.append(cmd);
				readBuffer.append(FLAG);
				addCommand = false;
			}
			// 选择项列表
			if (cmd.startsWith(IN_TAG)) {
				readBuffer.delete(0, readBuffer.length());
				isRead = true;
				return executeCommand;
			}

			// 输出脚本判断
			if (addCommand && ifing) {
				if (backIfBool && esleflag) {
					executeCommand = cmd;
				}

			} else if (addCommand) {
				executeCommand = cmd;
			}

			if (cmd.startsWith(FLAG_SAVE_TAG)) {
				temps = commandSplit(cmd);
				if (temps != null && temps.size() == 2) {
					executeCommand = cmd;
					saveCommand(null, null);
					return executeCommand;
				}
			} else if (cmd.startsWith(FLAG_LOAD_TAG)) {
				temps = commandSplit(cmd);
				if (temps != null && temps.size() == 2) {
					executeCommand = cmd;
					loadCommand(null, -1);
					return executeCommand;
				}
			}

			// 替换脚本字符串内容
			if (executeCommand != null) {
				printTags = Command.getNameTags(executeCommand, PRINT_TAG
						+ BRACKET_LEFT_TAG, BRACKET_RIGHT_TAG);
				if (printTags != null) {
					for (Iterator it = printTags.iterator(); it.hasNext();) {
						String key = (String) it.next();
						Object value = setEnvironmentList.get(key);
						if (value != null) {
							executeCommand = StringUtils
									.replaceMatch(
											executeCommand,
											(PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG)
													.intern(), value.toString());
						} else {
							executeCommand = StringUtils
									.replaceMatch(
											executeCommand,
											(PRINT_TAG + BRACKET_LEFT_TAG + key + BRACKET_RIGHT_TAG)
													.intern(), key);
						}

					}

				}

				if (isCache) {
					// 注入脚本缓存
					scriptContext.put(cacheCommandName, executeCommand);
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (!isInnerCommand) {
				offsetPos++;
			}
		}

		return executeCommand;
	}

	/**
	 * 获得脚本保存用名
	 * 
	 * @param name
	 * @return
	 */
	public final String getSaveName(final String name) {
		String newName = scriptName + "_" + name;
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
	public final void saveCommand(String name, HashMap<String, String> other) {
		isRead = false;
		addCommand = false;
		if (name == null && temps != null && temps.size() > 0) {
			name = (String) temps.get(1);
		}
		Session session = new Session(getSaveName(name), false);
		Set set = setEnvironmentList.entrySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			session.add((String) entry.getKey(), (String) entry.getValue());
		}
		session.add("cmd_offsetPos", MathUtils.min(offsetPos + 1, scriptSize));
		session.add("cmd_cacheName", cacheCommandName);
		session.add("cmd_nowPosFlagName", nowPosFlagName);
		session.add("cmd_flaging", flaging);
		session.add("cmd_ifing", ifing);
		session.add("cmd_functioning", functioning);
		session.add("cmd_esleflag", esleflag);
		session.add("cmd_esleover", esleover);
		session.add("cmd_backIfBool", backIfBool);
		session.add("cmd_isInnerCommand", isInnerCommand);
		session.add("cmd_isRead", isRead);
		session.add("cmd_isCall", isCall);
		session.add("cmd_if_bool", if_bool);
		session.add("cmd_elseif_bool", elseif_bool);
		if (other != null) {
			Set otherSet = other.entrySet();
			for (Iterator it = otherSet.iterator(); it.hasNext();) {
				Entry entry = (Entry) it.next();
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
	public final void loadCommand(String name) {
		loadCommand(name, -1);
	}

	public final void loadCommand(String name, int line) {
		loadCommand(name, line, null);
	}

	/**
	 * 加载指定名称的脚本数据，并跳向指定行(请注意，此处仅仅还原了脚本数据，并不主动为具体游戏恢复任何额外的参数)
	 * 
	 * @param name
	 * @param line
	 * @param other
	 * @return
	 */
	public final HashMap loadCommand(String name, int line, List<String> other) {
		isRead = false;
		addCommand = false;
		if (name == null && temps != null && temps.size() > 0) {
			name = (String) temps.get(1);
		}
		Session session = Session.load(getSaveName(name));
		if (session.getSize() > 0) {
			setEnvironmentList.putAll(session.getRecords(0));
			int offsetLine = session.getInt("cmd_offsetPos", offsetPos);
			if (offsetLine == offsetPos) {
				gotoIndex(offsetPos + 1);
			} else {
				gotoIndex(offsetLine);
			}
			cacheCommandName = session.get("cmd_cacheName");
			nowPosFlagName = session.get("cmd_nowPosFlagName");
			flaging = session.getBoolean("cmd_flaging");
			ifing = session.getBoolean("cmd_ifing");
			functioning = session.getBoolean("cmd_functioning");
			esleflag = session.getBoolean("cmd_esleflag");
			esleover = session.getBoolean("cmd_esleover");
			backIfBool = session.getBoolean("cmd_backIfBool");
			isInnerCommand = session.getBoolean("cmd_isInnerCommand");
			isRead = session.getBoolean("cmd_isRead");
			isCall = session.getBoolean("cmd_isCall");
			if_bool = session.getBoolean("cmd_if_bool");
			elseif_bool = session.getBoolean("cmd_elseif_bool");
			if (other == null) {
				return null;
			} else {
				final int size = other.size();
				HashMap result = new HashMap(size);
				for (int i = 0; i < size; i++) {
					String otherName = other.get(i);
					result.put(otherName, session.get(otherName));
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * 载入其它脚本
	 * 
	 * @param cmd
	 * @return
	 */
	private final boolean includeCommand(String cmd) {
		temps = commandSplit(cmd);
		StringBuffer sbr = new StringBuffer();
		for (int i = 1; i < temps.size(); i++) {
			sbr.append(temps.get(i));
		}
		String fileName = sbr.toString();
		if (fileName.length() > 0) {
			innerCommand = new Command(fileName);
			isInnerCommand = true;
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
		if (scriptLazy == null) {
			scriptLazy = new HashMap(100);
		} else if (scriptLazy.size() > 10000) {
			scriptLazy.clear();
		}
		final int capacity = 2000;
		String key = fileName.trim().toLowerCase();
		String[] result = (String[]) scriptLazy.get(key);
		if (result == null) {
			InputStream in = null;
			BufferedReader reader = null;
			result = new String[capacity];
			int length = capacity;
			int index = 0;
			try {
				in = Resources.openResource(fileName);
				reader = new BufferedReader(new InputStreamReader(in,
						LSystem.encoding));
				String record = null;
				for (; (record = reader.readLine()) != null;) {
					record = record.trim();
					if (record.length() > 0 && !record.startsWith(FLAG_L_TAG)
							&& !record.startsWith(FLAG_C_TAG)
							&& !record.startsWith(FLAG_I_TAG)) {
						if (index >= length) {
							result = (String[]) CollectionUtils.expand(result,
									capacity);
							length += capacity;
						}
						result[index] = record;
						index++;
					}
				}
				result = CollectionUtils.copyOf(result, index);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (reader != null) {
					try {
						reader.close();
						reader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			scriptLazy.put(key, result);
			return result;
		} else {
			return CollectionUtils.copyOf(result);
		}

	}

	/**
	 * 获得指定脚本内容
	 * 
	 * @param in
	 * @return
	 */
	public final static String[] includeFile(InputStream in) {
		if (scriptLazy == null) {
			scriptLazy = new HashMap(100);
		} else if (scriptLazy.size() > 10000) {
			scriptLazy.clear();
		}
		final int capacity = 2000;
		String key = String.valueOf(in.hashCode());
		String[] result = (String[]) scriptLazy.get(key);
		if (result == null) {
			BufferedReader reader = null;
			result = new String[capacity];
			int length = capacity;
			int index = 0;
			try {
				reader = new BufferedReader(new InputStreamReader(in,
						LSystem.encoding));
				String record = null;
				for (; (record = reader.readLine()) != null;) {
					record = record.trim();
					if (record.length() > 0 && !record.startsWith(FLAG_L_TAG)
							&& !record.startsWith(FLAG_C_TAG)
							&& !record.startsWith(FLAG_I_TAG)) {
						if (index >= length) {
							result = (String[]) CollectionUtils.expand(result,
									capacity);
							length += capacity;
						}
						result[index] = record;
						index++;
					}
				}
				result = CollectionUtils.copyOf(result, index);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			} finally {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (reader != null) {
					try {
						reader.close();
						reader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			scriptLazy.put(key, result);
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
	public static List commandSplit(final String src) {
		String result = updateOperator(src);
		String[] cmds = result.split(FLAG);
		return Arrays.asList(cmds);
	}

	/**
	 * 释放并清空全部缓存资源
	 * 
	 */
	public final static void releaseCache() {
		if (setEnvironmentList != null) {
			setEnvironmentList.clear();
			setEnvironmentList = null;
		}
		if (conditionEnvironmentList != null) {
			conditionEnvironmentList.clear();
			conditionEnvironmentList = null;
		}
		if (functions != null) {
			functions.clear();
			functions = null;
		}
		if (scriptContext != null) {
			scriptContext.clear();
			scriptContext = null;
		}
		if (scriptLazy != null) {
			scriptLazy.clear();
			scriptLazy = null;
		}
		System.gc();
	}

	public boolean isClose() {
		return isClose;
	}

	@Override
	public void dispose() {
		this.isClose = true;
		if (readBuffer != null) {
			readBuffer = null;
		}
		if (temps != null) {
			try {
				temps.clear();
				temps = null;
			} catch (Exception e) {
			}
		}
		if (printTags != null) {
			printTags.clear();
			printTags = null;
		}
		if (randTags != null) {
			randTags.clear();
			randTags = null;
		}
		if (exp != null) {
			exp.dispose();
		}
	}

}
