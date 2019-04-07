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

/**
 * 简单的四则运算类,脚本用
 */
public class Calculator {

	private final static int ADD = 0, SUBTRACT = 1, MULTIPLY = 2, DIVIDE = 3, EQUAL = 4;

	private float currentTotal;

	public Calculator() {
		this(0);
	}

	public Calculator(Object num) {
		this(convertObjectToFloat(num));
	}

	public Calculator(float num) {
		this.currentTotal = num;
	}

	protected static final float convertObjectToFloat(Object num) {
		if (num == null) {
			return -1f;
		}
		float value;
		if (num instanceof Number) {
			value = ((Number) num).floatValue();
		} else {
			String mes = num.toString();
			if (MathUtils.isNan(mes)) {
				value = Float.valueOf(mes);
			} else {
				value = -1f;
			}
		}
		return value;
	}

	public Calculator add(Object number) {
		return add(convertObjectToFloat(number));
	}

	public Calculator sub(Object number) {
		return sub(convertObjectToFloat(number));
	}

	public Calculator mul(Object number) {
		return mul(convertObjectToFloat(number));
	}

	public Calculator div(Object number) {
		return div(convertObjectToFloat(number));
	}

	public Calculator equal(Object number) {
		return div(convertObjectToFloat(number));
	}

	public Calculator add(String number) {
		return convertToFloat(number, ADD);
	}

	public Calculator sub(String number) {
		return convertToFloat(number, SUBTRACT);
	}

	public Calculator mul(String number) {
		return convertToFloat(number, MULTIPLY);
	}

	public Calculator div(String number) {
		return convertToFloat(number, DIVIDE);
	}

	private Calculator convertToFloat(String number, int operator) {
		if (MathUtils.isNan(number)) {
			float dblNumber = Float.valueOf(number);
			switch (operator) {
			case ADD:
				return add(dblNumber);
			case SUBTRACT:
				return sub(dblNumber);
			case MULTIPLY:
				return mul(dblNumber);
			case DIVIDE:
				return div(dblNumber);
			case EQUAL:
				return equal(dblNumber);
			default:
				break;
			}
		}
		return this;
	}

	public Calculator add(float number) {
		currentTotal += number % 1f == 0 ? (int) number : number;
		return this;
	}

	public Calculator sub(float number) {
		currentTotal -= number % 1f == 0 ? (int) number : number;
		return this;
	}

	public Calculator mul(float number) {
		currentTotal *= number % 1f == 0 ? (int) number : number;
		return this;
	}

	public Calculator div(float number) {
		currentTotal /= number % 1f == 0 ? (int) number : number;
		return this;
	}

	public Calculator equal(float number) {
		currentTotal = number;
		return this;
	}

	public Calculator equal(String number) {
		if (MathUtils.isNan(number)) {
			currentTotal = Float.valueOf(number);
		}
		return this;
	}

	public int getInt() {
		return (int) currentTotal;
	}

	public float getFloat() {
		return currentTotal;
	}

	@Override
	public String toString() {
		return currentTotal % 1f == 0 ? Integer.toString(getInt()) : String.valueOf(currentTotal);
	}

}
