package loon.component;

import loon.LTexture;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class LRole extends LComponent {

	private float x;
	private float y;
	private float startX;
	private float startY;
	private float vecX;
	private float vecY;

	private LTexture iBackground;
	private boolean bSelect;
	private boolean bVisible;
	private boolean bClose;
	private boolean bUse;
	private boolean bOpaque;

	public void init() {
		this.x = this.startX;
		this.y = this.startY;
		this.bSelect = false;
		this.bVisible = true;
		this.vecX = 0.0F;
		this.vecY = 0.0F;
		setBUse(false);
	}

	public LRole(LTexture texture, float x, float y, float width, float height) {
		super((int)x, (int)y, (int)width, (int)height);
		this.iBackground = texture;
		this.startX = x;
		this.startY = y;
		this.bOpaque = true;
		init();
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		render(g, x, y);
	}

	public float getStartX() {
		return this.startX;
	}

	public void setStartX(float startX) {
		this.startX = startX;
	}

	public float getStartY() {
		return this.startY;
	}

	public void setStartY(float startY) {
		this.startY = startY;
	}

	public boolean isBOpaque() {
		return this.bOpaque;
	}

	public void setBOpaque(boolean bOpaque) {
		this.bOpaque = bOpaque;
	}

	public boolean isBVisible() {
		return this.bVisible;
	}

	public void setBVisible(boolean bVisible) {
		this.bVisible = bVisible;
	}

	public boolean isBSelect() {
		return this.bSelect;
	}

	public void setBSelect(boolean bSelect) {
		this.bSelect = bSelect;
	}

	public boolean isBClose() {
		return this.bClose;
	}

	public void setBClose(boolean bClose) {
		this.bClose = bClose;
	}

	public boolean isBUse() {
		return this.bUse;
	}

	public void setBUse(boolean bUse) {
		this.bUse = bUse;
	}

	public float getVecY() {
		return this.vecY;
	}

	public void setVecY(float vecY) {
		this.vecY = vecY;
	}

	public float getVecX() {
		return this.vecX;
	}

	public void setVecX(float vecX) {
		this.vecX = vecX;
	}

	public LTexture getIBackground() {
		return this.iBackground;
	}

	public void setIBackground(LTexture background) {
		this.iBackground = background;
	}

	public float getXMiddle() {
		return this.x + this.getWidth() / 2f;
	}

	public float getYMiddle() {
		return this.y + this.getHeight() / 2f;
	}

	public boolean intersects(float x, float y, float width, float height) {
		if (getRec().intersects(x, y, width, height)) {
			if (isBOpaque()) {
				if (getIBackground() != null) {
					RectBox myRec = getRec();
					RectBox cut = (RectBox) myRec
							.createIntersection(new RectBox(x, y, width, height));
					for (int i = (int) cut.y; i < (int) (cut.y + cut.height); i++) {
						for (int j = (int) cut.x; j < (int) (cut.x + cut.width); j++) {
							if ((i - getY() < getHeight())
									&& (j - getX() < getWidth())
									&& (isOpaque(getIBackground().getImage()
											.getRGB((int) (j - myRec.x),
													(int) (i - myRec.y))))) {
								return true;
							}
						}
					}

					return false;
				}
				return true;
			}

			return true;
		}

		return false;
	}

	public boolean intersects(LRole entity) {
		if (getRec().intersects(entity.getRec())) {
			if (isBOpaque()) {
				return checkOpaqueColorCollisions(entity);
			}
			return true;
		}

		return false;
	}

	@Override
	public boolean contains(float x, float y, float width, float height) {
		return getRec().contains(x, y, width, height);
	}

	public boolean contains(LRole entity) {
		return getRec().contains(entity.getRec());
	}

	public RectBox getRec() {
		return new RectBox(getX(), getY(), getWidth(), getHeight());
	}

	public boolean checkOpaqueColorCollisions(LRole entity) {
		RectBox cut = (RectBox) getRec().createIntersection(entity.getRec());

		if ((cut.width < 1f) || (cut.height < 1f)) {
			return false;
		}

		RectBox sub_me = getSubRec(getRec(), cut);
		RectBox sub_him = getSubRec(entity.getRec(), cut);

		Image img_me = getIBackground().getImage().getSubImage((int) sub_me.x,
				(int) sub_me.y, (int) sub_me.width, (int) sub_me.height);
		Image img_him = entity
				.getIBackground()
				.getImage()
				.getSubImage((int) sub_him.x, (int) sub_him.y,
						(int) sub_him.width, (int) sub_him.height);

		for (int i = 0; i < img_me.getWidth(); i++) {
			for (int n = 0; n < img_him.getHeight(); n++) {
				int rgb1 = img_me.getRGB(i, n);
				int rgb2 = img_him.getRGB(i, n);

				if ((isOpaque(rgb1)) && (isOpaque(rgb2))) {
					return true;
				}
			}

		}
		if (img_me != null) {
			img_me.close();
		}
		if (img_him != null) {
			img_him.close();
		}
		return false;
	}

	private RectBox getSubRec(RectBox source, RectBox part) {
		RectBox sub = new RectBox();

		if (source.x > part.x)
			sub.x = 0.0F;
		else {
			part.x -= source.x;
		}

		if (source.y > part.y)
			sub.y = 0.0F;
		else {
			part.y -= source.y;
		}

		sub.width = part.width;
		sub.height = part.height;

		return sub;
	}

	private boolean isOpaque(int rgb) {
		int alpha = rgb >> 24 & 0xFF;

		if (alpha == 0) {
			return false;
		}

		return true;
	}

	@Override
	public void update(long elapsedTime) {

	}

	public void render(GLEx g, int x, int y) {
		if ((getIBackground() != null) && (isBVisible())) {
			g.draw(this.iBackground, (int) (getX() + x), (int) (getY() + y),
					(int) (getX() + x + getWidth()),
					(int) (getY() + y + getHeight()), 0, 0, (int) getWidth(),
					(int) getHeight());
			if (isBSelect()) {
				g.setColor(LColor.red);
				g.drawRect((int) (getX() + x), (int) (getY() + y),
						(int) (getWidth() - 1f), (int) (getHeight() - 1f));
			}
		}
	}

	public void render(GLEx g) {
		render(g, 0, 0);
	}

	@Override
	public String getUIName() {
		return "Role";
	}

}
