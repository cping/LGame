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

public class Pools<T> {

	private final ObjectMap<String, Pool<T>> _inPoolDic;

	public Pools() {
		_inPoolDic = new ObjectMap<String, Pool<T>>();
	}
	
	public Pools<T> recover(String sign, Pool<T> item) {
		_inPoolDic.put(sign, item);
		return this;
	}

	public Pool<T> getBySign(String sign) {
		return _inPoolDic.get(sign);
	}

	public boolean clearBySign(String sign) {
		return _inPoolDic.remove(sign) != null;
	}

	public void clear() {
		_inPoolDic.clear();
	}

	public int getFreeAll() {
		int count = 0;
		for (Pool<T> p : _inPoolDic.values()) {
			count += p.getFree();
		}
		return count;
	}
}
