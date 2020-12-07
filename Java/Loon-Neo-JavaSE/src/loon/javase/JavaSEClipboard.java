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
package loon.javase;

import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import java.io.File;
import java.util.List;

import loon.Clipboard;

public class JavaSEClipboard extends Clipboard implements ClipboardOwner {

	@Override
	public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, Transferable contents) {

	}

	@Override
	public String getContent() {
		try {
			java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable contents = clipboard.getContents(null);
			if (contents != null) {
				if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					try {
						return (String) contents.getTransferData(DataFlavor.stringFlavor);
					} catch (Throwable ex) {
					}
				}
				if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					try {
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) contents.getTransferData(DataFlavor.javaFileListFlavor);
						StringBuilder buffer = new StringBuilder(128);
						for (int i = 0, n = files.size(); i < n; i++) {
							if (buffer.length() > 0)
								buffer.append('\n');
							buffer.append(files.get(i).toString());
						}
						return buffer.toString();
					} catch (RuntimeException ex) {
					}
				}
			}
		} catch (Throwable ex) {
		}
		return "";
	}

	@Override
	public void setContent(String content) {
		try {
			StringSelection stringSelection = new StringSelection(content);
			java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, this);
		} catch (Throwable ignored) {
		}
	}

}
