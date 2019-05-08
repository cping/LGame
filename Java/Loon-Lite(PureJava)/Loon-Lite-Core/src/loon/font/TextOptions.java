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
package loon.font;

import loon.HorizontalAlign;

/**
 * Text配置用类
 */
public class TextOptions {

	// 默认行间间隔
	public static float LEADING_DEFAULT = 5;

	protected AutoWrap _autoWrap;
	protected float _autoWrapWidth;
	protected float _leading;
	protected HorizontalAlign _horizontalAlign;

	public static TextOptions LEFT() {
		return new TextOptions(HorizontalAlign.LEFT);
	}

	public static TextOptions RIGHT() {
		return new TextOptions(HorizontalAlign.RIGHT);
	}

	public static TextOptions CENTER() {
		return new TextOptions(HorizontalAlign.CENTER);
	}

	public static TextOptions VERTICAL_CENTER() {
		return new TextOptions(AutoWrap.VERTICAL, 0, HorizontalAlign.CENTER, LEADING_DEFAULT);
	}

	public static TextOptions VERTICAL_LEFT() {
		return new TextOptions(AutoWrap.VERTICAL, 0, HorizontalAlign.LEFT, LEADING_DEFAULT);
	}

	public static TextOptions VERTICAL_RIGHT() {
		return new TextOptions(AutoWrap.VERTICAL, 0, HorizontalAlign.RIGHT, LEADING_DEFAULT);
	}

	public TextOptions() {
		this(AutoWrap.NONE, 0, HorizontalAlign.LEFT, LEADING_DEFAULT);
	}

	public TextOptions(final HorizontalAlign horizontalAlign) {
		this(AutoWrap.NONE, 0, horizontalAlign, LEADING_DEFAULT);
	}

	public TextOptions(final AutoWrap autoWrap, final float autoWrapWidth) {
		this(autoWrap, autoWrapWidth, HorizontalAlign.LEFT, LEADING_DEFAULT);
	}

	public TextOptions(final AutoWrap autoWrap, final float autoWrapWidth, final HorizontalAlign horizontalAlign) {
		this(autoWrap, autoWrapWidth, horizontalAlign, LEADING_DEFAULT);
	}

	public TextOptions(final AutoWrap autoWrap, final float autoWrapWidth, final HorizontalAlign horizontalAlign,
			final float leading) {
		this._autoWrap = autoWrap;
		this._autoWrapWidth = autoWrapWidth;
		this._horizontalAlign = horizontalAlign;
		this._leading = leading;
	}

	public AutoWrap getAutoWrap() {
		return this._autoWrap;
	}

	public void setAutoWrap(final AutoWrap autoWrap) {
		this._autoWrap = autoWrap;
		return;
	}

	public float getAutoWrapWidth() {
		return this._autoWrapWidth;
	}

	public void setAutoWrapWidth(final float autoWrapWidth) {
		this._autoWrapWidth = autoWrapWidth;
	}

	public float getLeading() {
		return this._leading;
	}

	public void setLeading(final float leading) {
		this._leading = leading;
	}

	public HorizontalAlign getHorizontalAlign() {
		return this._horizontalAlign;
	}

	public void setHorizontalAlign(final HorizontalAlign horizontalAlign) {
		if (horizontalAlign == null) {
			this._horizontalAlign = HorizontalAlign.LEFT;
		} else {
			this._horizontalAlign = horizontalAlign;
		}
	}

}
