/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.utils;

/**
 * 一个Loon数据临时存储器Bundle的Object存储实现,具有简单的存储中数据四则运算能力.<br>
 * 
 * Screen中默认的全局通用Bundle(screen.getBundle获得)就是此类.
 * 
 * <pre>
 * 
 * ObjectBundle b = new ObjectBundle();
 * // 添加一个数值a,数值1234,+128,+325(输入数值就可以,不管是字符串或数字类型),乘3,除69,返回a结果
 * System.out.println(b.add("a", "1234").inc("a", 128).inc("a", "325").mul("a", 3).div("a", 69).get("a"));
 * 
 * </pre>
 * 
 */
public class ObjectBundle extends MapBundle<Object> {

	public ObjectBundle setVar(String name, Object v) {
		put(name, v);
		return this;
	}

	public ObjectBundle setStr(String name, String v) {
		return set(name, v);
	}

	public ObjectBundle setBool(String name, boolean v) {
		return set(name, Boolean.valueOf(v));
	}

	public ObjectBundle setFloat(String name, float v) {
		return set(name, Float.valueOf(v));
	}

	public ObjectBundle setInt(String name, int v) {
		return set(name, Integer.valueOf(v));
	}

	public ObjectBundle setLong(String name, long v) {
		return set(name, Long.valueOf(v));
	}

	public Object getVar(String name) {
		return get(name, null);
	}

	public Object getVar(String name, Object rollback) {
		return get(name, rollback);
	}

	public Object removeVar(String name) {
		return remove(name, null);
	}

	public Object removeVar(String name, Object rollback) {
		return remove(name, rollback);
	}

	public ObjectBundle clearVars() {
		clear();
		return this;
	}

	public String getStr(String name) {
		return HelperUtils.toStr(get(name));
	}

	public int getInt(String name) {
		return HelperUtils.toInt(get(name));
	}

	public long getLong(String name) {
		return HelperUtils.toLong(get(name));
	}

	public float getFloat(String name) {
		return HelperUtils.toFloat(get(name));
	}

	public boolean getBool(String name) {
		return isBool(name);
	}

	public boolean isBool(String name) {
		Object result = get(name);
		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		}
		return StringUtils.toBoolean(HelperUtils.toStr(result));
	}

	public ObjectBundle set(String key, Object v) {
		put(key, v);
		return this;
	}

	public ObjectBundle add(String key, Object v) {
		return inc(key, v);
	}

	public ObjectBundle inc(String key, Object v) {
		Calculator calculator = new Calculator(get(key, 0f));
		if (calculator.getFloat() != -1f) {
			set(key, calculator.add(v).getFloat());
		}
		return this;
	}

	public ObjectBundle sub(String key, Object v) {
		Calculator calculator = new Calculator(get(key, 0f));
		if (calculator.getFloat() != -1f) {
			set(key, calculator.sub(v).getFloat());
		}
		return this;
	}

	public ObjectBundle mul(String key, Object v) {
		Calculator calculator = new Calculator(get(key, 0f));
		if (calculator.getFloat() != -1f) {
			set(key, calculator.mul(v).getFloat());
		}
		return this;
	}

	public ObjectBundle div(String key, Object v) {
		Calculator calculator = new Calculator(get(key, 0f));
		if (calculator.getFloat() != -1f) {
			set(key, calculator.div(v).getFloat());
		}
		return this;
	}

	public ObjectBundle mod(String key, Object v) {
		Calculator calculator = new Calculator(get(key, 0f));
		if (calculator.getFloat() != -1f) {
			set(key, calculator.mod(v).getFloat());
		}
		return this;
	}

	public Calculator calc(String key) {
		return new Calculator(get(key, -1f));
	}

	@Override
	public String toString() {
		return _mapBundle.toString();
	}
}
