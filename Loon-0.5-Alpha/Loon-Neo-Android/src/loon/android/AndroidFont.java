/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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

import java.util.EnumMap;
import java.util.Map;

import loon.font.Font;
import android.graphics.Paint;
import android.graphics.Typeface;

class AndroidFont {

	private static final String[] NO_HACKS = {};

	public static final AndroidFont DEFAULT = new AndroidFont(Typeface.DEFAULT,
			14, null);

	public final Typeface typeface;
	public final float size;
	public final String[] resources;
	protected Paint paint;

	public AndroidFont(Typeface typeface, float size, String[] res) {
		this.typeface = typeface;
		this.size = size;
		this.resources = (res != null) ? res : NO_HACKS;
	}

	public static Typeface create(Font font) {
		String familyName = font.name;
		if (familyName != null) {
			if (familyName.equalsIgnoreCase("Serif")
					|| familyName.equalsIgnoreCase("TimesRoman")) {
				familyName = "serif";
			} else if (familyName.equalsIgnoreCase("SansSerif")
					|| familyName.equalsIgnoreCase("Helvetica")) {
				familyName = "sans-serif";
			} else if (familyName.equalsIgnoreCase("Monospaced")
					|| familyName.equalsIgnoreCase("Courier")
					|| familyName.equalsIgnoreCase("Dialog")) {
				familyName = "monospace";
			}
		}
		return Typeface.create(familyName, TO_ANDROID_STYLE.get(font.style));
	}

	protected static final Map<Font.Style, Integer> TO_ANDROID_STYLE = new EnumMap<Font.Style, Integer>(
			Font.Style.class);
	static {
		TO_ANDROID_STYLE.put(Font.Style.PLAIN, Typeface.NORMAL);
		TO_ANDROID_STYLE.put(Font.Style.BOLD, Typeface.BOLD);
		TO_ANDROID_STYLE.put(Font.Style.ITALIC, Typeface.ITALIC);
		TO_ANDROID_STYLE.put(Font.Style.BOLD_ITALIC, Typeface.BOLD_ITALIC);
	}
}
