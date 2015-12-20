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

import java.io.IOException;
import java.io.InputStream;

import loon.LSystem;
import loon.core.resource.Resources;

public class Typeface {
	public static final Typeface DEFAULT = new Typeface();
	public static final Typeface DEFAULT_BOLD = new Typeface();
	public static final Typeface MONOSPACE = new Typeface();
	public static final int NORMAL = 0;
	public static final int BOLD = 1;
	public static final int ITALIC = 2;
	public static final int BOLD_ITALIC = 3;

	public int opt = NORMAL;

	public LFont font = LFont.getDefaultFont();

	public Typeface() {
		this.font = LFont.getDefaultFont();
	}
	
	public Typeface(LFont f) {
		this.font = f;
	}

	
	public Typeface(int opt) {
		this.opt = opt;
		this.font = LFont.getDefaultFont();
	}

	public static Typeface create(Typeface face, int opt) {
		Typeface t = new Typeface(opt);
		t.font = face.font;
		return t;
	}

	public static LFont loadFont(String fileName) {
		LFont font = null;
		InputStream is = null;
		try {
			is = Resources.openResource(fileName);
			if (is == null) {
				font = LFont.getFont("Dialog", 0, 20);
			} else {
				font = LFont.getFileFont(fileName, 0, 20);
			}
		} catch (IOException e) {
			font = LFont.getFont("Dialog", 0, 20);
		}
		if (is != null) {
			LSystem.close(is);
		}
		return font;
	}

	public static Typeface createFromAsset(Object assets, String fileName) {
		Typeface face = new Typeface();
		face.font = Typeface.loadFont(fileName);
		return face;
	}

}
