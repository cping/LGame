package loon.action.scripting.pack;

import loon.core.graphics.opengl.LTexturePack.PackEntry;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class PackFrame {

	 int id;

	 String name;

	 int width;

	 int height;

	 boolean flag;

	public PackFrame(PackEntry p) {
		this(p.id(), p.name(), p.width(), p.height());
	}

	public PackFrame(int id, String name, int width, int height) {
		this.id = id;
		this.name = name;
		this.flag = (name == null) || ("".equals(name));
		this.width = width;
		this.height = height;
	}

	public boolean isFlag() {
		return flag;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
