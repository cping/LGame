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

import loon.LSystem;

public class URecognizerResult {

	protected String _name;

	protected float _score;

	protected int _index;

	protected float _theta;

	public URecognizerResult() {
		this(LSystem.UNKNOWN, 0, 0);
	}

	public URecognizerResult(String name, float score, int index) {
		this(name, score, index, 0);
	}

	public URecognizerResult(String name, float score, int index, float theta) {
		this._name = name;
		this._score = score;
		this._index = index;
		this._theta = theta;
	}

	public String getName() {
		return _name;
	}

	public void setName(String n) {
		this._name = n;
	}

	public float getScore() {
		return _score;
	}

	public void setScore(float s) {
		this._score = s;
	}

	public int getIndex() {
		return _index;
	}

	public void setIndex(int i) {
		this._index = i;
	}

	public float getTheta() {
		return _theta;
	}

	public void setTheta(float t) {
		this._theta = t;
	}

}
