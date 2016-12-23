package loon.font;

import loon.HorizontalAlign;

/**
 * Text配置用类
 */
public class TextOptions {

	//默认行间间隔
	public static float LEADING_DEFAULT = 5;
	AutoWrap _autoWrap;
	float _autoWrapWidth;
	float _leading;
	HorizontalAlign _horizontalAlign;
	
	public static TextOptions LEFT(){
		return new TextOptions(HorizontalAlign.LEFT);
	}

	public static TextOptions RIGHT(){
		return new TextOptions(HorizontalAlign.RIGHT);
	}

	public static TextOptions CENTER(){
		return new TextOptions(HorizontalAlign.CENTER);
	}
	
	public static TextOptions VERTICAL_CENTER(){
		return new TextOptions(AutoWrap.VERTICAL, 0, HorizontalAlign.CENTER, LEADING_DEFAULT);
	}

	public static TextOptions VERTICAL_LEFT(){
		return new TextOptions(AutoWrap.VERTICAL, 0, HorizontalAlign.LEFT, LEADING_DEFAULT);
	}

	public static TextOptions VERTICAL_RIGHT(){
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

	public TextOptions(final AutoWrap autoWrap, final float autoWrapWidth,
			final HorizontalAlign horizontalAlign, final float leading) {
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
		this._horizontalAlign = horizontalAlign;
	}

}
