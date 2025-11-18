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
package loon.teavm;

import org.teavm.jso.JSBody;

import loon.Clipboard;
import loon.teavm.dom.TeaClipboardEvent;
import loon.teavm.dom.TeaDataTransfer;

public class TeaClipboard extends Clipboard {

	private boolean _writePermissions = false;
	private boolean _hasWritePermissions = true;

	private final ClipboardWriteHandler _writeHandler = new ClipboardWriteHandler();

	private String _content = "";

	public TeaClipboard() {
		TeaApp.get().getDocument().addEventListener("copy", evt -> {
			TeaClipboardEvent event = (TeaClipboardEvent) evt;
			TeaDataTransfer clipboardData = event.getClipboardData();
			if (clipboardData != null) {
				clipboardData.setData("text/plain", _content);
			}
			evt.preventDefault();
		});

		TeaApp.get().getDocument().addEventListener("cut", evt -> {
			TeaClipboardEvent event = (TeaClipboardEvent) evt;
			TeaDataTransfer clipboardData = event.getClipboardData();
			if (clipboardData != null) {
				clipboardData.setData("text/plain", _content);
			}
			evt.preventDefault();
		});

		TeaApp.get().getDocument().addEventListener("paste", evt -> {
			TeaClipboardEvent event = (TeaClipboardEvent) evt;
			TeaDataTransfer clipboardData = event.getClipboardData();
			if (clipboardData != null) {
				_content = clipboardData.getData("text/plain");
			}
			evt.preventDefault();
		});
	}

	@JSBody(params = { "content" }, script = "if (\"clipboard\" in navigator) {\n"
			+ "    navigator.clipboard.writeText(content);\n" + "}")
	private static native void setContentNATIVE(String content);

	private class ClipboardWriteHandler implements TeaPermissions.TeaPermissionResult {
		@Override
		public void granted() {
			_hasWritePermissions = true;
			setContentNATIVE(_content);
		}

		@Override
		public void denied() {
			_hasWritePermissions = false;
		}

		@Override
		public void prompt() {
			_hasWritePermissions = true;
			setContentNATIVE(_content);
		}
	}

	@Override
	public String getContent() {
		return _content;
	}

	@Override
	public void setContent(String content) {
		this._content = content;
		if (_writePermissions || TeaApp.get().getAgentInfo().isFirefox()) {
			if (_hasWritePermissions)
				setContentNATIVE(content);
		} else {
			TeaPermissions.queryPermission("clipboard-write", _writeHandler);
			_writePermissions = true;
		}
	}
}
