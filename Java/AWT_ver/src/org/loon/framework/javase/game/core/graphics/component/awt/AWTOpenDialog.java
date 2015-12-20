package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.FileDialog;
import java.awt.Frame;

import org.loon.framework.javase.game.core.LSystem;

/**
 * Copyright 2008 - 2009
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
public class AWTOpenDialog {

	private String fileName, dirName;

	private FileDialog dialog;

	public AWTOpenDialog(String title, String path) {
		open(title, path, null);
	}

	void open(String title, String path, String fileName) {
		dialog = new FileDialog((Frame) LSystem.getSystemHandler()
				.getWindow(), title);
		if (path != null) {
			dialog.setDirectory(path);
		}
		if (fileName != null) {
			dialog.setFile(fileName);
		}
		fileName = dialog.getFile();
		if (fileName == null) {
			if (LSystem.isMacOS()) {
				System.setProperty("apple.awt.fileDialogForDirectories",
						"false");
			}
		} else {
			dirName = dialog.getDirectory();
		}

	}

	public void setVisible(boolean visible) {
		dialog.setVisible(visible);
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
