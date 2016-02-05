package org.test;

import loon.LTexture;
import loon.utils.MathUtils;

public class CAnimObject {

	private float animTime;

	private int count;
	private float currentFrameTime;
	private int currentloop;
	private float frameTime;
	private int loop;
	public boolean stopped;
	private LTexture[] texture;

	public CAnimObject() {
	}

	public CAnimObject(CAnimObject anim) {
		this.texture = anim.texture;
		this.count = anim.count;
		this.frameTime = anim.frameTime;
		this.animTime = anim.animTime;
		this.stopped = false;
		this.loop = anim.loop;
	}

	public final LTexture getTexture() {
		int index = MathUtils.floor((this.currentFrameTime / this.frameTime));
		return this.texture[index < texture.length ? index : 0];
	}

	public final void init(LTexture[] tex, int cnt, int fps, int loop) {
		this.texture = tex;
		this.count = cnt;
		this.frameTime = 1f / fps;
		this.animTime = this.frameTime * this.count;
		this.loop = loop;
		this.currentloop = 0;
	}

	public final void play(int loop) {
		this.reset();
		this.loop = loop;
	}

	public final void reset() {
		this.currentFrameTime = 0f;
		this.currentloop = 0;
		this.stopped = false;
	}

	public final void setFps(int fps) {
		this.frameTime = 1f / ((float) fps);
		this.animTime = this.frameTime * this.count;
	}

	public final void update(float time) {
		if ((this.loop <= -1) || (this.currentloop < this.loop)) {
			this.currentFrameTime += time;
			if (this.currentFrameTime > this.animTime) {
				if (this.loop > -1) {
					this.currentloop++;
					if (this.currentloop < this.loop) {
						this.currentFrameTime = 0f;
					} else {
						this.currentFrameTime = 0f;
						this.stopped = true;
					}
				} else {
					this.currentFrameTime = 0f;
				}
			}
		}
	}
}