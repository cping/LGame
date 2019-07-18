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
package loon.action.map;

import loon.LRelease;
import loon.geom.Vector2f;
import loon.utils.TArray;

/**
 * 交给用户自定义行走路径的对象移动路径存储用类,此类没有寻径能力,仅仅是用来记录一组固定的行走路线
 */
public class CustomPath implements LRelease {

	private TArray<Vector2f> steps = new TArray<Vector2f>();

	public CustomPath() {
	}

	public int getLength() {
		return steps.size;
	}

	public Vector2f getStep(int index) {
		if (index < 0 || index >= steps.size) {
			return null;
		}
		return steps.get(index);
	}

	public Vector2f get(int index) {
		return getStep(index);
	}

	public float getX(int index) {
		return getStep(index).getX();
	}

	public float getY(int index) {
		return getStep(index).getY();
	}

	public CustomPath append(int x, int y) {
		steps.add(new Vector2f(x, y));
		return this;
	}

	public CustomPath prepend(int x, int y) {
		steps.add(new Vector2f(x, y));
		return this;
	}

	public CustomPath remove(Vector2f step) {
		steps.removeValue(step, true);
		return this;
	}

	public Vector2f pop() {
		return steps.pop();
	}

	public CustomPath add(Vector2f step) {
		steps.add(step);
		return this;
	}

	public boolean contains(int x, int y) {
		return steps.contains(new Vector2f(x, y), false);
	}

	public CustomPath clear() {
		steps.clear();
		return this;
	}

	@Override
	public void close() {
		clear();
	}

}
