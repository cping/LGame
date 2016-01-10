package loon.component;

import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class LAnimation extends LRole {

	private int frame;
	private int tiles;
	private long time;
	private long curTime;
	private boolean bLoop;
	private boolean bAnimation;

	public LAnimation(LTexture iAnimation, float x, float y, int tiles,
			long time) {
		this(iAnimation, x, y, iAnimation.getWidth() / tiles, iAnimation
				.getHeight(), tiles, time);
	}

	public LAnimation(LTexture iAnimation, float x, float y, float width,
			float height, int tiles, long time) {
		super(iAnimation, x, y, width, height);

		setTiles(tiles);
		setTime(time);
		setBLoop(true);
		setBAnimation(true);

		init();
	}

	@Override
	public void init() {
		super.init();

		setFrame(0);
		setCurTime(0L);
	}

	public int getTiles() {
		return this.tiles;
	}

	public void setTiles(int tiles) {
		this.tiles = tiles;
	}

	public int getFrame() {
		return this.frame;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public long getCurTime() {
		return this.curTime;
	}

	public void setCurTime(long curTime) {
		this.curTime = curTime;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isBLoop() {
		return this.bLoop;
	}

	public void setBLoop(boolean bLoop) {
		this.bLoop = bLoop;
	}

	public boolean isBAnimation() {
		return this.bAnimation;
	}

	public void setBAnimation(boolean bAnimation) {
		this.bAnimation = bAnimation;
	}

	public void think(int time) {
		if (isBAnimation()) {
			setCurTime(getCurTime() + time);
			while (getCurTime() >= getTime()) {
				setCurTime(getCurTime() - getTime());
				setFrame(getFrame() + 1);
				if (getFrame() >= getTiles()) {
					setFrame(0);
					if (!isBLoop())
						setBAnimation(false);
				}
			}
		}
	}

	@Override
	public void render(GLEx g) {
		render(g, 0, 0);
	}

	@Override
	public void render(GLEx g, int x, int y) {
		if (super.isBVisible()) {
			if (super.getIBackground() != null) {
				g.draw(super.getIBackground(), (int) (getX() + x),
						(int) (getY() + y), (int) getWidth(),
						(int) getHeight(), (int) (getFrame() * getWidth()), 0,
						(int) getWidth(), (int) getHeight());
			}
			if (super.isBSelect()) {
				g.setColor(LColor.red);
				g.drawRect((int) (getX() - x), (int) (getY() - y),
						(int) (getWidth() - 1f), (int) (getHeight() - 1f));
			}
		}
	}

	@Override
	public String getUIName() {
		return "Animation";
	}
}
