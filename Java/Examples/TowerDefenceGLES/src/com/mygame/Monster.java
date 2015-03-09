package com.mygame;

import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public abstract class Monster extends AnimatedSprite {

	private Vector2f destinationPosition;
	private Vector2f direction;
	private MainGame game;
	private boolean isCurrentlyInMud;
	private Vector2f position;
	private Wave wave;

	public Monster(MainGame game, Wave wave, int startHitPoints, float speed,
			int value, String textureFile, int columnCount, int spriteCount,
			int spriteHeight, int spriteWidth) {
		super(game, textureFile, new Vector2f(1f, 200f), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		Vector2f startPoint = game.getGameplayScreen().getLevelSettings()
				.getStartPoint().cpy();

		this.setGridPosition(new Vector2f(startPoint.x, MathUtils
				.nextInt(-1, 2) + startPoint.y));
		this.Init(game, wave, value, startHitPoints, speed);
	}

	public Monster(MainGame game, Wave wave, int startHitPoints, float speed,
			int value, String textureFile, int columnCount, int spriteCount,
			int spriteHeight, int spriteWidth, Vector2f gridPosition) {
		super(game, textureFile, new Vector2f(1f, 200f), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		if (((gridPosition.x < 2) || (gridPosition.x > 0x10))
				|| ((gridPosition.y < 0) || (gridPosition.y > 0x12))) {
			throw new RuntimeException("gridPosition is out of bounds.");
		}
		if (game.getGameplayScreen().getDirs()[gridPosition.x()][gridPosition
				.y()] == null) {
			throw new RuntimeException("gridPosition is not valid.");
		}
		this.setGridPosition(gridPosition);
		this.Init(game, wave, value, startHitPoints, speed);
	}

	private java.util.ArrayList<Vector2f> GetMonsterSpawnOffsetPositions() {
		java.util.ArrayList<Vector2f> list = new java.util.ArrayList<Vector2f>();
		list.add(new Vector2f(1, 1));
		list.add(new Vector2f(1, -1));
		list.add(new Vector2f(-1, 1));
		list.add(new Vector2f(-1, -1));
		list.add(new Vector2f(0, 0));
		list.add(new Vector2f(0, 0));
		list.add(new Vector2f(0, 0));
		list.add(new Vector2f(0, 0));
		return list;
	}

	public Vector2f GetNextGridPoint(Vector2f gridPosition) {
		return this.game.getGameplayScreen().GetNextGridPoint(gridPosition);
	}

	public int GetVerticalTextureOffset() {
		return Utils.GetTextureOffsetY(Utils.GetAngle(this.direction),
				super.getSpriteHeight());
	}

	public final void Hit(int damage) {
		if (!this.getDead()) {
			this.setHitPoints(this.getHitPoints() - damage);
			this.getHealthBar().setCurrentPercent(
					(100 * this.getHitPoints()) / this.getStartHitPoints());
			if (this.getHitPoints() <= 0) {
				this.setDead(true);
				this.game.getGameplayScreen().getCash()
						.Increase(this.getValue());
				switch (this.getMonsterType()) {
				case Peasant:

					break;

				case Peon:

					break;

				case Berserker:

					break;

				case Chicken:

					break;

				case Doctor: {

					int num = 0;
					for (Vector2f point : this.GetMonsterSpawnOffsetPositions()) {
						try {
							MonsterPeon monster = new MonsterPeon(this.game,
									this.wave, this.getSpeed(),
									this.getStartHitPoints() / 4,
									this.getValue() / 2, new Vector2f(
											this.getGridPosition().x + point.x,
											this.getGridPosition().y + point.y));
							this.wave.AddMonster(monster);
							num++;
							if (num == 4) {
								break;
							}
							continue;
						} catch (RuntimeException e) {
							continue;
						}
					}
					break;
				}
				case Chieftain:

					break;
				}
				this.Remove();
				this.game.Components().add(
						new DieInfo(this.game, super.getDrawPosition(), this
								.getValue()));
				this.game.getGameplayScreen().MonsterDied(this);
			}
		}
	}

	private void Init(MainGame game, Wave wave, int value, int startHitPoints,
			float speed) {
		this.setStartHitPoints(startHitPoints);
		this.setHitPoints(startHitPoints);
		this.setSpeed(speed);
		this.setHealthBar(new ProgressBar(game, 20, true));
		this.getHealthBar().setDrawOrder(1);
		this.getHealthBar().setDrawBorder(true);
		this.setPosition(Utils.ConvertToPositionCoordinates(
				this.getGridPosition()).add(10f, 10f));
		this.destinationPosition = this.getPosition();
		this.game = game;
		this.wave = wave;
		this.setValue(value);
		game.Components().add(this.getHealthBar());
		game.Components().add(this);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
	}

	public final void Remove() {
		this.game.Components().remove(this.getHealthBar());
		this.wave.RemoveMonster(this);
		this.game.Components().remove(this);
	}

	public final void StartedSelection() {
		super.setObeyGameOpacity(false);
	}

	public final void StoppedSelection() {
		super.setObeyGameOpacity(true);
	}

	public final void Survived() {
		if (this.getHealthBar() != null) {
			this.game.Components().remove(this.getHealthBar());
		}
		this.wave.RemoveMonster(this);

		this.game.getGameplayScreen().MonsterSurvived(this);
		this.game.Components().remove(this);
		if (this.game.getGameplayScreen().getRemainingLives().Decrease() < 0) {
			this.game.getGameplayScreen().Lose();
		}
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (GameplayScreen.getGameState() == GameState.Started) {
			if (Utils.GetDistance(this.getPosition(), this.destinationPosition) < 1f) {
				if (this.game.getGameplayScreen().getLevelSettings()
						.getTowerBlockingGridCells()
						.contains(this.getGridPosition())
						&& (this.getMonsterType() != MonsterType.Chicken)) {
					this.isCurrentlyInMud = true;
				} else {
					this.isCurrentlyInMud = false;
				}
				this.setGridPosition(this.GetNextGridPoint(this
						.getGridPosition()));
				this.destinationPosition = Utils.ConvertToPositionCoordinates(
						this.getGridPosition()).add(10f, 10f);
				if (this.getGridPosition().x >= this.game.getGameplayScreen()
						.getLevelSettings().getEndPoint().x) {
					this.Survived();
					return;
				}
			}
			this.direction = Utils.GetDirection(this.getPosition(),
					this.destinationPosition);
			if (this.isCurrentlyInMud) {
				this.setPosition(this.getPosition().add(
						(this.direction.mul(this.getSpeed())).mul(0.55f)));
			} else {
				this.setPosition(this.getPosition().add(
						this.direction.mul(this.getSpeed())));
			}
			super.setVerticalTextureOffset(this.GetVerticalTextureOffset());
		}
	}

	private boolean privateDead;

	public final boolean getDead() {
		return privateDead;
	}

	public final void setDead(boolean value) {
		privateDead = value;
	}

	private Vector2f privateGridPosition;

	public final Vector2f getGridPosition() {
		return privateGridPosition;
	}

	public final void setGridPosition(Vector2f value) {
		privateGridPosition = value;
	}

	private ProgressBar privateHealthBar;

	public final ProgressBar getHealthBar() {
		return privateHealthBar;
	}

	public final void setHealthBar(ProgressBar value) {
		privateHealthBar = value;
	}

	private int privateHitPoints;

	public final int getHitPoints() {
		return privateHitPoints;
	}

	public final void setHitPoints(int value) {
		privateHitPoints = value;
	}

	private int privateLivingTime;

	public final int getLivingTime() {
		return privateLivingTime;
	}

	public final void setLivingTime(int value) {
		privateLivingTime = value;
	}

	private MonsterType privateMonsterType;

	public final MonsterType getMonsterType() {
		return privateMonsterType;
	}

	public final void setMonsterType(MonsterType value) {
		privateMonsterType = value;
	}

	public final Vector2f getPosition() {
		return this.position;
	}

	public final void setPosition(Vector2f value) {
		this.position = value;
		super.setDrawPosition(new Vector2f(value.x
				- (super.getSpriteWidth() / 2), value.y
				- (super.getSpriteHeight() / 2)));
		this.getHealthBar().setPosition(
				new Vector2f((value.x - (super.getSpriteWidth() / 2)) + 4f,
						value.y + 6f));
	}

	private float privateRadius;

	public final float getRadius() {
		return privateRadius;
	}

	public final void setRadius(float value) {
		privateRadius = value;
	}

	private int privateReservedHitPoints;

	public final int getReservedHitPoints() {
		return privateReservedHitPoints;
	}

	public final void setReservedHitPoints(int value) {
		privateReservedHitPoints = value;
	}

	public final void addReservedHitPoints(int value) {
		privateReservedHitPoints += value;
	}

	public final void removeReservedHitPoints(int value) {
		privateReservedHitPoints -= value;
	}

	private float privateSpeed;

	public final float getSpeed() {
		return privateSpeed;
	}

	public final void setSpeed(float value) {
		privateSpeed = value;
	}

	private int privateStartHitPoints;

	public final int getStartHitPoints() {
		return privateStartHitPoints;
	}

	public final void setStartHitPoints(int value) {
		privateStartHitPoints = value;
	}

	private int privateValue;

	public final int getValue() {
		return privateValue;
	}

	public final void setValue(int value) {
		privateValue = value;
	}
}