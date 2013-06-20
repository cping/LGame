package loon.action.scripting.pack;

import java.util.ArrayList;

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
public class PackAnimation {

	private String name;

	private ArrayList<PackFrame> frames = new ArrayList<PackFrame>();

	private boolean looped;

	public PackAnimation(String name, boolean looped) {
		this.name = name;
		this.looped = looped;
	}

	public int size() {
		return frames.size();
	}

	public PackFrame getFrame(int index) {
		if (frames.size() == 0) {
			return frames.get(0);
		}
		if (looped == true) {
			return frames.get(index % frames.size());
		} else {
			if (index >= frames.size()) {
				return frames.get(frames.size() - 1);
			} else {
				return frames.get(index % frames.size());
			}
		}
	}

	public String getName() {
		return name;
	}

	public void addFrame(PackFrame frame) {
		frames.add(frame);
	}

	public boolean isLoop() {
		return looped;
	}

	public void setLoop(boolean loop) {
		this.looped = loop;
	}

}
