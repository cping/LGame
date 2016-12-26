package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.event.ValueListener;
import loon.opengl.GLEx;
import loon.opengl.LTextureRegion;

public class LProgress extends LComponent {

	private boolean vertical = false;

	// 默认提供了三种进度条模式，分别是游戏类血槽，普通的UI形式，以及用户自制图像.(默认为游戏模式)
	public enum ProgressType {
		GAME, UI, Custom
	}

	private static LTexture defaultColorTexture;
	private LTextureRegion bgTexture;
	private LTextureRegion bgTextureEnd;
	private LTextureRegion bgProgressTexture;
	private LTextureRegion bgProgressStart;

	private ValueListener listener;

	private float percentage = 1f;

	private LTextureRegion texture;

	private SpriteBatch batch;

	private ProgressType progressType;

	public LProgress(int x, int y, int width, int height) {
		this(ProgressType.GAME, LColor.red, x, y, width, height, null, null);
	}

	public LProgress(LColor baseColor, int x, int y, int width, int height) {
		this(ProgressType.GAME, baseColor, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, LColor baseColor, int x, int y,
			int width, int height) {
		this(type, baseColor, x, y, width, height, null, null);
	}

	public LProgress(ProgressType type, LColor baseColor, int x, int y,
			int width, int height, LTexture bg, LTexture bgProgress) {
		super(x, y, width, height);
		this.progressType = type;
		this.batch = new SpriteBatch();
		this.baseColor = baseColor;
		switch (progressType) {
		case GAME:
			this.texture = new LTextureRegion(LSystem.FRAMEWORK_IMG_NAME
					+ "bar.png");
			this.bgTexture = new LTextureRegion(texture.getTexture(),
					texture.getRegionX() + 3, texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			this.bgProgressTexture = new LTextureRegion(texture.getTexture(),
					texture.getRegionX() + 1, texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			this.bgProgressStart = new LTextureRegion(texture.getTexture(),
					texture.getRegionX(), texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			this.bgTextureEnd = new LTextureRegion(texture.getTexture(),
					texture.getRegionX() + 4, texture.getRegionY(), 1,
					texture.getRegionHeight() - 2);
			break;
		case UI:
			if (defaultColorTexture == null || defaultColorTexture.isClose()) {
				defaultColorTexture = LSystem.base().graphics().finalColorTex();
			}
			this.bgTexture = new LTextureRegion(DefUI.getDefaultTextures(4));
			this.bgProgressTexture = new LTextureRegion(defaultColorTexture);
			break;
		default:
			this.bgTexture = new LTextureRegion(bg);
			this.bgProgressTexture = new LTextureRegion(bgProgress);
			break;
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (batch != null) {
			batch.begin();
			draw(batch, x, y);
			batch.end();
		}
	}

	public void update(final long elapsedTime) {
		super.update(elapsedTime);
		if (listener != null) {
			listener.onChange(this, percentage);
		}
	}

	public void draw(SpriteBatch batch, int x, int y) {
		if (vertical) {
			float size = 0;
			switch (progressType) {
			case GAME:
				size = getWidth() * (1 - percentage);
				float posY = getHeight() / 2;
				batch.draw(bgTexture, x + getHeight() / 2 + getWidth() / 2, y
						- posY, size, getHeight(), 90);
				batch.setColor(baseColor);
				size = getWidth() * percentage;
				batch.draw(bgProgressTexture, x + getHeight() / 2 + getWidth()
						/ 2, y + getWidth() - size - posY, getWidth()
						* percentage, getHeight(), 90);
				batch.resetColor();
				break;
			case UI:
				batch.draw(bgTexture.getTexture(), x, y, getHeight(),
						getWidth());
				batch.setColor(baseColor);
				size = (getWidth() * percentage - 2);
				batch.draw(bgProgressTexture.getTexture(), x + 1, y
						+ getWidth() - size - 1, getHeight() - 2, size);
				batch.resetColor();
				break;
			default:
				batch.draw(bgTexture.getTexture(), x, y, getHeight(),
						getWidth());
				batch.setColor(baseColor);
				size = (getWidth() * percentage);
				batch.draw(bgProgressTexture.getTexture(), x, y + getWidth()
						- size, getHeight(), size);
				batch.resetColor();
				break;
			}
		} else {
			switch (progressType) {
			case GAME:
				batch.draw(bgTexture, x + getWidth() * percentage + 1, y,
						getWidth() * (1 - percentage), getHeight());
				batch.draw(bgTextureEnd, x + getWidth() + 1, y,
						bgTextureEnd.getRegionWidth(), getHeight());
				batch.setColor(baseColor);
				batch.draw(bgProgressTexture, x + 1, y,
						getWidth() * percentage, getHeight());
				batch.draw(bgProgressStart, x, y,
						bgProgressStart.getRegionWidth(), getHeight());
				batch.resetColor();
				break;
			case UI:
				batch.draw(bgTexture.getTexture(), x, y, getWidth(),
						getHeight());
				batch.setColor(baseColor);
				batch.draw(bgProgressTexture.getTexture(), x + 1, y + 1,
						getWidth() * percentage - 2, getHeight() - 2);
				batch.resetColor();
				break;
			default:
				batch.draw(bgTexture.getTexture(), x, y, getWidth(),
						getHeight());
				batch.setColor(baseColor);
				batch.draw(bgProgressTexture.getTexture(), x, y, getWidth()
						* percentage, getHeight());
				batch.resetColor();
				break;
			}
		}
	}

	public void setPercentage(float p) {
		if (p >= 0f && p <= 1f) {
			this.percentage = p;
		} else {
			if (p > 1f) {
				this.percentage = 1f;
			} else if (p < 0f) {
				this.percentage = 0f;
			}
		}
	}

	public boolean isVertical() {
		return vertical;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public float getPercentage() {
		return this.percentage;
	}

	@Override
	public void close() {
		super.close();
		if (texture != null) {
			if (bgTexture != null) {
				bgTexture.close();
			}
			if (bgTextureEnd != null) {
				bgTextureEnd.close();
			}
			if (bgProgressTexture != null) {
				bgProgressTexture.close();
			}
			if (bgProgressStart != null) {
				bgProgressStart.close();
			}
			texture.close();
			batch.close();
		}
	}

	public ValueListener getListener() {
		return listener;
	}

	public void setListener(ValueListener listener) {
		this.listener = listener;
	}

	@Override
	public String getUIName() {
		return "Progress";
	}

}
