/**
 * Copyright 2008 - 2020 The Loon Game Engine Authors
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
package loon.lwjgl;

import loon.Clipboard;
import loon.LGame;
import loon.LSystem;

public class Lwjgl3Clipboard extends Clipboard {

	@Override
	public String getContent() {
		LGame game = LSystem.base();
		if (game != null) {
			return org.lwjgl.glfw.GLFW.glfwGetClipboardString(((Lwjgl3Game) game).getWindowHandle());
		}
		return null;
	}

	@Override
	public void setContent(String content) {
		LGame game = LSystem.base();
		if (game != null) {
			org.lwjgl.glfw.GLFW.glfwSetClipboardString(((Lwjgl3Game) game).getWindowHandle(), content);
		}
	}

}
