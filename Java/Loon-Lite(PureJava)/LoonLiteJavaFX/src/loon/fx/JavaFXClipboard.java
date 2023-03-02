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
package loon.fx;

import javafx.scene.input.ClipboardContent;
import loon.Clipboard;

public class JavaFXClipboard extends Clipboard {

	@Override
	public String getContent() {
		javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
		if (clipboard.hasString()) {
			return clipboard.getString();
		}
		return null;
	}

	@Override
	public void setContent(String content) {
		javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
		ClipboardContent javafxcontent = new ClipboardContent();
		javafxcontent.putString(content);
		clipboard.setContent(javafxcontent);
	}

}
