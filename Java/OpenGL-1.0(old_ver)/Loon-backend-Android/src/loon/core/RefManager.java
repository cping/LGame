/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core;

import java.util.ArrayList;
import java.util.Arrays;

import loon.utils.CollectionUtils;
//引用管理器，作用是保存实现了LRelease接口的对象，然后统一释放。
public class RefManager implements LRelease {

	public ArrayList<LRelease> objects = new ArrayList<LRelease>(
			CollectionUtils.INITIAL_CAPACITY);

	public RefManager() {
	}

	public RefManager(LRelease... res) {
		objects.addAll(Arrays.asList(res));
	}

	public RefManager(ArrayList<LRelease> res) {
		objects.addAll(res);
	}

	public boolean add(LRelease res) {
		return objects.add(res);
	}

	@Override
	public void dispose() {
		for (LRelease release : objects) {
			if (release != null) {
				release.dispose();
			}
		}
		objects.clear();
	}

}
