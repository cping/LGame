/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.core.graphics.device;

public class Path {

	android.graphics.Path path2D;

	public Path() {
		this.path2D = new android.graphics.Path();
	}

	public Path(Path p) {
		this.path2D = p.path2D;
	}

	public void close() {
		this.path2D.close();
	}

	public void clear() {
		this.path2D.reset();
	}

	public void reset() {
		this.path2D.reset();
	}

	public void lineTo(float x, float y) {
		this.path2D.lineTo(x, y);
	}

	public void moveTo(float x, float y) {
		this.path2D.moveTo(x, y);
	}

}
