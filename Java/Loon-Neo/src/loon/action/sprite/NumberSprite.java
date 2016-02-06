package loon.action.sprite;

import loon.LObject;
import loon.LTexture;
import loon.LTrans;
import loon.action.ActionBind;
import loon.action.map.Field2D;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.font.IFont;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/*
 * 显示大写数字
 * 
 * example:
 *  NumberSprite number = new NumberSprite("1334");
 *	number.setLocation(125, 125);
 *	add(number);
 * 
 */
public class NumberSprite extends LObject implements ActionBind, ISprite,
		LTrans, BoxSize {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean visible = true;

	private String label;
	// 0
	public static final int[][] ZERO = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 0, 1 },
			{ 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, };

	// 1
	public static final int[][] ONE = { { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 },
			{ 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, };

	// 2
	public static final int[][] TWO = { { 1, 1, 1 }, { 0, 0, 1 }, { 1, 1, 1 },
			{ 1, 0, 0 }, { 1, 0, 0 }, { 1, 1, 1 }, };

	// 3
	public static final int[][] THREE = { { 1, 1, 1 }, { 0, 0, 1 },
			{ 1, 1, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 1, 1, 1 }, };

	// 4
	public static final int[][] FOUR = { { 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 },
			{ 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, };

	// 5
	public static final int[][] FIVE = { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 1, 1 },
			{ 0, 0, 1 }, { 0, 0, 1 }, { 1, 1, 1 }, };

	// 6
	public static final int[][] SIX = { { 1, 1, 1 }, { 1, 0, 0 }, { 1, 1, 1 },
			{ 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, };

	// 7
	public static final int[][] SEVEN = { { 1, 1, 1 }, { 0, 0, 1 },
			{ 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, };

	// 8
	public static final int[][] EIGHT = { { 1, 1, 1 }, { 1, 0, 1 },
			{ 1, 1, 1 }, { 1, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, };

	// 9
	public static final int[][] NINE = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 },
			{ 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 }, };

	private int unit;

	private LColor color;

	private float scaleX = 1f, scaleY = 1f;

	public NumberSprite(String mes) {
		this(mes, LColor.white);
	}

	public NumberSprite(String mes, LColor color) {
		this(mes, color, 5);
	}

	public NumberSprite(String mes, int unit) {
		this(mes, LColor.white, unit);
	}

	public NumberSprite(String mes, LColor color, int unit) {
		this.label = mes;
		this.color = color;
		this.unit = unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public void setColor(LColor color) {
		if (color == null) {
			return;
		}
		this.color = color;
	}

	public int getUnit() {
		return unit;
	}

	private int[][] getNum(int num) {
		if (num == 0) {
			return ZERO;
		} else if (num == 1) {
			return ONE;
		} else if (num == 2) {
			return TWO;
		} else if (num == 3) {
			return THREE;
		} else if (num == 4) {
			return FOUR;
		} else if (num == 5) {
			return FIVE;
		} else if (num == 6) {
			return SIX;
		} else if (num == 7) {
			return SEVEN;
		} else if (num == 8) {
			return EIGHT;
		} else if (num == 9) {
			return NINE;
		} else {
			return ZERO;
		}
	}

	public void drawNumber(GLEx g, int x, int y, int[][] num) {
		int tmp = g.color();
		g.setColor(color);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
				if (num[j][i] == 1) {
					g.fillRect(x + (unit * i), y + (unit * j), unit, unit);
				}
			}
		}
		g.setColor(tmp);
	}

	public void drawNumber(GLEx g, int x, int y, String num) {
		if (StringUtils.isEmpty(num)) {
			return;
		}
		int index, size = unit * 9, offset = unit * 2;
		for (int i = 0; i < num.length(); i++) {
			String number = num.substring(i, i + 1);
			if (MathUtils.isNan(number)) {
				index = Integer.parseInt(number);
				drawNumber(g, x + (unit * (4 * i)), y, getNum(index));
			} else {
				if (StringUtils.isChinese(number.charAt(0))) {
					size = unit * 5;
					offset = unit * 2;
					IFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2
							+ offset + 4);
					g.setFont(oldFont);

				} else {
					IFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2
							+ offset);
					g.setFont(oldFont);
				}
			}
		}
	}

	public void drawNumber(Canvas g, int x, int y, int[][] num) {
		int tmp1 = g.getFillColor();
		int tmp2 = g.getStrokeColor();
		g.setColor(color);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
				if (num[j][i] == 1) {
					g.fillRect(x + (unit * i), y + (unit * j), unit, unit);
				}
			}
		}
		g.setFillColor(tmp1);
		g.setStrokeColor(tmp2);
	}

	public void drawNumber(Canvas g, int x, int y, String num) {
		if (StringUtils.isEmpty(num)) {
			return;
		}
		int index, size = unit * 10, offset = unit / 2;
		for (int i = 0; i < num.length(); i++) {
			String number = num.substring(i, i + 1);
			if (MathUtils.isNan(number)) {
				index = Integer.parseInt(number);
				drawNumber(g, x + (unit * (4 * i)), y, getNum(index));
			} else {
				int tmp1 = g.getFillColor();
				int tmp2 = g.getStrokeColor();
				g.setColor(color);
				if (StringUtils.isChinese(number.charAt(0))) {
					size = unit * 5;
					offset = unit * 2;
					LFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2
							+ offset + 4 - g.getFont().getAscent());
					g.setFont(oldFont);
				} else {
					LFont oldFont = g.getFont();
					g.setFont(LFont.getFont(size));
					g.drawText(number, x + (unit * (4 * i)) - 4, y + size / 2
							+ offset - g.getFont().getAscent() + 4);
					g.setFont(oldFont);
				}
				g.setFillColor(tmp1);
				g.setStrokeColor(tmp2);
			}
		}
	}

	@Override
	public void setWidth(float w) {
		this.unit = (int) MathUtils.max(unit, w);
	}

	@Override
	public void setHeight(float h) {
		this.unit = (int) MathUtils.max(unit, h);
	}

	LTexture tex;

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			drawNumber(g, x(), y(), label);
		}
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public void setVisible(boolean v) {
		this.visible = v;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public float getScaleX() {
		return scaleX;
	}

	@Override
	public float getScaleY() {
		return scaleY;
	}

	@Override
	public void setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return false;
	}

	@Override
	public RectBox getRectBox() {
		return getRect(x(), y(), unit, unit);
	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public float getWidth() {
		return unit;
	}

	@Override
	public float getHeight() {
		return unit;
	}

	@Override
	public void close() {

	}

}
