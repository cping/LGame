package loon.core.graphics.component;

import loon.LSystem;
import loon.action.sprite.ISprite;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.LTimer;

/**
 * Example:
 * 
 * LToast toast = LToast.makeText(null, "ABCDEFG",Style.ERROR); add(toast);
 * 
 */
public class LToast extends LComponent {

	public enum Style {
		NORMAL, SUCCESS, ERROR
	};

	public static LToast makeText(String text) {
		return makeText(LFont.getDefaultFont(), null, text, LENGTH_SHORT);
	}

	public static LToast makeText(String text, Style style) {
		return makeText(LFont.getDefaultFont(), null, text, LENGTH_SHORT, style);
	}

	public static LToast makeText(String text, int duration) {
		return makeText(LFont.getDefaultFont(), null, text, duration,
				Style.NORMAL);
	}

	public static LToast makeText(LComponent owner, String text) {
		return makeText(LFont.getDefaultFont(), owner, text, LENGTH_SHORT);
	}

	public static LToast makeText(LComponent owner, String text, Style style) {
		return makeText(LFont.getDefaultFont(), owner, text, LENGTH_SHORT,
				style);
	}

	public static LToast makeText(LComponent owner, String text, int duration) {
		return makeText(LFont.getDefaultFont(), owner, text, duration,
				Style.NORMAL);
	}

	public static LToast makeText(LFont font, LComponent owner, String text) {
		return makeText(font, owner, text, LENGTH_SHORT);
	}

	public static LToast makeText(LFont font, LComponent owner, String text,
			Style style) {
		return makeText(font, owner, text, LENGTH_SHORT, style);
	}

	public static LToast makeText(LFont font, LComponent owner, String text,
			int duration) {
		return makeText(font, owner, text, duration, Style.NORMAL);
	}

	public static LToast makeText(LFont font, LComponent owner, String text,
			int duration, Style style) {
		LToast toast = null;
		if (owner != null) {
			if (owner instanceof LToast) {
				return (LToast) owner;
			} else if (owner instanceof LContainer) {
				toast = new LToast(font, text, duration, owner.x(), owner.y(),
						owner.getWidth(), owner.getHeight());
				((LContainer) owner).add(toast);
			} else {
				toast = new LToast(font, text, duration, owner.x(), owner.y(),
						owner.getWidth(), owner.getHeight());
			}
		} else {
			toast = new LToast(font, text, duration, 0, 0,
					LSystem.screenRect.width, LSystem.screenRect.height);
		}
		if (style == Style.SUCCESS) {
			toast.mBackgroundColor = SUCCESS_GRAY;
		}
		if (style == Style.ERROR) {
			toast.mBackgroundColor = ERROR_RED;
		}
		if (style == Style.NORMAL) {
			toast.mBackgroundColor = NORMAL_ORANGE;
		}
		return toast;
	}

	private boolean stop = false;
	public static final int LENGTH_SHORT = 30;
	public static final int LENGTH_LONG = 60;
	public static final LColor ERROR_RED = LColor.maroon;
	public static final LColor SUCCESS_GRAY = LColor.gray;
	public static final LColor NORMAL_ORANGE = LColor.orange;
	private final float MAX_OPACITY = 1.0f;
	private final float OPACITY_INCREMENT = 0.05f;
	private int _frame_radius = 15;
	private int _frame_length_multiplier = 10;
	private float opacity = 0;
	private String mText;
	private int mDuration;
	private LTimer timer = new LTimer();
	private LTimer lock = new LTimer(LSystem.SECOND * 2);
	private LColor mBackgroundColor = LColor.orange;
	private LColor mForegroundColor = LColor.white;
	private LFont font;
	private int displayX = 0;
	private int displayY = 0;
	private int cellHeight = 30;
	private int cellWidth = 30;
	private int mType;
	private boolean autoClose = true;

	public LToast(LFont font, String text, int duration, int x, int y,
			int width, int height) {
		super(x, y, width, height);
		this.mType = ISprite.TYPE_FADE_IN;
		this.opacity = 0f;
		this.mDuration = duration;
		this.font = font;
		this.mText = text;
		this.cellWidth = font.stringWidth(mText)
				+ (_frame_length_multiplier * 10);
		this.cellHeight = font.getHeight() + 10;
		if (this.cellHeight < 30) {
			this.cellHeight = 30;
		}
		this.displayX = x + ((width / 2) - (cellWidth / 2));
		this.displayY = (y + ((height / 2) - (cellHeight / 2)))
				- font.getHeight() / 2;
		this.setSize(cellWidth, cellHeight);
		timer.setDelay(this.mDuration);
	}

	public void fadeIn() {
		this.mType = ISprite.TYPE_FADE_IN;
		this.opacity = 0f;
	}

	public void fadeOut() {
		this.mType = ISprite.TYPE_FADE_OUT;
		this.opacity = MAX_OPACITY;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (!isVisible()) {
			return;
		}
		int w = this.getWidth();
		int h = this.getHeight();
		float old = g.getAlpha();
		g.setAlpha(opacity);
		g.setColor(mBackgroundColor);
		g.fillRoundRect(displayX, displayY, w, h, _frame_radius);
		g.setColor(LColor.white);
		g.setAlpha(old);
		g.setFont(font);
		g.drawString(mText, displayX + (cellWidth - font.stringWidth(mText))
				/ 2, displayY + font.getHeight(), mForegroundColor);
		g.resetColor();
		g.resetFont();
	}

	public void update(long elapsedTime) {
		super.update(elapsedTime);
		if (timer.action(elapsedTime)) {
			if (mType == ISprite.TYPE_FADE_IN) {
				opacity += OPACITY_INCREMENT;
				opacity = (Math.min(opacity, MAX_OPACITY));
				if (opacity >= MAX_OPACITY) {
					stop = true;
				}
			} else {
				opacity -= OPACITY_INCREMENT;
				opacity = (Math.max(opacity, 0));
				if (opacity <= 0) {
					stop = true;
					setVisible(false);
					dispose();
				}
			}
		}
		if (stop && autoClose && lock.action(elapsedTime)) {
			fadeOut();
		}
	}

	public void setText(String text) {
		mText = text;
	}

	public void setDuration(int duration) {
		this.mDuration = duration;
		timer.setDelay(this.mDuration);
	}

	public void setBackground(LColor backgroundColor) {
		mBackgroundColor = backgroundColor;
	}

	public void setForeground(LColor foregroundColor) {
		mForegroundColor = foregroundColor;
	}

	public boolean isStop() {
		return stop;
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	@Override
	public String getUIName() {
		return "Toast";
	}

}
