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

public enum MemoryMode {

	MINIMALIST("Minimalist (4MB - 64MB)", 4, 64), MINIMUM("Minimum (8MB - 128MB)", 8, 128),
	NORMAL("Normal (16MB - 512MB)", 16, 512), MAXIMUM("Maximum (32MB - 1024MB)", 32, 1024),
	EXTREME("Extreme (64MB - 2047MB)", 64, 2047);

	protected final String description;
	protected final int minSize;
	protected final int maxSize;

	MemoryMode(String description, int minSize, int maxSize) {
		this.description = description;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	public String getDescription() {
		return description;
	}

	public int getMinSize() {
		return minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	@Override
	public String toString() {
		return description;
	}
}