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
package loon.cport.builder;

import loon.teavm.builder.AssetFilter;
import loon.teavm.builder.AssetFilterOption;

public class DefaultAssetFilter implements AssetFilter {
	@Override
	public boolean accept(String file, boolean isDirectory, AssetFilterOption op) {
		if (isDirectory && file.endsWith(".svn")) {
			return false;
		}
		if (file.endsWith(".bak")) {
			return false;
		}
		if (file.endsWith(".jar")) {
			return false;
		}
		if (file.endsWith("assets.txt")) {
			return false;
		}
		return true;
	}
}