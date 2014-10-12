package org.test;

import loon.core.geom.Vector2f;

public abstract class TowerMan extends AnimatedSprite {
	private Tower tower;

	public TowerMan(MainGame game, String textureFile, Tower tower,
			int spriteWidth, int spriteHeight) {
		super(game, textureFile, tower.getDrawPosition(), 8, 8, spriteHeight,
				spriteWidth, 1f);
		this.tower = tower;
		this.Init(tower.getDrawPosition());
	}

	public TowerMan(MainGame game, String textureFile, Tower tower,
			int spriteWidth, int spriteHeight, int spriteCount, int columnCount) {
		super(game, textureFile, tower.getDrawPosition(), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		this.tower = tower;
		this.Init(tower.getDrawPosition());
	}

	private void Init(Vector2f towerDrawPosition) {
		super.setDrawPosition(towerDrawPosition.add(10f, 4f));
		super.setOnlyPlayOnceFeature(true);
		this.UpdateThrowDirection(new Vector2f(-1f, 0f));
		super.setDrawOrder(this.tower.getDrawOrder() + 1);
	}

	public final void PlayAnimation() {
		super.setPlayNow(true);
	}

	public void UpdateThrowDirection(Vector2f direction) {
		super.setVerticalTextureOffset(Utils.GetTextureOffsetY(
				Utils.GetAngle(direction), super.getSpriteHeight()));
	}
}