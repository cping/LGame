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

import loon.utils.ObjectMap;
import loon.utils.TArray;

public class CCodeFix {

	final static class FileFix {

		public String fileName;

		public final ObjectMap<String, String> fixContexts = new ObjectMap<String, String>();

		public FileFix(String name, String src, String dst) {
			fileName = name;
			putFixReplace(src, dst);
		}

		public void putFixReplace(String src, String dst) {
			fixContexts.put(src, dst);
		}
	}

	public final TArray<FileFix> fixContexts = new TArray<FileFix>();

	public CCodeFix() {

		FileFix fix1 = new FileFix("file.c", "file, size, 0, where)", "file, size, 0, FILE_BEGIN)");

		fixContexts.add(fix1);
	}

	public TArray<FileFix> getFixList() {
		return fixContexts;
	}

}
