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
package loon.cport.bridge.controller;

public class SDLAxis implements SDLKeybindValue {

	public SDLKey min, max;

	public SDLKey key;

	public SDLAxis(SDLKey key) {
		this.key = key;
		this.min = max = null;
	}

	public SDLAxis(SDLKey min, SDLKey max) {
		this.min = min;
		this.max = max;
		this.key = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SDLAxis axis = (SDLAxis) o;
		return min.equals(axis.min) && max.equals(axis.max) && key.equals(axis.key);
	}
}