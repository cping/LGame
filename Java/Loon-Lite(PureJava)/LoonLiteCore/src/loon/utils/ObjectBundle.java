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

	public ObjectBundle add(String key, Object value) {
		put(key, value);
		return this;
	}

	public ObjectBundle inc(String key, Object value) {
		Object data = _mapBundle.get(key);
		Calculator calculator = new Calculator(data);
		if (calculator.getFloat() != -1f) {
			_mapBundle.put(key, calculator.add(value).getFloat());
		}
		return this;
	}

	public ObjectBundle sub(String key, Object value) {
		Object data = _mapBundle.get(key);
		Calculator calculator = new Calculator(data);
		if (calculator.getFloat() != -1f) {
			_mapBundle.put(key, calculator.sub(value).getFloat());
		}
		return this;
	}

	public ObjectBundle mul(String key, Object value) {
		Object data = _mapBundle.get(key);
		Calculator calculator = new Calculator(data);
		if (calculator.getFloat() != -1f) {
			_mapBundle.put(key, calculator.mul(value).getFloat());
		}
		return this;
	}

	public ObjectBundle div(String key, Object value) {
		Object data = _mapBundle.get(key);
		Calculator calculator = new Calculator(data);
		if (calculator.getFloat() != -1f) {
			_mapBundle.put(key, calculator.div(value).getFloat());
		}
		return this;
	}

	public ObjectBundle mod(String key, Object value) {
		Object data = _mapBundle.get(key);
		Calculator calculator = new Calculator(data);
		if (calculator.getFloat() != -1f) {
			_mapBundle.put(key, calculator.mod(value).getFloat());
		}
		return this;
	}

	public Calculator calc(String key) {
		Object data = _mapBundle.get(key);
		return new Calculator(data);
	}

	@Override
	public String toString() {
		return _mapBundle.toString();
	}
}
