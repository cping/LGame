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
package loon.android;

import loon.Clipboard;

public class AndroidClipboard extends Clipboard {

	private android.content.ClipboardManager clipboard;

	public AndroidClipboard(android.content.Context context) {
		clipboard = (android.content.ClipboardManager) context
				.getSystemService(android.content.Context.CLIPBOARD_SERVICE);
	}

	@Override
	public String getContent() {
		android.content.ClipData clip = clipboard.getPrimaryClip();
		if (clip == null) {
			return null;
		}
		CharSequence text = clip.getItemAt(0).getText();
		if (text == null) {
			return null;
		}
		return text.toString();
	}

	@Override
	public void setContent(final String content) {
		android.content.ClipData data = android.content.ClipData.newPlainText(content, content);
		clipboard.setPrimaryClip(data);
	}

}
