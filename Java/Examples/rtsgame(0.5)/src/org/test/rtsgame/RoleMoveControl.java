package org.test.rtsgame;

import loon.LRelease;
import loon.LSystem;
import loon.action.sprite.SpriteBatch;
import loon.events.LTouchCollection;
import loon.events.LTouchLocation;
import loon.events.SysTouch;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

//战斗控制用类，操作我放角色移动行为
public class RoleMoveControl implements LRelease {

	private java.util.ArrayList<RoleControl> armies = new java.util.ArrayList<RoleControl>();
	private java.util.ArrayList<Vector2f> armyPosition = new java.util.ArrayList<Vector2f>();
	private Castle[] castles = new Castle[2];
	private float enemyWaitTime;
	private GameContent gameContent;
	private float MaxEnemyWaitTime = 2f;
	private RoleControl mouseOverArmy;
	private RoleControl selectedArmy;

	public RoleMoveControl(EntityManager screenManager, int levelIndex) {
		this.gameContent = screenManager.getGameContent();
		Levels.LevelDetails details = Levels.Load(levelIndex);
		Vector2f item = (new Vector2f(1.5f, 6.5f).mul(Tile.size));
		this.armyPosition.add(item);
		this.castles[0] = new Castle(gameContent, Shape.square, item);
		this.castles[1] = new Castle(gameContent, Shape.triangle,
				new Vector2f(LSystem.viewSize.width, LSystem.viewSize.height).sub(item));
		for (int i = 0; i < details.ArmyNumber.size(); i++) {
			for (int j = 0; j < details.ArmyNumber.get(i); j++) {
				Vector2f position = this.GetPosition().cpy();
				this.armies.add(new RoleControl(this.gameContent, Shape.square, RoleRank.forValue(i), position));
				this.armies.add(new RoleControl(this.gameContent, Shape.triangle, RoleRank.forValue(i),
						new Vector2f(LSystem.viewSize.width, LSystem.viewSize.height).sub(position)));
			}
		}
		this.enemyWaitTime = 0f;
		this.MaxEnemyWaitTime /= MathUtils.pow(10f, details.GameMode);
	}

	public final void close() {
	}

	public final void Draw(SpriteBatch spriteBatch, GameTime gameTime) {
		spriteBatch.draw(this.gameContent.background, 0, 0);
		if (this.mouseOverArmy != null) {
			this.mouseOverArmy.DrawMouseOver(spriteBatch);
		}
		if (this.selectedArmy != null) {
			this.selectedArmy.DrawMouseOver(spriteBatch);
		}
		for (int i = 0; i < this.armies.size(); i++) {
			this.armies.get(i).DrawPath(gameTime, spriteBatch);
		}

		for (int j = 0; j < this.armies.size(); j++) {
			this.armies.get(j).Draw(gameTime, spriteBatch);
		}
		for (Castle castle : this.castles) {
			castle.Draw(spriteBatch);
		}
	}

	private Vector2f GetPosition() {
		Vector2f vector;
		do {
			int num = this.gameContent.random.nextInt(5);
			int num2 = this.gameContent.random.nextInt(11);
			RectBox center = Tile.getBounds(num * Tile.size, num2 * Tile.size);
			vector = new Vector2f(center.getCenterX(), center.getCenterY());
		} while (this.armyPosition.contains(vector));
		this.armyPosition.add(vector);
		return vector;
	}

	public final void HandleInput() {

		Vector2f nextDestPosition = SysTouch.getLocation().cpy();
		LTouchCollection collection = SysTouch.getTouchState();
		this.mouseOverArmy = null;
		for (int i = 0; i < this.armies.size(); i++) {

			if (((this.armies.get(i).getMouseBounds().contains(nextDestPosition.x(), nextDestPosition.y())
					&& this.armies.get(i).isAlive())
					&& ((this.selectedArmy == null) && (this.armies.get(i).shape == Shape.square)))
					&& (collection.size() > 0)) {

				LTouchLocation location = collection.get(0);
				if (location.isDown()) {

					this.mouseOverArmy = this.armies.get(i);
					break;
				}
			}
		}

		if (collection.size() > 0) {
			LTouchLocation location2 = collection.get(0);
			if (!location2.isDown()) {
				LTouchLocation location3 = collection.get(0);
				if (!location3.isDrag()) {
					if (collection.size() > 0) {
						LTouchLocation location4 = collection.get(0);
						if (location4.isUp()) {
							this.selectedArmy = null;
						}
					}
					if ((this.selectedArmy == null) || this.selectedArmy.isAlive()) {
						return;
					}

					this.selectedArmy = null;
				}
			}
			if (SysTouch.isDown() && (this.mouseOverArmy != null)) {
				this.selectedArmy = this.mouseOverArmy;
				this.mouseOverArmy = null;
				this.selectedArmy.Reset();
			}

			if (this.selectedArmy != null) {

				this.selectedArmy.AddPosition(nextDestPosition);
			}
		}

	}

	public final void Update(GameTime gameTime) {
		this.enemyWaitTime += (float) gameTime.getElapsedGameTime();
		int num = (this.gameContent.random.nextInt(this.armies.size() / 2) * 2) + 1;
		boolean flag = true;
		for (int i = this.armies.size() - 1; i >= 0; i--) {
			this.armies.get(i).Update(gameTime);
			if (this.armies.get(i).isAlive()) {
				this.armies.get(i).HandleEnemy(this.armies);
				if (((this.armies.get(i).shape == Shape.triangle) && (this.enemyWaitTime > this.MaxEnemyWaitTime))
						&& (i == num)) {
					this.enemyWaitTime = 0f;
					this.armies.get(i).EnemyAI(this.castles[0].AttackRectangle);
				}
			}
			if ((this.armies.get(i).shape.equals(Shape.square))
					&& (this.castles[1].position.equals(this.armies.get(i).position))) {
				this.setIsLevelUp(true);
			}
			if ((this.armies.get(i).shape == Shape.triangle)
					&& (this.castles[0].position.equals(this.armies.get(i).position))) {
				this.setReloadLevel(true);
			}
			if (this.armies.get(i).isAlive() && (this.armies.get(i).shape.equals(Shape.square))) {
				flag = false;
			}
		}
		if (flag) {
			this.setReloadLevel(true);
		}
	}

	private boolean privateIsLevelUp;

	public final boolean getIsLevelUp() {
		return privateIsLevelUp;
	}

	public final void setIsLevelUp(boolean value) {
		privateIsLevelUp = value;
	}

	private boolean privateReloadLevel;

	public final boolean getReloadLevel() {
		return privateReloadLevel;
	}

	public final void setReloadLevel(boolean value) {
		privateReloadLevel = value;
	}

	private int privateScore;

	public final int getScore() {
		return privateScore;
	}

	public final void setScore(int value) {
		privateScore = value;
	}
}