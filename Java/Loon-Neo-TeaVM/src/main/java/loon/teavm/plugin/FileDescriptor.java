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
package loon.teavm.plugin;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {
	private List<FileDescriptor> childFiles = new ArrayList<FileDescriptor>();
	private String name;
	private String path;
	private boolean directory;
	private long length = 0;

	public List<FileDescriptor> getChildFiles() {
		return childFiles;
	}

	public void setChildFiles(List<FileDescriptor> childFiles) {
		this.childFiles = childFiles;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String p) {
		path = p;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public void setLength(long len) {
		this.length = len;
	}

	public long getLength() {
		return length;
	}
}
