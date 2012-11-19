package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;

public class Animation {
	public boolean Active;
	private LColor color;
	private int currentFrame;
	private RectBox destinationRect = new RectBox();
	private int elapsedTime;
	private int frameCount;
	public int FrameHeight;
	private int frameTime;
	public int FrameWidth;
	public boolean Looping;
	public Vector2f Position=new Vector2f();
	private float scale;
	private RectBox sourceRect = new RectBox();
	private LTexture spriteStrip;

	public final void Draw(SpriteBatch batch) {
		if (this.Active) {
			batch.draw(this.spriteStrip, this.destinationRect, this.sourceRect,
					this.color);
		}
	}

	public final void Initialize(LTexture texture, Vector2f position,
			int frameWidth, int frameHeight, int frameCount, int frametime,
			LColor color, float scale, boolean looping) {
		this.color = color;
		this.FrameWidth = frameWidth;
		this.FrameHeight = frameHeight;
		this.frameCount = frameCount;
		this.frameTime = frametime;
		this.scale = scale;
		this.Looping = looping;
		this.Position.set(position);
		this.spriteStrip = texture;
		this.elapsedTime = 0;
		this.currentFrame = 0;
		this.Active = true;
	}

	public final void Update(GameTime gameTime) {
		if (this.Active) {
			this.elapsedTime +=  gameTime.getMilliseconds();
			if (this.elapsedTime > this.frameTime) {
				this.currentFrame++;
				if (this.currentFrame == this.frameCount) {
					this.currentFrame = 0;
					if (!this.Looping) {
						this.Active = false;
					}
				}
				this.elapsedTime = 0;
			}
			this.sourceRect.setBounds(this.currentFrame * this.FrameWidth, 0,
					this.FrameWidth, this.FrameHeight);
			this.destinationRect.setBounds(( this.Position.x)
					- (( (this.FrameWidth * this.scale)) / 2),
					( this.Position.y)
							- (( (this.FrameHeight * this.scale)) / 2),
					 (this.FrameWidth * this.scale),
					 (this.FrameHeight * this.scale));
		}
	}
}