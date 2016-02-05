package org.test.zombiedefence;

import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class Bombardment {
	private int delayBeforeHit;
	private ScreenGameplay gameplay;
	public boolean isToBeDeleted;
	private int numHit;
	private int numHitTotal;
	private int t;

	public Bombardment(int numHitTotal, ScreenGameplay gameplay) {
		this.numHitTotal = numHitTotal;
		this.gameplay = gameplay;
		this.delayBeforeHit = 5;
		this.t = 0;
		this.numHit = 0;
	}

	public final void Draw(SpriteBatch batch) {
	}

	public final void Update() {
		if ((this.t != 0) && (this.t >= this.delayBeforeHit)) {
			if (this.numHit < this.numHitTotal) {
				if (MathUtils.random() < 0.15f) {
					this.gameplay.artilleryShellList
							.add(new ArtilleryShell(
									ScreenGameplay.t2DArtilleryShell,
									new Vector2f(
											((MathUtils.random()) * 600f) + 30f,
											-900f)));
					this.numHit++;
				}
			} else if (this.gameplay.artilleryShellList.size() == 0) {
				this.isToBeDeleted = true;
			}
		}
		this.t++;
	}
}