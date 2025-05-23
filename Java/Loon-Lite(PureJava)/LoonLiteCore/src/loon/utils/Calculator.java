/**
 * 
 * Copyright 2008 - 2009
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
 * @version 0.1.1
 */
package loon.utils;

import loon.LRelease;
import loon.LSystem;
import loon.utils.reply.TValue;

/**
 * 简单的四则运算类,脚本用,也可用于跨域传参(单纯跨域传参推荐使用 @see Counter 类,因为该类可以限制最大和最小值,更适合简单增减用).
 */
public class Calculator implements LRelease {

	private final static float convertObjectToFloat(Object num) {
		if (num == null) {
			return -1f;
		}
		float value;
		if (num instanceof Number) {
			value = HelperUtils.toFloat(num);
		} else {
			final String mes = num.toString();
			if (MathUtils.isNan(mes)) {
				value = Float.parseFloat(mes);
			} else {
				value = -1f;
			}
		}
		return value;
	}

	public static final int ADD = 1;

	public static final int SUBTRACT = 2;

	public static final int MULTIPLY = 3;

	public static final int DIVIDE = 4;

	public static final int MODULO = 5;

	public static final int EQUAL = 6;

	public static final int XOR = 7;

	private float currentTotal;

	public Calculator() {
		this(0f);
	}

	public Calculator(Object num) {
		this(convertObjectToFloat(num));
	}

	public Calculator(float num) {
		this.currentTotal = num;
	}

	public Calculator add(Object num) {
		return add(convertObjectToFloat(num));
	}

	public Calculator sub(Object num) {
		return sub(convertObjectToFloat(num));
	}

	public Calculator mul(Object num) {
		return mul(convertObjectToFloat(num));
	}

	public Calculator div(Object num) {
		return div(convertObjectToFloat(num));
	}

	public Calculator mod(Object num) {
		return mod(convertObjectToFloat(num));
	}

	public Calculator equal(Object num) {
		return div(convertObjectToFloat(num));
	}

	public Calculator add(String num) {
		return convertToFloat(num, ADD);
	}

	public Calculator sub(String num) {
		return convertToFloat(num, SUBTRACT);
	}

	public Calculator mul(String num) {
		return convertToFloat(num, MULTIPLY);
	}

	public Calculator div(String num) {
		return convertToFloat(num, DIVIDE);
	}

	public Calculator mod(String num) {
		return convertToFloat(num, MODULO);
	}

	public Calculator xor(String num) {
		return convertToFloat(num, XOR);
	}

	private Calculator convertToFloat(String num, int operator) {
		if (MathUtils.isNan(num)) {
			float dblNumber = Float.parseFloat(num);
			switch (operator) {
			case ADD:
				return add(dblNumber);
			case SUBTRACT:
				return sub(dblNumber);
			case MULTIPLY:
				return mul(dblNumber);
			case DIVIDE:
				return div(dblNumber);
			case MODULO:
				return mod(dblNumber);
			case EQUAL:
				return equal(dblNumber);
			case XOR:
				return xor(dblNumber);
			default:
				break;
			}
		}
		return this;
	}

	public Calculator add(Calculator c) {
		if (c == null) {
			return this;
		}
		return add(c.get());
	}

	public Calculator add(float num) {
		currentTotal += num % 1f == 0 ? (int) num : num;
		return this;
	}

	public Calculator sub(Calculator c) {
		if (c == null) {
			return this;
		}
		return sub(c.get());
	}

	public Calculator sub(float num) {
		currentTotal -= num % 1f == 0 ? (int) num : num;
		return this;
	}

	public Calculator mul(Calculator c) {
		if (c == null) {
			return this;
		}
		return mul(c.get());
	}

	public Calculator mul(float num) {
		currentTotal *= num % 1f == 0 ? (int) num : num;
		return this;
	}

	public Calculator div(Calculator c) {
		if (c == null) {
			return this;
		}
		return div(c.get());
	}

	public Calculator div(float num) {
		currentTotal /= num % 1f == 0 ? (int) num : num;
		return this;
	}

	public Calculator mod(Calculator c) {
		if (c == null) {
			return this;
		}
		return mod(c.get());
	}

	public Calculator mod(float num) {
		currentTotal %= num % 1f == 0 ? (int) num : num;
		return this;
	}

	public Calculator equal(Calculator c) {
		if (c == null) {
			return this;
		}
		return equal(c.get());
	}

	public Calculator equal(float num) {
		currentTotal = num;
		return this;
	}

	public Calculator equal(String num) {
		if (MathUtils.isNan(num)) {
			currentTotal = Float.parseFloat(num);
		}
		return this;
	}

	public Calculator xor(Calculator c) {
		if (c == null) {
			return this;
		}
		return xor(c.get());
	}

	public Calculator xor(float num) {
		currentTotal = MathUtils.pow(currentTotal, num);
		return this;
	}

	public Calculator set(float num) {
		return equal(num);
	}

	public Calculator set(String num) {
		return equal(num);
	}

	public TValue<Object> eval(final String op, final Object v) {
		return HelperUtils.eval(op, get(), convertObjectToFloat(v));
	}

	public int getInt() {
		return (int) currentTotal;
	}

	public float getFloat() {
		return currentTotal;
	}

	public float get() {
		return currentTotal;
	}

	@Override
	public int hashCode() {
		int result = 31;
		result = LSystem.unite(result, this.currentTotal);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof Calculator) {
			Calculator c = (Calculator) o;
			return MathUtils.equal(c.currentTotal, this.currentTotal);
		}
		return false;
	}

	@Override
	public String toString() {
		return currentTotal % 1f == 0 ? Integer.toString(getInt()) : String.valueOf(currentTotal);
	}

	@Override
	public void close() {
		this.currentTotal = 0f;
	}

}
